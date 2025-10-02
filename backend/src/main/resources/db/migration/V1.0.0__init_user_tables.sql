-- =============================================
-- 用户权限模块表结构
-- =============================================

-- 1. 用户表
CREATE TABLE users (
    -- 基础字段
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    mobile VARCHAR(20) UNIQUE COMMENT '手机号',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    
    -- 个人信息
    nickname VARCHAR(50) COMMENT '昵称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    gender ENUM('MALE', 'FEMALE', 'OTHER') COMMENT '性别',
    birthday DATE COMMENT '生日',
    bio TEXT COMMENT '个人简介',
    
    -- 状态信息
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED', 'PENDING') DEFAULT 'PENDING' COMMENT '用户状态',
    email_verified TINYINT(1) DEFAULT 0 COMMENT '邮箱是否验证',
    mobile_verified TINYINT(1) DEFAULT 0 COMMENT '手机是否验证',
    
    -- 登录信息
    last_login_at DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    failed_login_count INT DEFAULT 0 COMMENT '失败登录次数',
    locked_until DATETIME COMMENT '锁定截止时间',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    
    -- 索引
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_mobile (mobile),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 角色表
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    description VARCHAR(500) COMMENT '角色描述',
    
    -- 角色属性
    level INT NOT NULL DEFAULT 0 COMMENT '角色级别',
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否系统角色',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认角色',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    
    INDEX idx_code (code),
    INDEX idx_level (level),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 权限表
CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    name VARCHAR(100) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    description VARCHAR(500) COMMENT '权限描述',
    
    -- 权限分类
    module VARCHAR(50) NOT NULL COMMENT '所属模块',
    resource VARCHAR(100) NOT NULL COMMENT '资源标识',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    
    -- 权限属性
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否系统权限',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_resource_action (resource, action),
    INDEX idx_module (module),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 4. 用户角色关联表
CREATE TABLE user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    site_id BIGINT COMMENT '站点ID（站点级角色）',
    
    -- 授权信息
    granted_by BIGINT NOT NULL COMMENT '授权人ID',
    granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    expires_at DATETIME COMMENT '过期时间',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_user_role_site (user_id, role_id, site_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_site_id (site_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 5. 角色权限关联表
CREATE TABLE role_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

