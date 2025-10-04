-- =====================================================
-- Multi-Site CMS Database Initialization Script
-- Version: 1.0.0
-- Description: Complete database schema and initial data
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS multi_site_cms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE multi_site_cms;

-- =====================================================
-- 1. User Management Tables
-- =====================================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'User ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Password hash',
    email VARCHAR(100) UNIQUE COMMENT 'Email',
    phone VARCHAR(20) COMMENT 'Phone number',
    nickname VARCHAR(50) COMMENT 'Nickname',
    avatar_url VARCHAR(500) COMMENT 'Avatar URL',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Status: ACTIVE, INACTIVE, LOCKED',
    last_login_at DATETIME COMMENT 'Last login time',
    last_login_ip VARCHAR(50) COMMENT 'Last login IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Creator ID',
    updated_by BIGINT COMMENT 'Updater ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    version INT NOT NULL DEFAULT 0 COMMENT 'Version for optimistic locking',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Users table';

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Role ID',
    name VARCHAR(50) NOT NULL COMMENT 'Role name',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role code',
    description VARCHAR(200) COMMENT 'Description',
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'System role flag',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Creator ID',
    updated_by BIGINT COMMENT 'Updater ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Roles table';

-- Permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Permission ID',
    name VARCHAR(100) NOT NULL COMMENT 'Permission name',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT 'Permission code',
    description VARCHAR(200) COMMENT 'Description',
    module VARCHAR(50) NOT NULL COMMENT 'Module',
    resource VARCHAR(50) NOT NULL COMMENT 'Resource',
    action VARCHAR(50) NOT NULL COMMENT 'Action',
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'System permission flag',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Creator ID',
    updated_by BIGINT COMMENT 'Updater ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    INDEX idx_code (code),
    INDEX idx_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Permissions table';

-- User-Role relationship table
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    role_id BIGINT NOT NULL COMMENT 'Role ID',
    granted_by BIGINT COMMENT 'Granted by user ID',
    granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Granted time',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User-Role relationship table';

-- Role-Permission relationship table
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT 'Role ID',
    permission_id BIGINT NOT NULL COMMENT 'Permission ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role-Permission relationship table';

-- =====================================================
-- 2. Site Management Tables
-- =====================================================

-- Sites table
CREATE TABLE IF NOT EXISTS sites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Site ID',
    name VARCHAR(100) NOT NULL COMMENT 'Site name',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT 'Site code',
    domain VARCHAR(200) COMMENT 'Domain',
    description VARCHAR(500) COMMENT 'Description',
    logo_url VARCHAR(500) COMMENT 'Logo URL',
    favicon_url VARCHAR(500) COMMENT 'Favicon URL',
    language VARCHAR(20) DEFAULT 'zh-CN' COMMENT 'Language',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT 'Timezone',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Status: ACTIVE, INACTIVE',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Default site flag',
    seo_title VARCHAR(200) COMMENT 'SEO title',
    seo_keywords VARCHAR(500) COMMENT 'SEO keywords',
    seo_description VARCHAR(1000) COMMENT 'SEO description',
    contact_email VARCHAR(100) COMMENT 'Contact email',
    contact_phone VARCHAR(50) COMMENT 'Contact phone',
    contact_address VARCHAR(500) COMMENT 'Contact address',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Creator ID',
    updated_by BIGINT COMMENT 'Updater ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    version INT NOT NULL DEFAULT 0 COMMENT 'Version for optimistic locking',
    INDEX idx_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Sites table';

-- =====================================================
-- 3. Content Management Tables
-- =====================================================

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Category ID',
    site_id BIGINT NOT NULL COMMENT 'Site ID',
    parent_id BIGINT COMMENT 'Parent category ID',
    name VARCHAR(100) NOT NULL COMMENT 'Category name',
    code VARCHAR(50) NOT NULL COMMENT 'Category code',
    description VARCHAR(500) COMMENT 'Description',
    icon VARCHAR(200) COMMENT 'Icon',
    sort_order INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
    level INT NOT NULL DEFAULT 1 COMMENT 'Level',
    path VARCHAR(500) NOT NULL COMMENT 'Path',
    is_visible TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Visible flag',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Creator ID',
    updated_by BIGINT COMMENT 'Updater ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    version INT NOT NULL DEFAULT 0 COMMENT 'Version for optimistic locking',
    UNIQUE KEY uk_site_code (site_id, code),
    INDEX idx_site_id (site_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Categories table';

-- Contents table
CREATE TABLE IF NOT EXISTS contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Content ID',
    site_id BIGINT NOT NULL COMMENT 'Site ID',
    category_id BIGINT COMMENT 'Category ID',
    title VARCHAR(200) NOT NULL COMMENT 'Title',
    slug VARCHAR(200) COMMENT 'URL slug',
    summary TEXT COMMENT 'Summary',
    content LONGTEXT COMMENT 'Content',
    cover_image VARCHAR(500) COMMENT 'Cover image URL',
    content_type VARCHAR(20) NOT NULL DEFAULT 'ARTICLE' COMMENT 'Content type',
    author_id BIGINT COMMENT 'Author ID',
    author_name VARCHAR(50) COMMENT 'Author name',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT 'Status: DRAFT, PUBLISHED, ARCHIVED',
    is_top TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Top flag',
    is_recommend TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Recommend flag',
    view_count BIGINT NOT NULL DEFAULT 0 COMMENT 'View count',
    published_at DATETIME COMMENT 'Published time',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    created_by BIGINT COMMENT 'Creator ID',
    updated_by BIGINT COMMENT 'Updater ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag',
    version INT NOT NULL DEFAULT 0 COMMENT 'Version for optimistic locking',
    UNIQUE KEY uk_site_slug (site_id, slug),
    INDEX idx_site_id (site_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_author_id (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Contents table';

-- =====================================================
-- 4. System Tables
-- =====================================================

-- Flyway schema history (managed by Flyway)
CREATE TABLE IF NOT EXISTS flyway_schema_history (
    installed_rank INT NOT NULL,
    version VARCHAR(50),
    description VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INT,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_time INT NOT NULL,
    success TINYINT(1) NOT NULL,
    PRIMARY KEY (installed_rank),
    INDEX flyway_schema_history_s_idx (success)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Flyway schema history';

-- =====================================================
-- 5. Initial Data
-- =====================================================

-- Insert default admin user (password: admin123)
INSERT INTO users (id, username, password_hash, email, nickname, status, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', 'Administrator', 'ACTIVE', NOW(), NOW());

-- Insert default roles
INSERT INTO roles (id, name, code, description, is_system, sort_order, created_at, updated_at) VALUES
(1, 'Administrator', 'ADMIN', 'System administrator with full permissions', 1, 1, NOW(), NOW()),
(2, 'Editor', 'EDITOR', 'Content editor', 1, 2, NOW(), NOW()),
(3, 'Viewer', 'VIEWER', 'Read-only viewer', 1, 3, NOW(), NOW());

-- Insert permissions
INSERT INTO permissions (code, name, description, module, resource, action, is_system, sort_order, created_at, updated_at) VALUES
-- User permissions
('user:list', 'View user list', 'View user list', 'user', 'user', 'list', 1, 1, NOW(), NOW()),
('user:view', 'View user details', 'View user details', 'user', 'user', 'view', 1, 2, NOW(), NOW()),
('user:create', 'Create user', 'Create new user', 'user', 'user', 'create', 1, 3, NOW(), NOW()),
('user:update', 'Update user', 'Update user information', 'user', 'user', 'update', 1, 4, NOW(), NOW()),
('user:delete', 'Delete user', 'Delete user', 'user', 'user', 'delete', 1, 5, NOW(), NOW()),
-- Site permissions
('site:list', 'View site list', 'View site list', 'site', 'site', 'list', 1, 10, NOW(), NOW()),
('site:view', 'View site details', 'View site details', 'site', 'site', 'view', 1, 11, NOW(), NOW()),
('site:create', 'Create site', 'Create new site', 'site', 'site', 'create', 1, 12, NOW(), NOW()),
('site:update', 'Update site', 'Update site information', 'site', 'site', 'update', 1, 13, NOW(), NOW()),
('site:delete', 'Delete site', 'Delete site', 'site', 'site', 'delete', 1, 14, NOW(), NOW()),
('site:config', 'Configure site', 'Configure site settings', 'site', 'site', 'config', 1, 15, NOW(), NOW()),
-- Content permissions
('content:list', 'View content list', 'View content list', 'content', 'content', 'list', 1, 20, NOW(), NOW()),
('content:view', 'View content details', 'View content details', 'content', 'content', 'view', 1, 21, NOW(), NOW()),
('content:create', 'Create content', 'Create new content', 'content', 'content', 'create', 1, 22, NOW(), NOW()),
('content:update', 'Update content', 'Update content', 'content', 'content', 'update', 1, 23, NOW(), NOW()),
('content:delete', 'Delete content', 'Delete content', 'content', 'content', 'delete', 1, 24, NOW(), NOW()),
('content:publish', 'Publish content', 'Publish content', 'content', 'content', 'publish', 1, 25, NOW(), NOW()),
-- Category permissions
('category:list', 'View category list', 'View category list', 'category', 'category', 'list', 1, 30, NOW(), NOW()),
('category:view', 'View category details', 'View category details', 'category', 'category', 'view', 1, 31, NOW(), NOW()),
('category:create', 'Create category', 'Create new category', 'category', 'category', 'create', 1, 32, NOW(), NOW()),
('category:update', 'Update category', 'Update category', 'category', 'category', 'update', 1, 33, NOW(), NOW()),
('category:delete', 'Delete category', 'Delete category', 'category', 'category', 'delete', 1, 34, NOW(), NOW()),
-- Log permissions
('log:list', 'View log list', 'View system log list', 'log', 'log', 'list', 1, 80, NOW(), NOW()),
('log:view', 'View log details', 'View log details', 'log', 'log', 'view', 1, 81, NOW(), NOW()),
('log:delete', 'Delete logs', 'Delete system logs', 'log', 'log', 'delete', 1, 82, NOW(), NOW());

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id, granted_by, granted_at) VALUES
(1, 1, 1, NOW());

-- Assign all permissions to admin role
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions WHERE is_system = 1;

-- Insert default sites
INSERT INTO sites (id, name, code, domain, description, language, timezone, status, is_default, created_at, updated_at, created_by) VALUES
(1, 'Main Site', 'main', 'www.example.com', 'Main website', 'zh-CN', 'Asia/Shanghai', 'ACTIVE', 1, NOW(), NOW(), 1),
(2, 'News Site', 'news', 'news.example.com', 'News portal', 'zh-CN', 'Asia/Shanghai', 'ACTIVE', 0, NOW(), NOW(), 1),
(3, 'Blog Site', 'blog', 'blog.example.com', 'Personal blog', 'zh-CN', 'Asia/Shanghai', 'ACTIVE', 0, NOW(), NOW(), 1);

-- Insert default categories
INSERT INTO categories (site_id, name, code, description, sort_order, level, path, is_visible, created_at, updated_at, created_by) VALUES
(1, 'News', 'news', 'News category', 1, 1, '/1', 1, NOW(), NOW(), 1),
(1, 'Products', 'products', 'Products category', 2, 1, '/2', 1, NOW(), NOW(), 1),
(1, 'About', 'about', 'About us category', 3, 1, '/3', 1, NOW(), NOW(), 1);

-- Insert sample contents
INSERT INTO contents (site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, published_at, created_at, updated_at, created_by) VALUES
(1, 1, 'Welcome to Multi-Site CMS', 'welcome', 'This is a sample article', '<p>Welcome to our Multi-Site CMS system!</p>', 'ARTICLE', 1, 'admin', 'PUBLISHED', NOW(), NOW(), NOW(), 1),
(1, 1, 'Getting Started Guide', 'getting-started', 'Learn how to use the CMS', '<p>This guide will help you get started with the CMS.</p>', 'ARTICLE', 1, 'admin', 'PUBLISHED', NOW(), NOW(), NOW(), 1),
(1, 2, 'Product Introduction', 'product-intro', 'Introduction to our products', '<p>Learn about our amazing products.</p>', 'ARTICLE', 1, 'admin', 'DRAFT', NULL, NOW(), NOW(), 1);

-- =====================================================
-- End of initialization script
-- =====================================================

