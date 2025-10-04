-- 注意: 字段和表已经在之前的迁移中创建,这里只插入测试数据

-- 插入更多测试工作流数据
-- 站点审批流程
INSERT IGNORE INTO workflows (id, site_id, name, code, description, workflow_type, trigger_event, status, version, created_at, updated_at, created_by, updated_by, deleted)
VALUES (2, NULL, '站点创建审批流程', 'site_approval', '用于审批新创建的站点', 'CUSTOM', 'site.created', 'ACTIVE', 1, NOW(), NOW(), 1, 1, 0);

-- 站点审批流程节点
INSERT IGNORE INTO workflow_nodes (id, workflow_id, name, node_type, approver_type, approver_ids, approval_mode, condition_expression, position_x, position_y, sort_order, created_at, updated_at, deleted)
VALUES 
(5, 2, '开始', 'START', NULL, NULL, NULL, NULL, 100, 100, 0, NOW(), NOW(), 0),
(6, 2, '技术主管审批', 'APPROVAL', 'USER', '[1]', 'ANY', NULL, 300, 100, 1, NOW(), NOW(), 0),
(7, 2, '运营主管审批', 'APPROVAL', 'USER', '[1]', 'ANY', NULL, 500, 100, 2, NOW(), NOW(), 0),
(8, 2, '结束', 'END', NULL, NULL, NULL, NULL, 700, 100, 3, NOW(), NOW(), 0);

-- 插入测试内容数据
INSERT IGNORE INTO contents (id, site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, approval_status, is_top, is_featured, view_count, published_at, created_at, updated_at, created_by, updated_by, deleted, version)
VALUES
(1, 1, NULL, '测试文章1 - 待审批', 'test-article-1', '这是一篇待审批的测试文章', '<p>这是测试文章1的内容,需要审批后才能发布</p>', 'ARTICLE', 1, 'admin', 'DRAFT', 'NONE', 0, 0, 0, NULL, NOW(), NOW(), 1, 1, 0, 0),
(2, 1, NULL, '测试文章2 - 已发布', 'test-article-2', '这是一篇已发布的文章', '<p>这是测试文章2的内容,已经审批通过并发布</p>', 'ARTICLE', 1, 'admin', 'PUBLISHED', 'APPROVED', 0, 0, 100, NOW(), NOW(), NOW(), 1, 1, 0, 0),
(3, 1, NULL, '测试新闻1 - 草稿', 'test-news-1', '这是一篇草稿新闻', '<p>这是测试新闻1的内容,还在编辑中</p>', 'NEWS', 1, 'admin', 'DRAFT', 'NONE', 0, 0, 0, NULL, NOW(), NOW(), 1, 1, 0, 0),
(4, 1, NULL, '测试页面1 - 待审批', 'test-page-1', '这是一个待审批的页面', '<p>这是测试页面1的内容,需要审批</p>', 'PAGE', 1, 'admin', 'DRAFT', 'NONE', 0, 0, 0, NULL, NOW(), NOW(), 1, 1, 0, 0);

-- 插入测试分类数据
INSERT IGNORE INTO categories (id, site_id, parent_id, name, code, description, icon_url, cover_url, level, path, sort_order, is_visible, seo_title, seo_keywords, seo_description, created_at, updated_at, deleted)
VALUES 
(1, 1, NULL, '新闻资讯', 'news', '最新新闻和资讯', NULL, NULL, 1, '/1', 1, 1, '新闻资讯', '新闻,资讯', '最新新闻和资讯', NOW(), NOW(), 0),
(2, 1, NULL, '技术文章', 'tech', '技术相关文章', NULL, NULL, 1, '/2', 2, 1, '技术文章', '技术,开发', '技术相关文章', NOW(), NOW(), 0),
(3, 1, 1, '公司新闻', 'company-news', '公司内部新闻', NULL, NULL, 2, '/1/3', 1, 1, '公司新闻', '公司,新闻', '公司内部新闻', NOW(), NOW(), 0),
(4, 1, 1, '行业动态', 'industry-news', '行业最新动态', NULL, NULL, 2, '/1/4', 2, 1, '行业动态', '行业,动态', '行业最新动态', NOW(), NOW(), 0),
(5, 1, 2, 'Java开发', 'java', 'Java开发技术', NULL, NULL, 2, '/2/5', 1, 1, 'Java开发', 'Java,开发', 'Java开发技术', NOW(), NOW(), 0),
(6, 1, 2, '前端开发', 'frontend', '前端开发技术', NULL, NULL, 2, '/2/6', 2, 1, '前端开发', '前端,Vue,React', '前端开发技术', NOW(), NOW(), 0);

-- 更新内容的分类ID
UPDATE contents SET category_id = 1 WHERE id = 1;
UPDATE contents SET category_id = 2 WHERE id = 2;
UPDATE contents SET category_id = 3 WHERE id = 3;
UPDATE contents SET category_id = 4 WHERE id = 4;

