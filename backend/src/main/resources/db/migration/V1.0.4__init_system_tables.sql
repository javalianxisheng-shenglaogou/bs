-- =============================================
-- 系统管理模块表结构
-- =============================================

-- 1. 操作日志表
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    
    -- 操作信息
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_module VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    
    -- 请求信息
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    request_ip VARCHAR(50) COMMENT '请求IP',
    user_agent VARCHAR(500) COMMENT 'User Agent',
    
    -- 响应信息
    response_status INT COMMENT '响应状态码',
    response_data TEXT COMMENT '响应数据',
    error_message TEXT COMMENT '错误信息',
    
    -- 执行信息
    execution_time INT COMMENT '执行时长（毫秒）',
    
    -- 操作人信息
    user_id BIGINT COMMENT '操作人ID',
    username VARCHAR(50) COMMENT '操作人用户名',
    
    -- 业务关联
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id BIGINT COMMENT '业务ID',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_module (operation_module),
    INDEX idx_business (business_type, business_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 2. 系统配置表
CREATE TABLE system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    
    -- 配置项
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型',
    
    -- 配置分组
    config_group VARCHAR(50) COMMENT '配置分组',
    config_label VARCHAR(100) COMMENT '配置标签',
    description VARCHAR(500) COMMENT '配置描述',
    
    -- 配置属性
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开',
    is_encrypted TINYINT(1) DEFAULT 0 COMMENT '是否加密',
    is_system TINYINT(1) DEFAULT 0 COMMENT '是否系统配置',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    INDEX idx_config_group (config_group),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 3. 媒体文件表
CREATE TABLE media_files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    site_id BIGINT COMMENT '站点ID',
    
    -- 文件信息
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
    
    -- 文件属性
    file_type VARCHAR(50) NOT NULL COMMENT '文件类型',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_ext VARCHAR(20) COMMENT '文件扩展名',
    
    -- 图片属性
    width INT COMMENT '宽度',
    height INT COMMENT '高度',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    
    -- 存储信息
    storage_type ENUM('LOCAL', 'OSS', 'S3', 'CDN') DEFAULT 'LOCAL' COMMENT '存储类型',
    storage_path VARCHAR(500) COMMENT '存储路径',
    bucket_name VARCHAR(100) COMMENT '存储桶名称',
    
    -- 使用信息
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    
    -- 文件状态
    status ENUM('ACTIVE', 'ARCHIVED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '状态',
    
    -- 分类
    category VARCHAR(50) COMMENT '分类',
    tags JSON COMMENT '标签',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    INDEX idx_site_id (site_id),
    INDEX idx_file_type (file_type),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体文件表';

