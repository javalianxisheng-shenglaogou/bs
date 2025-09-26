-- 基于Spring Boot的多站点内容管理系统 - 数据表创建脚本
-- 按照开发优先级顺序创建表

USE multi_site_cms;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 用户权限模块
-- =============================================

-- 1.1 用户表
DROP TABLE IF EXISTS users;
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
    created_by VARCHAR(50) COMMENT '创建者',
    updated_by VARCHAR(50) COMMENT '更新者',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1.2 角色表
DROP TABLE IF EXISTS roles;
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

-- 1.3 用户角色关联表
DROP TABLE IF EXISTS user_roles;
CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    site_id BIGINT NULL COMMENT '站点ID（站点级角色）',
    granted_by BIGINT NOT NULL COMMENT '授权人ID',
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    
    UNIQUE KEY uk_user_role_site (user_id, role_id, site_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_site_id (site_id),
    INDEX idx_user_roles_site (user_id, site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 2. 站点管理模块
-- =============================================

-- 2.1 站点表
DROP TABLE IF EXISTS sites;
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
    created_by VARCHAR(50) COMMENT '创建者',
    updated_by VARCHAR(50) COMMENT '更新者',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    
    INDEX idx_code (code),
    INDEX idx_domain (domain),
    INDEX idx_status (status),
    INDEX idx_owner_id (owner_id),
    INDEX idx_parent_site_id (parent_site_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点表';

-- 2.2 站点配置表
DROP TABLE IF EXISTS site_configs;
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
    INDEX idx_site_id (site_id),
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

-- =============================================
-- 3. 内容管理模块
-- =============================================

-- 3.1 内容分类表
DROP TABLE IF EXISTS content_categories;
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
    
    INDEX idx_site_id (site_id),
    INDEX idx_slug (slug),
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容分类表';

-- 设置外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 添加外键约束
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_granted_by FOREIGN KEY (granted_by) REFERENCES users(id);

ALTER TABLE sites ADD CONSTRAINT fk_sites_owner FOREIGN KEY (owner_id) REFERENCES users(id);
ALTER TABLE sites ADD CONSTRAINT fk_sites_parent FOREIGN KEY (parent_site_id) REFERENCES sites(id);

ALTER TABLE site_configs ADD CONSTRAINT fk_site_configs_site FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE;

ALTER TABLE content_categories ADD CONSTRAINT fk_content_categories_site FOREIGN KEY (site_id) REFERENCES sites(id);
ALTER TABLE content_categories ADD CONSTRAINT fk_content_categories_parent FOREIGN KEY (parent_id) REFERENCES content_categories(id);

SELECT 'Core tables created successfully!' as message;
