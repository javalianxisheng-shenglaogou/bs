-- 基于Spring Boot的多站点内容管理系统 - 内容共享和多语言表创建脚本

USE multi_site_cms;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 6. 内容共享模块
-- =============================================

-- 6.1 内容引用表
DROP TABLE IF EXISTS content_references;
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
    
    UNIQUE KEY uk_source_target (source_content_id, target_site_id),
    INDEX idx_source_content_id (source_content_id),
    INDEX idx_target_site_id (target_site_id),
    INDEX idx_sync_status (sync_status),
    INDEX idx_content_references_source (source_content_id, sync_enabled),
    INDEX idx_content_references_target (target_site_id, sync_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容引用表';

-- 6.2 内容同步日志表
DROP TABLE IF EXISTS content_sync_logs;
CREATE TABLE content_sync_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    content_reference_id BIGINT NOT NULL COMMENT '内容引用ID',
    action ENUM('CREATE', 'UPDATE', 'DELETE') COMMENT '同步动作',
    sync_status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING' COMMENT '同步状态',
    error_message TEXT COMMENT '错误信息',
    sync_data JSON COMMENT '同步数据',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    INDEX idx_content_reference_id (content_reference_id),
    INDEX idx_sync_status (sync_status),
    INDEX idx_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容同步日志表';

-- =============================================
-- 7. 多语言模块
-- =============================================

-- 7.1 语言配置表
DROP TABLE IF EXISTS languages;
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

-- 7.2 内容翻译表
DROP TABLE IF EXISTS content_translations;
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
    
    UNIQUE KEY uk_content_language (content_id, language_code),
    INDEX idx_content_id (content_id),
    INDEX idx_language_code (language_code),
    INDEX idx_status (status),
    INDEX idx_translator_id (translator_id),
    INDEX idx_content_translations_content_lang (content_id, language_code),
    INDEX idx_content_translations_status (status, translator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容翻译表';

-- =============================================
-- 8. 系统日志表（可选）
-- =============================================

-- 8.1 操作日志表
DROP TABLE IF EXISTS operation_logs;
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT NULL COMMENT '操作用户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_module VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation_description VARCHAR(500) COMMENT '操作描述',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_status INT COMMENT '响应状态码',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    execution_time INT COMMENT '执行时间（毫秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_module (operation_module),
    INDEX idx_created_at (created_at),
    INDEX idx_ip_address (ip_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 设置外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 添加外键约束
ALTER TABLE content_references ADD CONSTRAINT fk_content_references_source FOREIGN KEY (source_content_id) REFERENCES contents(id) ON DELETE CASCADE;
ALTER TABLE content_references ADD CONSTRAINT fk_content_references_target_site FOREIGN KEY (target_site_id) REFERENCES sites(id) ON DELETE CASCADE;
ALTER TABLE content_references ADD CONSTRAINT fk_content_references_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE content_sync_logs ADD CONSTRAINT fk_content_sync_logs_reference FOREIGN KEY (content_reference_id) REFERENCES content_references(id) ON DELETE CASCADE;

ALTER TABLE content_translations ADD CONSTRAINT fk_content_translations_content FOREIGN KEY (content_id) REFERENCES contents(id) ON DELETE CASCADE;
ALTER TABLE content_translations ADD CONSTRAINT fk_content_translations_language FOREIGN KEY (language_code) REFERENCES languages(code);
ALTER TABLE content_translations ADD CONSTRAINT fk_content_translations_translator FOREIGN KEY (translator_id) REFERENCES users(id);
ALTER TABLE content_translations ADD CONSTRAINT fk_content_translations_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id);

ALTER TABLE operation_logs ADD CONSTRAINT fk_operation_logs_user FOREIGN KEY (user_id) REFERENCES users(id);

SELECT 'Sharing and i18n tables created successfully!' as message;
