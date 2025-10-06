-- ============================================================================
-- 内容版本控制系统 - 数据库迁移脚本
-- 版本：V1.2.6
-- 创建日期：2025-10-06
-- 描述：创建内容版本表和版本操作日志表
-- ============================================================================

-- 1. 创建内容版本表
CREATE TABLE IF NOT EXISTS content_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    content_id BIGINT NOT NULL COMMENT '关联的内容ID',
    version_number INT NOT NULL COMMENT '版本号（从1开始递增）',
    
    -- 内容快照字段
    title VARCHAR(500) NOT NULL COMMENT '标题快照',
    slug VARCHAR(200) COMMENT 'URL别名快照',
    summary TEXT COMMENT '摘要快照',
    content LONGTEXT COMMENT '内容快照（富文本）',
    cover_image VARCHAR(500) COMMENT '封面图快照',
    content_type VARCHAR(50) COMMENT '内容类型快照',
    status VARCHAR(50) COMMENT '状态快照',
    is_top BOOLEAN DEFAULT FALSE COMMENT '是否置顶快照',
    is_featured BOOLEAN DEFAULT FALSE COMMENT '是否推荐快照',
    is_original BOOLEAN DEFAULT FALSE COMMENT '是否原创快照',
    tags JSON COMMENT '标签快照（JSON数组）',
    metadata JSON COMMENT '元数据快照（JSON对象）',
    
    -- 版本元信息
    change_summary VARCHAR(500) COMMENT '修改摘要（用户填写）',
    change_type VARCHAR(50) NOT NULL COMMENT '修改类型（CREATE/UPDATE/PUBLISH/UNPUBLISH/RESTORE）',
    created_by BIGINT NOT NULL COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 版本标记
    is_tagged BOOLEAN DEFAULT FALSE COMMENT '是否标记为重要版本',
    tag_name VARCHAR(100) COMMENT '版本标签名称（如"上线版本"、"备份版本"）',
    
    -- 统计信息
    file_size BIGINT COMMENT '版本数据大小（字节）',
    
    -- 索引
    INDEX idx_content_id (content_id),
    INDEX idx_version_number (content_id, version_number),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_change_type (change_type),
    
    -- 外键约束
    CONSTRAINT fk_content_versions_content_id 
        FOREIGN KEY (content_id) 
        REFERENCES contents(id) 
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容版本表';

-- 2. 创建版本操作日志表
CREATE TABLE IF NOT EXISTS content_version_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    version_id BIGINT COMMENT '版本ID',
    
    -- 操作信息
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型（CREATE/VIEW/RESTORE/DELETE/TAG/COMPARE）',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(100) COMMENT '操作人姓名',
    operation_detail JSON COMMENT '操作详情（JSON对象）',
    
    -- 请求信息
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    
    -- 时间戳
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    
    -- 索引
    INDEX idx_content_id (content_id),
    INDEX idx_version_id (version_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容版本操作日志表';

-- 3. 为现有内容创建初始版本（可选，根据需要启用）
-- 注意：这会为所有现有内容创建版本1，可能需要较长时间
-- 如果内容表数据量很大，建议分批执行或在业务低峰期执行

-- INSERT INTO content_versions (
--     content_id, version_number, title, slug, summary, content, 
--     cover_image, content_type, status, is_top, is_featured, is_original,
--     tags, metadata, change_summary, change_type, 
--     created_by, created_by_name, created_at
-- )
-- SELECT 
--     c.id, 1, c.title, c.slug, c.summary, c.content,
--     c.cover_image, c.content_type, c.status, c.is_top, c.is_featured, c.is_original,
--     c.tags, c.metadata, '初始版本', 'CREATE',
--     c.created_by, u.real_name, c.created_at
-- FROM contents c
-- LEFT JOIN users u ON c.created_by = u.id
-- WHERE NOT EXISTS (
--     SELECT 1 FROM content_versions cv WHERE cv.content_id = c.id
-- );

-- 4. 添加注释说明
-- 版本号规则：
-- - 每个内容的版本号从1开始
-- - 每次创建新版本时，版本号递增
-- - 版本号在同一内容下唯一

-- 修改类型说明：
-- - CREATE: 内容创建时的初始版本
-- - UPDATE: 内容更新时创建的版本
-- - PUBLISH: 内容发布时创建的版本
-- - UNPUBLISH: 内容撤回时创建的版本
-- - RESTORE: 从历史版本恢复时创建的版本

-- 操作类型说明：
-- - CREATE: 创建版本
-- - VIEW: 查看版本
-- - RESTORE: 恢复版本
-- - DELETE: 删除版本
-- - TAG: 标记版本
-- - COMPARE: 对比版本

