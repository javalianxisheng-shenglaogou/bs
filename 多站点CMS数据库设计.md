# 基于Spring Boot的多站点内容管理系统数据库设计

## 数据库概述
- **项目名称**：基于Spring Boot的多站点内容管理系统的设计与实现
- **数据库类型**：MySQL 8.0
- **字符集**：utf8mb4
- **排序规则**：utf8mb4_unicode_ci
- **存储引擎**：InnoDB
- **设计原则**：支持多站点、内容共享、版本控制、工作流、多语言
- **性能策略**：基于数据库索引优化，无缓存依赖

---

## 核心表结构设计

### 1. 用户权限模块

#### 1.1 用户表（users）
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

#### 1.2 角色表（roles）
```sql
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    description VARCHAR(255) COMMENT '角色描述',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否系统角色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_code (code),
    INDEX idx_is_system (is_system)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
```

#### 1.3 用户角色关联表（user_roles）
```sql
CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    site_id BIGINT NULL COMMENT '站点ID（站点级角色）',
    granted_by BIGINT NOT NULL COMMENT '授权人ID',
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    
    UNIQUE KEY uk_user_role_site (user_id, role_id, site_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (granted_by) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_site_id (site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
```

### 2. 站点管理模块

#### 2.1 站点表（sites）
```sql
CREATE TABLE sites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '站点ID',
    name VARCHAR(100) NOT NULL COMMENT '站点名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '站点代码',
    domain VARCHAR(255) COMMENT '主域名',
    description TEXT COMMENT '站点描述',
    logo_url VARCHAR(500) COMMENT 'Logo URL',
    favicon_url VARCHAR(500) COMMENT 'Favicon URL',
    theme VARCHAR(50) DEFAULT 'default' COMMENT '主题',
    language VARCHAR(10) DEFAULT 'zh-CN' COMMENT '默认语言',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE' COMMENT '站点状态',
    owner_id BIGINT NOT NULL COMMENT '站点所有者ID',
    parent_site_id BIGINT NULL COMMENT '父站点ID（支持站点层级）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    FOREIGN KEY (owner_id) REFERENCES users(id),
    FOREIGN KEY (parent_site_id) REFERENCES sites(id),
    INDEX idx_code (code),
    INDEX idx_domain (domain),
    INDEX idx_status (status),
    INDEX idx_owner_id (owner_id),
    INDEX idx_parent_site_id (parent_site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点表';
```

#### 2.2 站点配置表（site_configs）
```sql
CREATE TABLE site_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    site_id BIGINT NOT NULL COMMENT '站点ID',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') DEFAULT 'STRING' COMMENT '配置类型',
    description VARCHAR(255) COMMENT '配置描述',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_site_config (site_id, config_key),
    FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE,
    INDEX idx_site_id (site_id),
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';
```

### 3. 内容管理模块

#### 3.1 内容表（contents）
```sql
CREATE TABLE contents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '内容ID',
    site_id BIGINT NOT NULL COMMENT '所属站点ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    slug VARCHAR(255) COMMENT 'URL别名',
    summary TEXT COMMENT '摘要',
    content LONGTEXT COMMENT '内容',
    content_type ENUM('ARTICLE', 'PAGE', 'MEDIA') DEFAULT 'ARTICLE' COMMENT '内容类型',
    status ENUM('DRAFT', 'PENDING', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT' COMMENT '状态',
    publish_at TIMESTAMP NULL COMMENT '发布时间',
    featured_image VARCHAR(500) COMMENT '特色图片',
    seo_title VARCHAR(255) COMMENT 'SEO标题',
    seo_description VARCHAR(500) COMMENT 'SEO描述',
    seo_keywords VARCHAR(500) COMMENT 'SEO关键词',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    comment_count INT DEFAULT 0 COMMENT '评论次数',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    editor_id BIGINT NULL COMMENT '最后编辑者ID',
    category_id BIGINT NULL COMMENT '分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    allow_comment BOOLEAN DEFAULT TRUE COMMENT '是否允许评论',
    template VARCHAR(100) COMMENT '模板名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    FOREIGN KEY (site_id) REFERENCES sites(id),
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (editor_id) REFERENCES users(id),
    INDEX idx_site_id (site_id),
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_content_type (content_type),
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_publish_at (publish_at),
    INDEX idx_created_at (created_at),
    FULLTEXT idx_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';
```

#### 3.2 内容分类表（content_categories）
```sql
CREATE TABLE content_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    site_id BIGINT NOT NULL COMMENT '所属站点ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    slug VARCHAR(100) COMMENT 'URL别名',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT NULL COMMENT '父分类ID',
    level INT DEFAULT 1 COMMENT '层级',
    path VARCHAR(500) COMMENT '分类路径',
    icon VARCHAR(100) COMMENT '图标',
    cover_image VARCHAR(500) COMMENT '封面图片',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_nav BOOLEAN DEFAULT FALSE COMMENT '是否显示在导航',
    seo_title VARCHAR(255) COMMENT 'SEO标题',
    seo_description VARCHAR(500) COMMENT 'SEO描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    FOREIGN KEY (site_id) REFERENCES sites(id),
    FOREIGN KEY (parent_id) REFERENCES content_categories(id),
    INDEX idx_site_id (site_id),
    INDEX idx_slug (slug),
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容分类表';
```

### 4. 版本控制模块

#### 4.1 内容版本表（content_versions）
```sql
CREATE TABLE content_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '版本ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    version_number INT NOT NULL COMMENT '版本号',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content LONGTEXT COMMENT '内容',
    content_snapshot JSON COMMENT '内容快照（完整数据）',
    change_type ENUM('CREATE', 'UPDATE', 'DELETE', 'PUBLISH', 'UNPUBLISH') COMMENT '变更类型',
    change_description VARCHAR(500) COMMENT '变更描述',
    changed_fields JSON COMMENT '变更字段',
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id),
    UNIQUE KEY uk_content_version (content_id, version_number),
    INDEX idx_content_id (content_id),
    INDEX idx_version_number (version_number),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容版本表';
```

### 5. 工作流模块

#### 5.1 工作流定义表（workflows）
```sql
CREATE TABLE workflows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工作流ID',
    name VARCHAR(100) NOT NULL COMMENT '工作流名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '工作流代码',
    description VARCHAR(500) COMMENT '描述',
    trigger_type ENUM('MANUAL', 'AUTO') DEFAULT 'MANUAL' COMMENT '触发类型',
    trigger_condition JSON COMMENT '触发条件',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_code (code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流定义表';
```

#### 5.2 工作流步骤表（workflow_steps）
```sql
CREATE TABLE workflow_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '步骤ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    name VARCHAR(100) NOT NULL COMMENT '步骤名称',
    step_order INT NOT NULL COMMENT '步骤顺序',
    step_type ENUM('APPROVAL', 'NOTIFICATION', 'SCRIPT') DEFAULT 'APPROVAL' COMMENT '步骤类型',
    assignee_type ENUM('USER', 'ROLE', 'GROUP') COMMENT '指派类型',
    assignee_value VARCHAR(100) COMMENT '指派值',
    is_required BOOLEAN DEFAULT TRUE COMMENT '是否必须',
    timeout_hours INT DEFAULT 24 COMMENT '超时时间（小时）',
    timeout_action ENUM('AUTO_APPROVE', 'AUTO_REJECT', 'ESCALATE') DEFAULT 'ESCALATE' COMMENT '超时处理',
    condition_expression VARCHAR(500) COMMENT '条件表达式',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE,
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_step_order (step_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流步骤表';
```

#### 5.3 工作流实例表（workflow_instances）
```sql
CREATE TABLE workflow_instances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '实例ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    current_step_id BIGINT NULL COMMENT '当前步骤ID',
    status ENUM('RUNNING', 'COMPLETED', 'REJECTED', 'CANCELLED', 'TIMEOUT') DEFAULT 'RUNNING' COMMENT '状态',
    started_by BIGINT NOT NULL COMMENT '发起人ID',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    FOREIGN KEY (workflow_id) REFERENCES workflows(id),
    FOREIGN KEY (content_id) REFERENCES contents(id),
    FOREIGN KEY (current_step_id) REFERENCES workflow_steps(id),
    FOREIGN KEY (started_by) REFERENCES users(id),
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_content_id (content_id),
    INDEX idx_status (status),
    INDEX idx_started_by (started_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流实例表';
```

### 6. 内容共享模块

#### 6.1 内容引用表（content_references）
```sql
CREATE TABLE content_references (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '引用ID',
    source_content_id BIGINT NOT NULL COMMENT '源内容ID',
    target_site_id BIGINT NOT NULL COMMENT '目标站点ID',
    reference_type ENUM('COPY', 'LINK', 'SYNC') DEFAULT 'LINK' COMMENT '引用类型',
    sync_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用同步',
    sync_fields JSON COMMENT '同步字段配置',
    last_sync_at TIMESTAMP NULL COMMENT '最后同步时间',
    sync_status ENUM('SUCCESS', 'FAILED', 'PENDING') COMMENT '同步状态',
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (source_content_id) REFERENCES contents(id) ON DELETE CASCADE,
    FOREIGN KEY (target_site_id) REFERENCES sites(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id),
    UNIQUE KEY uk_source_target (source_content_id, target_site_id),
    INDEX idx_source_content_id (source_content_id),
    INDEX idx_target_site_id (target_site_id),
    INDEX idx_sync_status (sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容引用表';
```

### 7. 多语言模块

#### 7.1 语言配置表（languages）
```sql
CREATE TABLE languages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '语言ID',
    code VARCHAR(10) NOT NULL UNIQUE COMMENT '语言代码',
    name VARCHAR(50) NOT NULL COMMENT '语言名称',
    native_name VARCHAR(50) NOT NULL COMMENT '本地名称',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否默认语言',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_code (code),
    INDEX idx_is_default (is_default),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='语言配置表';
```

#### 7.2 内容翻译表（content_translations）
```sql
CREATE TABLE content_translations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '翻译ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    language_code VARCHAR(10) NOT NULL COMMENT '语言代码',
    title VARCHAR(255) COMMENT '翻译标题',
    summary TEXT COMMENT '翻译摘要',
    content LONGTEXT COMMENT '翻译内容',
    seo_title VARCHAR(255) COMMENT 'SEO标题',
    seo_description VARCHAR(500) COMMENT 'SEO描述',
    status ENUM('PENDING', 'TRANSLATING', 'COMPLETED', 'REVIEWED') DEFAULT 'PENDING' COMMENT '翻译状态',
    translator_id BIGINT NULL COMMENT '翻译者ID',
    reviewer_id BIGINT NULL COMMENT '审核者ID',
    translated_at TIMESTAMP NULL COMMENT '翻译完成时间',
    reviewed_at TIMESTAMP NULL COMMENT '审核时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE,
    FOREIGN KEY (language_code) REFERENCES languages(code),
    FOREIGN KEY (translator_id) REFERENCES users(id),
    FOREIGN KEY (reviewer_id) REFERENCES users(id),
    UNIQUE KEY uk_content_language (content_id, language_code),
    INDEX idx_content_id (content_id),
    INDEX idx_language_code (language_code),
    INDEX idx_status (status),
    INDEX idx_translator_id (translator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容翻译表';
```

---

## 索引优化策略

### 1. 查询优化索引
```sql
-- 内容查询优化（替代缓存的高性能索引）
CREATE INDEX idx_content_site_status_publish ON contents(site_id, status, publish_at);
CREATE INDEX idx_content_category_status ON contents(category_id, status, created_at);
CREATE INDEX idx_content_author_created ON contents(author_id, created_at);
CREATE INDEX idx_content_type_status ON contents(content_type, status);

-- 用户权限查询优化
CREATE INDEX idx_user_roles_site ON user_roles(user_id, site_id);
CREATE INDEX idx_user_status_created ON users(status, created_at);

-- 工作流查询优化
CREATE INDEX idx_workflow_instances_status ON workflow_instances(status, started_at);
CREATE INDEX idx_workflow_instances_content ON workflow_instances(content_id, status);

-- 内容同步查询优化
CREATE INDEX idx_content_references_source ON content_references(source_content_id, sync_enabled);
CREATE INDEX idx_content_references_target ON content_references(target_site_id, sync_status);

-- 版本控制查询优化
CREATE INDEX idx_content_versions_content_created ON content_versions(content_id, created_at);

-- 多语言查询优化
CREATE INDEX idx_content_translations_content_lang ON content_translations(content_id, language_code);
CREATE INDEX idx_content_translations_status ON content_translations(status, translator_id);
```

### 2. 分区策略
```sql
-- 内容表按时间分区（可选）
ALTER TABLE contents PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

---

## 数据初始化脚本

### 1. 基础角色数据
```sql
INSERT INTO roles (name, code, description, is_system) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员', TRUE),
('站点管理员', 'SITE_ADMIN', '站点管理员', TRUE),
('编辑者', 'EDITOR', '内容编辑者', TRUE),
('审核者', 'REVIEWER', '内容审核者', TRUE),
('翻译者', 'TRANSLATOR', '内容翻译者', TRUE),
('访客', 'GUEST', '访客用户', TRUE);
```

### 2. 基础语言数据
```sql
INSERT INTO languages (code, name, native_name, is_default, is_active, sort_order) VALUES
('zh-CN', '简体中文', '简体中文', TRUE, TRUE, 1),
('en-US', 'English', 'English', FALSE, TRUE, 2),
('ja-JP', '日本語', '日本語', FALSE, TRUE, 3),
('ko-KR', '한국어', '한국어', FALSE, TRUE, 4);
```

这个数据库设计支持多站点CMS系统的所有核心功能，包括多站点管理、内容共享、版本控制、工作流和多语言支持。设计考虑了性能优化、数据一致性和扩展性。
