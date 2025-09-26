-- 基于Spring Boot的多站点内容管理系统 - 初始数据插入脚本

USE multi_site_cms;

-- =============================================
-- 1. 插入基础角色数据
-- =============================================

INSERT INTO roles (name, code, description, is_system) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', TRUE),
('站点管理员', 'SITE_ADMIN', '站点管理员，管理特定站点', TRUE),
('编辑者', 'EDITOR', '内容编辑者，负责内容创建和编辑', TRUE),
('审核者', 'REVIEWER', '内容审核者，负责内容审核和发布', TRUE),
('翻译者', 'TRANSLATOR', '内容翻译者，负责多语言翻译', TRUE),
('访客', 'GUEST', '访客用户，只有基本查看权限', TRUE);

-- =============================================
-- 2. 插入默认超级管理员用户
-- =============================================

-- 密码为 admin123，使用BCrypt加密
INSERT INTO users (username, email, password_hash, nickname, status, created_by) VALUES
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLO.tz8cAQKK', '系统管理员', 'ACTIVE', 'system');

-- 为超级管理员分配角色
INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(1, 1, 1);

-- =============================================
-- 3. 插入基础语言数据
-- =============================================

INSERT INTO languages (code, name, native_name, is_default, is_active, sort_order) VALUES
('zh-CN', '简体中文', '简体中文', TRUE, TRUE, 1),
('en-US', 'English', 'English', FALSE, TRUE, 2),
('ja-JP', '日本語', '日本語', FALSE, TRUE, 3),
('ko-KR', '한국어', '한국어', FALSE, TRUE, 4),
('fr-FR', 'Français', 'Français', FALSE, FALSE, 5),
('de-DE', 'Deutsch', 'Deutsch', FALSE, FALSE, 6),
('es-ES', 'Español', 'Español', FALSE, FALSE, 7);

-- =============================================
-- 4. 插入示例站点数据
-- =============================================

INSERT INTO sites (name, code, domain, description, owner_id, status, created_by) VALUES
('主站', 'main', 'www.example.com', '系统主站点', 1, 'ACTIVE', 'admin'),
('博客站', 'blog', 'blog.example.com', '博客子站点', 1, 'ACTIVE', 'admin'),
('新闻站', 'news', 'news.example.com', '新闻子站点', 1, 'ACTIVE', 'admin');

-- =============================================
-- 5. 插入站点配置数据
-- =============================================

-- 主站配置
INSERT INTO site_configs (site_id, config_key, config_value, config_type, description, is_public) VALUES
(1, 'site_title', '多站点内容管理系统', 'STRING', '站点标题', TRUE),
(1, 'site_description', '基于Spring Boot的多站点内容管理系统', 'STRING', '站点描述', TRUE),
(1, 'site_keywords', 'CMS,多站点,Spring Boot,Vue.js', 'STRING', '站点关键词', TRUE),
(1, 'allow_registration', 'false', 'BOOLEAN', '是否允许用户注册', FALSE),
(1, 'max_upload_size', '10485760', 'NUMBER', '最大上传文件大小（字节）', FALSE),
(1, 'default_page_size', '20', 'NUMBER', '默认分页大小', FALSE);

-- 博客站配置
INSERT INTO site_configs (site_id, config_key, config_value, config_type, description, is_public) VALUES
(2, 'site_title', '技术博客', 'STRING', '站点标题', TRUE),
(2, 'site_description', '分享技术心得和经验', 'STRING', '站点描述', TRUE),
(2, 'allow_comment', 'true', 'BOOLEAN', '是否允许评论', TRUE),
(2, 'comment_audit', 'true', 'BOOLEAN', '评论是否需要审核', FALSE);

-- 新闻站配置
INSERT INTO site_configs (site_id, config_key, config_value, config_type, description, is_public) VALUES
(3, 'site_title', '新闻资讯', 'STRING', '站点标题', TRUE),
(3, 'site_description', '最新资讯和新闻报道', 'STRING', '站点描述', TRUE),
(3, 'auto_publish', 'false', 'BOOLEAN', '是否自动发布', FALSE);

-- =============================================
-- 6. 插入内容分类数据
-- =============================================

-- 主站分类
INSERT INTO content_categories (site_id, name, slug, description, level, path, sort_order) VALUES
(1, '系统公告', 'announcements', '系统相关公告和通知', 1, '/announcements', 1),
(1, '帮助文档', 'help', '用户帮助和使用指南', 1, '/help', 2),
(1, '关于我们', 'about', '公司介绍和联系方式', 1, '/about', 3);

-- 博客站分类
INSERT INTO content_categories (site_id, name, slug, description, level, path, sort_order) VALUES
(2, '技术分享', 'tech', '技术文章和教程', 1, '/tech', 1),
(2, '生活随笔', 'life', '生活感悟和随笔', 1, '/life', 2),
(2, '项目经验', 'projects', '项目开发经验分享', 1, '/projects', 3);

-- 新闻站分类
INSERT INTO content_categories (site_id, name, slug, description, level, path, sort_order) VALUES
(3, '科技新闻', 'technology', '最新科技资讯', 1, '/technology', 1),
(3, '行业动态', 'industry', '行业发展动态', 1, '/industry', 2),
(3, '产品发布', 'products', '新产品发布信息', 1, '/products', 3);

-- =============================================
-- 7. 插入基础工作流数据
-- =============================================

-- 内容发布工作流
INSERT INTO workflows (name, code, description, trigger_type, is_active) VALUES
('内容发布审批', 'content_publish', '内容发布前的审批流程', 'MANUAL', TRUE),
('内容删除审批', 'content_delete', '内容删除前的审批流程', 'MANUAL', TRUE);

-- 内容发布工作流步骤
INSERT INTO workflow_steps (workflow_id, name, step_order, step_type, assignee_type, assignee_value, is_required, timeout_hours) VALUES
(1, '编辑审核', 1, 'APPROVAL', 'ROLE', 'EDITOR', TRUE, 24),
(1, '最终审批', 2, 'APPROVAL', 'ROLE', 'REVIEWER', TRUE, 48),
(1, '发布通知', 3, 'NOTIFICATION', 'ROLE', 'SITE_ADMIN', FALSE, 1);

-- 内容删除工作流步骤
INSERT INTO workflow_steps (workflow_id, name, step_order, step_type, assignee_type, assignee_value, is_required, timeout_hours) VALUES
(2, '删除审核', 1, 'APPROVAL', 'ROLE', 'SITE_ADMIN', TRUE, 24),
(2, '最终确认', 2, 'APPROVAL', 'ROLE', 'SUPER_ADMIN', TRUE, 48);

-- =============================================
-- 8. 插入示例内容数据
-- =============================================

-- 系统公告
INSERT INTO contents (site_id, title, slug, summary, content, content_type, status, author_id, category_id, publish_at) VALUES
(1, '欢迎使用多站点内容管理系统', 'welcome', '欢迎使用基于Spring Boot的多站点内容管理系统', 
'<h1>欢迎使用多站点内容管理系统</h1><p>这是一个基于Spring Boot和Vue.js开发的现代化内容管理系统，支持多站点管理、内容共享、工作流审批等功能。</p>', 
'ARTICLE', 'PUBLISHED', 1, 1, NOW());

-- 帮助文档
INSERT INTO contents (site_id, title, slug, summary, content, content_type, status, author_id, category_id, publish_at) VALUES
(1, '系统使用指南', 'user-guide', '详细的系统使用指南和操作说明', 
'<h1>系统使用指南</h1><p>本指南将帮助您快速上手使用多站点内容管理系统。</p><h2>主要功能</h2><ul><li>多站点管理</li><li>内容管理</li><li>用户权限管理</li><li>工作流审批</li></ul>', 
'PAGE', 'PUBLISHED', 1, 2, NOW());

-- 博客文章
INSERT INTO contents (site_id, title, slug, summary, content, content_type, status, author_id, category_id, publish_at) VALUES
(2, 'Spring Boot 3.0 新特性介绍', 'spring-boot-3-features', 'Spring Boot 3.0 带来了许多令人兴奋的新特性', 
'<h1>Spring Boot 3.0 新特性介绍</h1><p>Spring Boot 3.0 是一个重要的版本更新，带来了许多新特性和改进。</p>', 
'ARTICLE', 'PUBLISHED', 1, 4, NOW());

-- 新闻文章
INSERT INTO contents (site_id, title, slug, summary, content, content_type, status, author_id, category_id, publish_at) VALUES
(3, '人工智能技术发展趋势', 'ai-trends-2024', '2024年人工智能技术的最新发展趋势分析', 
'<h1>人工智能技术发展趋势</h1><p>2024年，人工智能技术继续快速发展，在各个领域都有重要突破。</p>', 
'ARTICLE', 'PUBLISHED', 1, 7, NOW());

-- =============================================
-- 9. 创建版本记录
-- =============================================

-- 为已发布的内容创建版本记录
INSERT INTO content_versions (content_id, version_number, title, content, change_type, change_description, created_by) VALUES
(1, 1, '欢迎使用多站点内容管理系统', '<h1>欢迎使用多站点内容管理系统</h1><p>这是一个基于Spring Boot和Vue.js开发的现代化内容管理系统，支持多站点管理、内容共享、工作流审批等功能。</p>', 'CREATE', '初始创建', 1),
(2, 1, '系统使用指南', '<h1>系统使用指南</h1><p>本指南将帮助您快速上手使用多站点内容管理系统。</p><h2>主要功能</h2><ul><li>多站点管理</li><li>内容管理</li><li>用户权限管理</li><li>工作流审批</li></ul>', 'CREATE', '初始创建', 1),
(3, 1, 'Spring Boot 3.0 新特性介绍', '<h1>Spring Boot 3.0 新特性介绍</h1><p>Spring Boot 3.0 是一个重要的版本更新，带来了许多新特性和改进。</p>', 'CREATE', '初始创建', 1),
(4, 1, '人工智能技术发展趋势', '<h1>人工智能技术发展趋势</h1><p>2024年，人工智能技术继续快速发展，在各个领域都有重要突破。</p>', 'CREATE', '初始创建', 1);

SELECT 'Initial data inserted successfully!' as message;
SELECT 'Database setup completed!' as message;

-- 显示数据统计
SELECT 
    (SELECT COUNT(*) FROM users) as users_count,
    (SELECT COUNT(*) FROM roles) as roles_count,
    (SELECT COUNT(*) FROM sites) as sites_count,
    (SELECT COUNT(*) FROM contents) as contents_count,
    (SELECT COUNT(*) FROM content_categories) as categories_count,
    (SELECT COUNT(*) FROM languages) as languages_count;
