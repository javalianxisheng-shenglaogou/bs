-- 基于Spring Boot的多站点内容管理系统 - 内容相关表创建脚本

USE multi_site_cms;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 3. 内容管理模块（续）
-- =============================================

-- 3.2 内容表
DROP TABLE IF EXISTS contents;
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
    created_by VARCHAR(50) COMMENT '创建者',
    updated_by VARCHAR(50) COMMENT '更新者',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_site_id (site_id),
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_content_type (content_type),
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_publish_at (publish_at),
    INDEX idx_created_at (created_at),
    INDEX idx_content_site_status_publish (site_id, status, publish_at),
    INDEX idx_content_category_status (category_id, status, created_at),
    INDEX idx_content_author_created (author_id, created_at),
    INDEX idx_content_type_status (content_type, status),
    FULLTEXT idx_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';

-- =============================================
-- 4. 版本控制模块
-- =============================================

-- 4.1 内容版本表
DROP TABLE IF EXISTS content_versions;
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
    
    UNIQUE KEY uk_content_version (content_id, version_number),
    INDEX idx_content_id (content_id),
    INDEX idx_version_number (version_number),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    INDEX idx_content_versions_content_created (content_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容版本表';

-- =============================================
-- 5. 工作流模块
-- =============================================

-- 5.1 工作流定义表
DROP TABLE IF EXISTS workflows;
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

-- 5.2 工作流步骤表
DROP TABLE IF EXISTS workflow_steps;
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
    
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_step_order (step_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流步骤表';

-- 5.3 工作流实例表
DROP TABLE IF EXISTS workflow_instances;
CREATE TABLE workflow_instances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '实例ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    current_step_id BIGINT NULL COMMENT '当前步骤ID',
    status ENUM('RUNNING', 'COMPLETED', 'REJECTED', 'CANCELLED', 'TIMEOUT') DEFAULT 'RUNNING' COMMENT '状态',
    started_by BIGINT NOT NULL COMMENT '发起人ID',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_content_id (content_id),
    INDEX idx_status (status),
    INDEX idx_started_by (started_by),
    INDEX idx_workflow_instances_status (status, started_at),
    INDEX idx_workflow_instances_content (content_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流实例表';

-- 设置外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 添加外键约束
ALTER TABLE contents ADD CONSTRAINT fk_contents_site FOREIGN KEY (site_id) REFERENCES sites(id);
ALTER TABLE contents ADD CONSTRAINT fk_contents_author FOREIGN KEY (author_id) REFERENCES users(id);
ALTER TABLE contents ADD CONSTRAINT fk_contents_editor FOREIGN KEY (editor_id) REFERENCES users(id);
ALTER TABLE contents ADD CONSTRAINT fk_contents_category FOREIGN KEY (category_id) REFERENCES content_categories(id);

ALTER TABLE content_versions ADD CONSTRAINT fk_content_versions_content FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE;
ALTER TABLE content_versions ADD CONSTRAINT fk_content_versions_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE workflow_steps ADD CONSTRAINT fk_workflow_steps_workflow FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE;

ALTER TABLE workflow_instances ADD CONSTRAINT fk_workflow_instances_workflow FOREIGN KEY (workflow_id) REFERENCES workflows(id);
ALTER TABLE workflow_instances ADD CONSTRAINT fk_workflow_instances_content FOREIGN KEY (content_id) REFERENCES contents(id);
ALTER TABLE workflow_instances ADD CONSTRAINT fk_workflow_instances_current_step FOREIGN KEY (current_step_id) REFERENCES workflow_steps(id);
ALTER TABLE workflow_instances ADD CONSTRAINT fk_workflow_instances_started_by FOREIGN KEY (started_by) REFERENCES users(id);

SELECT 'Content and workflow tables created successfully!' as message;
