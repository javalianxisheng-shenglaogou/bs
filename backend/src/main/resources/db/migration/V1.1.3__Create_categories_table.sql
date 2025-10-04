-- 创建分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    site_id BIGINT NOT NULL COMMENT '站点ID',
    parent_id BIGINT COMMENT '父分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) NOT NULL COMMENT '分类代码',
    description TEXT COMMENT '分类描述',
    icon_url VARCHAR(500) COMMENT '分类图标',
    cover_url VARCHAR(500) COMMENT '分类封面',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    level INT NOT NULL DEFAULT 1 COMMENT '层级',
    path VARCHAR(500) COMMENT '路径',
    is_visible TINYINT(1) DEFAULT 1 COMMENT '是否显示',
    seo_title VARCHAR(200) COMMENT 'SEO标题',
    seo_keywords VARCHAR(500) COMMENT 'SEO关键词',
    seo_description TEXT COMMENT 'SEO描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    version INT NOT NULL DEFAULT 0 COMMENT '版本号',
    INDEX idx_site_id (site_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code),
    INDEX idx_sort_order (sort_order),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted),
    UNIQUE KEY uk_site_code (site_id, code, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 插入示例数据
INSERT INTO categories (site_id, parent_id, name, code, description, level, path, sort_order, created_by, updated_by) VALUES
(1, NULL, '技术', 'tech', '技术相关文章', 1, '/1', 1, 1, 1),
(1, 1, 'Java', 'java', 'Java技术文章', 2, '/1/2', 1, 1, 1),
(1, 1, 'Python', 'python', 'Python技术文章', 2, '/1/3', 2, 1, 1),
(1, 1, '前端', 'frontend', '前端技术文章', 2, '/1/4', 3, 1, 1),
(1, NULL, '生活', 'life', '生活相关文章', 1, '/5', 2, 1, 1),
(1, 5, '旅游', 'travel', '旅游相关文章', 2, '/5/6', 1, 1, 1),
(1, 5, '美食', 'food', '美食相关文章', 2, '/5/7', 2, 1, 1),
(2, NULL, '新闻', 'news', '新闻分类', 1, '/8', 1, 1, 1),
(2, 8, '国内新闻', 'domestic', '国内新闻', 2, '/8/9', 1, 1, 1),
(2, 8, '国际新闻', 'international', '国际新闻', 2, '/8/10', 2, 1, 1);

