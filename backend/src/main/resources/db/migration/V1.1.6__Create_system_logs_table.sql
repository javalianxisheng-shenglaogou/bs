-- 创建系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    module VARCHAR(50) NOT NULL COMMENT '模块',
    action VARCHAR(50) NOT NULL COMMENT '操作',
    level VARCHAR(20) NOT NULL DEFAULT 'INFO' COMMENT '日志级别',
    description TEXT COMMENT '描述',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    browser VARCHAR(200) COMMENT '浏览器',
    operating_system VARCHAR(100) COMMENT '操作系统',
    execution_time BIGINT COMMENT '执行时间(毫秒)',
    is_success TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否成功',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人',
    updated_by BIGINT COMMENT '更新人',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    INDEX idx_user_id (user_id),
    INDEX idx_module (module),
    INDEX idx_action (action),
    INDEX idx_level (level),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 插入测试数据
INSERT INTO system_logs (user_id, username, module, action, level, description, request_method, request_url, ip_address, execution_time, is_success, created_at) VALUES
(1, 'admin', 'auth', 'login', 'INFO', 'User login', 'POST', '/api/auth/login', '127.0.0.1', 150, 1, NOW()),
(1, 'admin', 'site', 'create', 'INFO', 'Create site', 'POST', '/api/sites', '127.0.0.1', 200, 1, NOW()),
(1, 'admin', 'content', 'create', 'INFO', 'Create content', 'POST', '/api/contents', '127.0.0.1', 180, 1, NOW()),
(1, 'admin', 'category', 'create', 'INFO', 'Create category', 'POST', '/api/categories', '127.0.0.1', 120, 1, NOW()),
(1, 'admin', 'user', 'update', 'INFO', 'Update user info', 'PUT', '/api/users/1', '127.0.0.1', 100, 1, NOW()),
(1, 'admin', 'site', 'update', 'INFO', 'Update site info', 'PUT', '/api/sites/1', '127.0.0.1', 110, 1, NOW()),
(1, 'admin', 'content', 'delete', 'WARN', 'Delete content', 'DELETE', '/api/contents/1', '127.0.0.1', 90, 1, NOW()),
(1, 'admin', 'auth', 'login', 'ERROR', 'Login failed', 'POST', '/api/auth/login', '127.0.0.1', 50, 0, NOW()),
(1, 'admin', 'file', 'upload', 'INFO', 'Upload file', 'POST', '/api/files/upload', '127.0.0.1', 500, 1, NOW()),
(1, 'admin', 'category', 'delete', 'INFO', 'Delete category', 'DELETE', '/api/categories/1', '127.0.0.1', 80, 1, NOW());

