-- =============================================
-- 站点管理模块表结构
-- =============================================

-- 1. 站点表
CREATE TABLE sites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '站点ID',
    
    -- 基本信息
    name VARCHAR(100) NOT NULL COMMENT '站点名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '站点代码',
    domain VARCHAR(255) NOT NULL COMMENT '站点域名',
    description TEXT COMMENT '站点描述',
    
    -- 站点配置
    logo_url VARCHAR(500) COMMENT 'Logo URL',
    favicon_url VARCHAR(500) COMMENT 'Favicon URL',
    language VARCHAR(10) DEFAULT 'zh_CN' COMMENT '默认语言',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区',
    
    -- 站点状态
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE' COMMENT '站点状态',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认站点',
    
    -- SEO配置
    seo_title VARCHAR(200) COMMENT 'SEO标题',
    seo_keywords VARCHAR(500) COMMENT 'SEO关键词',
    seo_description TEXT COMMENT 'SEO描述',
    
    -- 联系信息
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_address VARCHAR(500) COMMENT '联系地址',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    
    INDEX idx_code (code),
    INDEX idx_domain (domain),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点表';

-- 2. 站点配置表
CREATE TABLE site_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    site_id BIGINT NOT NULL COMMENT '站点ID',
    
    -- 配置项
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING/NUMBER/BOOLEAN/JSON',
    
    -- 配置分组
    config_group VARCHAR(50) COMMENT '配置分组',
    description VARCHAR(500) COMMENT '配置描述',
    
    -- 配置属性
    is_public TINYINT(1) DEFAULT 0 COMMENT '是否公开（前端可访问）',
    is_encrypted TINYINT(1) DEFAULT 0 COMMENT '是否加密存储',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_site_key (site_id, config_key),
    INDEX idx_site_id (site_id),
    INDEX idx_config_group (config_group),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

