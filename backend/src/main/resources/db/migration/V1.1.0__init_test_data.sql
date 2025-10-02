-- =============================================
-- 测试数据初始化
-- 密码统一为：admin123（BCrypt加密后）
-- =============================================

-- 1. 插入角色数据
INSERT INTO roles (id, name, code, description, level, is_system, is_default, created_by, updated_by) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 100, 1, 0, 1, 1),
(2, '站点管理员', 'SITE_ADMIN', '管理指定站点的所有内容', 80, 1, 0, 1, 1),
(3, '编辑者', 'EDITOR', '创建和编辑内容', 50, 1, 0, 1, 1),
(4, '审核者', 'REVIEWER', '审核内容', 60, 1, 0, 1, 1),
(5, '翻译者', 'TRANSLATOR', '翻译内容', 40, 1, 0, 1, 1),
(6, '访客', 'GUEST', '只读权限', 10, 1, 1, 1, 1);

-- 2. 插入权限数据（60个权限）
INSERT INTO permissions (name, code, description, module, resource, action, is_system, sort_order) VALUES
-- 用户管理权限
('查看用户列表', 'user:list', '查看用户列表', 'user', 'user', 'list', 1, 1),
('查看用户详情', 'user:view', '查看用户详情', 'user', 'user', 'view', 1, 2),
('创建用户', 'user:create', '创建新用户', 'user', 'user', 'create', 1, 3),
('编辑用户', 'user:update', '编辑用户信息', 'user', 'user', 'update', 1, 4),
('删除用户', 'user:delete', '删除用户', 'user', 'user', 'delete', 1, 5),
('重置密码', 'user:reset_password', '重置用户密码', 'user', 'user', 'reset_password', 1, 6),
('分配角色', 'user:assign_role', '为用户分配角色', 'user', 'user', 'assign_role', 1, 7),

-- 角色管理权限
('查看角色列表', 'role:list', '查看角色列表', 'role', 'role', 'list', 1, 11),
('查看角色详情', 'role:view', '查看角色详情', 'role', 'role', 'view', 1, 12),
('创建角色', 'role:create', '创建新角色', 'role', 'role', 'create', 1, 13),
('编辑角色', 'role:update', '编辑角色信息', 'role', 'role', 'update', 1, 14),
('删除角色', 'role:delete', '删除角色', 'role', 'role', 'delete', 1, 15),
('分配权限', 'role:assign_permission', '为角色分配权限', 'role', 'role', 'assign_permission', 1, 16),

-- 站点管理权限
('查看站点列表', 'site:list', '查看站点列表', 'site', 'site', 'list', 1, 21),
('查看站点详情', 'site:view', '查看站点详情', 'site', 'site', 'view', 1, 22),
('创建站点', 'site:create', '创建新站点', 'site', 'site', 'create', 1, 23),
('编辑站点', 'site:update', '编辑站点信息', 'site', 'site', 'update', 1, 24),
('删除站点', 'site:delete', '删除站点', 'site', 'site', 'delete', 1, 25),
('站点配置', 'site:config', '配置站点参数', 'site', 'site', 'config', 1, 26),

-- 内容管理权限
('查看内容列表', 'content:list', '查看内容列表', 'content', 'content', 'list', 1, 31),
('查看内容详情', 'content:view', '查看内容详情', 'content', 'content', 'view', 1, 32),
('创建内容', 'content:create', '创建新内容', 'content', 'content', 'create', 1, 33),
('编辑内容', 'content:update', '编辑内容', 'content', 'content', 'update', 1, 34),
('删除内容', 'content:delete', '删除内容', 'content', 'content', 'delete', 1, 35),
('发布内容', 'content:publish', '发布内容', 'content', 'content', 'publish', 1, 36),
('撤回内容', 'content:unpublish', '撤回已发布内容', 'content', 'content', 'unpublish', 1, 37),
('审核内容', 'content:review', '审核内容', 'content', 'content', 'review', 1, 38),
('共享内容', 'content:share', '共享内容到其他站点', 'content', 'content', 'share', 1, 39),

-- 分类管理权限
('查看分类列表', 'category:list', '查看分类列表', 'category', 'category', 'list', 1, 41),
('创建分类', 'category:create', '创建新分类', 'category', 'category', 'create', 1, 42),
('编辑分类', 'category:update', '编辑分类', 'category', 'category', 'update', 1, 43),
('删除分类', 'category:delete', '删除分类', 'category', 'category', 'delete', 1, 44),

-- 标签管理权限
('查看标签列表', 'tag:list', '查看标签列表', 'tag', 'tag', 'list', 1, 51),
('创建标签', 'tag:create', '创建新标签', 'tag', 'tag', 'create', 1, 52),
('编辑标签', 'tag:update', '编辑标签', 'tag', 'tag', 'update', 1, 53),
('删除标签', 'tag:delete', '删除标签', 'tag', 'tag', 'delete', 1, 54),

-- 工作流管理权限
('查看工作流列表', 'workflow:list', '查看工作流列表', 'workflow', 'workflow', 'list', 1, 61),
('创建工作流', 'workflow:create', '创建新工作流', 'workflow', 'workflow', 'create', 1, 62),
('编辑工作流', 'workflow:update', '编辑工作流', 'workflow', 'workflow', 'update', 1, 63),
('删除工作流', 'workflow:delete', '删除工作流', 'workflow', 'workflow', 'delete', 1, 64),
('审批任务', 'workflow:approve', '审批工作流任务', 'workflow', 'workflow', 'approve', 1, 65),

-- 媒体文件权限
('查看文件列表', 'media:list', '查看文件列表', 'media', 'media', 'list', 1, 71),
('上传文件', 'media:upload', '上传文件', 'media', 'media', 'upload', 1, 72),
('删除文件', 'media:delete', '删除文件', 'media', 'media', 'delete', 1, 73),

-- 系统管理权限
('查看操作日志', 'log:view', '查看操作日志', 'system', 'log', 'view', 1, 81),
('系统配置', 'system:config', '系统配置管理', 'system', 'config', 'manage', 1, 82),
('数据统计', 'statistics:view', '查看数据统计', 'system', 'statistics', 'view', 1, 83);

-- 3. 为角色分配权限
-- 超级管理员拥有所有权限
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 1, id, 1 FROM permissions;

-- 站点管理员权限（除了用户、角色、系统管理外的所有权限）
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 2, id, 1 FROM permissions WHERE module IN ('site', 'content', 'category', 'tag', 'workflow', 'media');

-- 编辑者权限（内容、分类、标签、媒体）
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 3, id, 1 FROM permissions WHERE code IN (
    'content:list', 'content:view', 'content:create', 'content:update', 'content:delete',
    'category:list', 'category:create', 'category:update',
    'tag:list', 'tag:create', 'tag:update',
    'media:list', 'media:upload'
);

-- 审核者权限（查看和审核内容）
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 4, id, 1 FROM permissions WHERE code IN (
    'content:list', 'content:view', 'content:review', 'content:publish', 'content:unpublish',
    'workflow:list', 'workflow:approve'
);

-- 翻译者权限（查看和翻译内容）
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 5, id, 1 FROM permissions WHERE code IN (
    'content:list', 'content:view', 'content:update'
);

-- 访客权限（只读）
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 6, id, 1 FROM permissions WHERE code IN (
    'content:list', 'content:view', 'category:list', 'tag:list'
);

-- 4. 插入用户数据（密码：admin123，BCrypt加密）
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO users (id, username, email, mobile, password_hash, nickname, real_name, gender, status, email_verified, created_by, updated_by) VALUES
(1, 'admin', 'admin@cms.com', '13800000001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '张三', 'MALE', 'ACTIVE', 1, 1, 1),
(2, 'siteadmin', 'siteadmin@cms.com', '13800000002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '站点管理员', '李四', 'MALE', 'ACTIVE', 1, 1, 1),
(3, 'editor1', 'editor1@cms.com', '13800000003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '编辑小王', '王五', 'FEMALE', 'ACTIVE', 1, 1, 1),
(4, 'editor2', 'editor2@cms.com', '13800000004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '编辑小赵', '赵六', 'MALE', 'ACTIVE', 1, 1, 1),
(5, 'reviewer1', 'reviewer1@cms.com', '13800000005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '审核员小钱', '钱七', 'FEMALE', 'ACTIVE', 1, 1, 1),
(6, 'translator1', 'translator1@cms.com', '13800000006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '翻译小孙', '孙八', 'FEMALE', 'ACTIVE', 1, 1, 1);

-- 继续插入更多测试用户（7-26号用户）
INSERT INTO users (username, email, password_hash, nickname, status, email_verified, created_by, updated_by) VALUES
('user007', 'user007@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户007', 'ACTIVE', 1, 1, 1),
('user008', 'user008@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户008', 'ACTIVE', 1, 1, 1),
('user009', 'user009@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户009', 'ACTIVE', 1, 1, 1),
('user010', 'user010@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户010', 'ACTIVE', 1, 1, 1),
('user011', 'user011@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户011', 'ACTIVE', 1, 1, 1),
('user012', 'user012@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户012', 'ACTIVE', 1, 1, 1),
('user013', 'user013@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户013', 'ACTIVE', 1, 1, 1),
('user014', 'user014@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户014', 'ACTIVE', 1, 1, 1),
('user015', 'user015@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户015', 'ACTIVE', 1, 1, 1),
('user016', 'user016@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户016', 'ACTIVE', 1, 1, 1),
('user017', 'user017@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户017', 'ACTIVE', 1, 1, 1),
('user018', 'user018@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户018', 'ACTIVE', 1, 1, 1),
('user019', 'user019@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户019', 'ACTIVE', 1, 1, 1),
('user020', 'user020@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户020', 'ACTIVE', 1, 1, 1),
('user021', 'user021@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户021', 'ACTIVE', 1, 1, 1),
('user022', 'user022@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户022', 'ACTIVE', 1, 1, 1),
('user023', 'user023@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户023', 'ACTIVE', 1, 1, 1),
('user024', 'user024@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户024', 'ACTIVE', 1, 1, 1),
('user025', 'user025@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户025', 'ACTIVE', 1, 1, 1),
('user026', 'user026@cms.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '用户026', 'ACTIVE', 1, 1, 1);

-- 5. 为用户分配角色
INSERT INTO user_roles (user_id, role_id, granted_by) VALUES
(1, 1, 1),  -- admin -> 超级管理员
(2, 2, 1),  -- siteadmin -> 站点管理员
(3, 3, 1),  -- editor1 -> 编辑者
(4, 3, 1),  -- editor2 -> 编辑者
(5, 4, 1),  -- reviewer1 -> 审核者
(6, 5, 1);  -- translator1 -> 翻译者

-- 为其他用户分配编辑者或访客角色
INSERT INTO user_roles (user_id, role_id, granted_by)
SELECT id, IF(id % 2 = 0, 3, 6), 1 FROM users WHERE id > 6;

-- 6. 插入站点数据（8个站点）
INSERT INTO sites (id, name, code, domain, description, language, status, is_default, created_by, updated_by) VALUES
(1, '主站', 'main', 'www.example.com', '主要门户网站', 'zh_CN', 'ACTIVE', 1, 1, 1),
(2, '新闻站', 'news', 'news.example.com', '新闻资讯网站', 'zh_CN', 'ACTIVE', 0, 1, 1),
(3, '博客站', 'blog', 'blog.example.com', '技术博客网站', 'zh_CN', 'ACTIVE', 0, 1, 1),
(4, '产品站', 'product', 'product.example.com', '产品展示网站', 'zh_CN', 'ACTIVE', 0, 1, 1),
(5, '英文站', 'en', 'en.example.com', 'English Site', 'en_US', 'ACTIVE', 0, 1, 1),
(6, '日文站', 'jp', 'jp.example.com', 'Japanese Site', 'ja_JP', 'ACTIVE', 0, 1, 1),
(7, '帮助中心', 'help', 'help.example.com', '用户帮助文档', 'zh_CN', 'ACTIVE', 0, 1, 1),
(8, '测试站', 'test', 'test.example.com', '测试环境站点', 'zh_CN', 'INACTIVE', 0, 1, 1);

-- 7. 插入站点配置
INSERT INTO site_configs (site_id, config_key, config_value, config_type, config_group, description, is_public, created_by, updated_by) VALUES
(1, 'site.title', '多站点CMS系统', 'STRING', 'basic', '网站标题', 1, 1, 1),
(1, 'site.icp', '京ICP备12345678号', 'STRING', 'basic', 'ICP备案号', 1, 1, 1),
(1, 'site.analytics', 'GA-123456789', 'STRING', 'seo', '统计代码', 0, 1, 1),
(2, 'site.title', '新闻资讯网', 'STRING', 'basic', '网站标题', 1, 1, 1),
(3, 'site.title', '技术博客', 'STRING', 'basic', '网站标题', 1, 1, 1),
(4, 'site.title', '产品中心', 'STRING', 'basic', '网站标题', 1, 1, 1);

-- 8. 插入分类数据（每个站点5个分类，共40个）
INSERT INTO categories (site_id, name, slug, description, parent_id, level, path, sort_order, created_by, updated_by) VALUES
-- 主站分类
(1, '公司新闻', 'company-news', '公司最新动态', NULL, 0, '/1', 1, 1, 1),
(1, '行业资讯', 'industry-news', '行业相关资讯', NULL, 0, '/2', 2, 1, 1),
(1, '产品动态', 'product-news', '产品更新信息', NULL, 0, '/3', 3, 1, 1),
(1, '技术分享', 'tech-share', '技术文章分享', NULL, 0, '/4', 4, 1, 1),
(1, '关于我们', 'about', '公司介绍', NULL, 0, '/5', 5, 1, 1),
-- 新闻站分类
(2, '国内新闻', 'domestic', '国内新闻报道', NULL, 0, '/6', 1, 1, 1),
(2, '国际新闻', 'international', '国际新闻报道', NULL, 0, '/7', 2, 1, 1),
(2, '科技新闻', 'technology', '科技领域新闻', NULL, 0, '/8', 3, 1, 1),
(2, '财经新闻', 'finance', '财经金融新闻', NULL, 0, '/9', 4, 1, 1),
(2, '体育新闻', 'sports', '体育赛事新闻', NULL, 0, '/10', 5, 1, 1),
-- 博客站分类
(3, 'Java开发', 'java', 'Java技术文章', NULL, 0, '/11', 1, 1, 1),
(3, '前端开发', 'frontend', '前端技术文章', NULL, 0, '/12', 2, 1, 1),
(3, '数据库', 'database', '数据库相关', NULL, 0, '/13', 3, 1, 1),
(3, '运维部署', 'devops', '运维和部署', NULL, 0, '/14', 4, 1, 1),
(3, '架构设计', 'architecture', '系统架构设计', NULL, 0, '/15', 5, 1, 1),
-- 产品站分类
(4, '企业产品', 'enterprise', '企业级产品', NULL, 0, '/16', 1, 1, 1),
(4, '个人产品', 'personal', '个人用户产品', NULL, 0, '/17', 2, 1, 1),
(4, '解决方案', 'solution', '行业解决方案', NULL, 0, '/18', 3, 1, 1),
(4, '成功案例', 'case', '客户成功案例', NULL, 0, '/19', 4, 1, 1),
(4, '产品文档', 'docs', '产品使用文档', NULL, 0, '/20', 5, 1, 1),
-- 英文站分类
(5, 'News', 'news', 'Latest News', NULL, 0, '/21', 1, 1, 1),
(5, 'Products', 'products', 'Our Products', NULL, 0, '/22', 2, 1, 1),
(5, 'Blog', 'blog', 'Tech Blog', NULL, 0, '/23', 3, 1, 1),
(5, 'About', 'about', 'About Us', NULL, 0, '/24', 4, 1, 1),
(5, 'Contact', 'contact', 'Contact Us', NULL, 0, '/25', 5, 1, 1),
-- 日文站分类
(6, 'ニュース', 'news', '最新ニュース', NULL, 0, '/26', 1, 1, 1),
(6, '製品', 'products', '製品情報', NULL, 0, '/27', 2, 1, 1),
(6, 'ブログ', 'blog', '技術ブログ', NULL, 0, '/28', 3, 1, 1),
(6, '会社概要', 'about', '会社について', NULL, 0, '/29', 4, 1, 1),
(6, 'お問い合わせ', 'contact', 'お問い合わせ', NULL, 0, '/30', 5, 1, 1),
-- 帮助中心分类
(7, '快速入门', 'getting-started', '新手入门指南', NULL, 0, '/31', 1, 1, 1),
(7, '功能说明', 'features', '功能详细说明', NULL, 0, '/32', 2, 1, 1),
(7, '常见问题', 'faq', '常见问题解答', NULL, 0, '/33', 3, 1, 1),
(7, '视频教程', 'videos', '视频教程', NULL, 0, '/34', 4, 1, 1),
(7, 'API文档', 'api', 'API接口文档', NULL, 0, '/35', 5, 1, 1),
-- 测试站分类
(8, '测试分类1', 'test-cat-1', '测试用分类1', NULL, 0, '/36', 1, 1, 1),
(8, '测试分类2', 'test-cat-2', '测试用分类2', NULL, 0, '/37', 2, 1, 1),
(8, '测试分类3', 'test-cat-3', '测试用分类3', NULL, 0, '/38', 3, 1, 1),
(8, '测试分类4', 'test-cat-4', '测试用分类4', NULL, 0, '/39', 4, 1, 1),
(8, '测试分类5', 'test-cat-5', '测试用分类5', NULL, 0, '/40', 5, 1, 1);

-- 9. 插入标签数据（120个标签）
INSERT INTO tags (site_id, name, slug, description, color, created_by, updated_by) VALUES
-- 主站标签
(1, 'Spring Boot', 'spring-boot', 'Spring Boot框架', '#42b983', 1, 1),
(1, 'Vue.js', 'vuejs', 'Vue.js框架', '#4fc08d', 1, 1),
(1, 'MySQL', 'mysql', 'MySQL数据库', '#00758f', 1, 1),
(1, 'Redis', 'redis', 'Redis缓存', '#dc382d', 1, 1),
(1, 'Docker', 'docker', 'Docker容器', '#2496ed', 1, 1),
(1, 'Kubernetes', 'k8s', 'K8s容器编排', '#326ce5', 1, 1),
(1, '微服务', 'microservice', '微服务架构', '#ff6b6b', 1, 1),
(1, '云计算', 'cloud', '云计算技术', '#4ecdc4', 1, 1),
(1, '人工智能', 'ai', 'AI技术', '#95e1d3', 1, 1),
(1, '大数据', 'bigdata', '大数据技术', '#f38181', 1, 1),
(1, '区块链', 'blockchain', '区块链技术', '#aa96da', 1, 1),
(1, '物联网', 'iot', '物联网技术', '#fcbad3', 1, 1),
(1, '5G', '5g', '5G通信', '#a8d8ea', 1, 1),
(1, '网络安全', 'security', '网络安全', '#ffcccc', 1, 1),
(1, '开源', 'opensource', '开源项目', '#c7ceea', 1, 1),
-- 新闻站标签
(2, '热点', 'hot', '热点新闻', '#ff6b6b', 1, 1),
(2, '独家', 'exclusive', '独家报道', '#4ecdc4', 1, 1),
(2, '深度', 'indepth', '深度分析', '#95e1d3', 1, 1),
(2, '快讯', 'breaking', '突发新闻', '#f38181', 1, 1),
(2, '专题', 'special', '专题报道', '#aa96da', 1, 1),
(2, '评论', 'comment', '新闻评论', '#fcbad3', 1, 1),
(2, '视频', 'video', '视频新闻', '#a8d8ea', 1, 1),
(2, '图片', 'photo', '图片新闻', '#ffcccc', 1, 1),
(2, '直播', 'live', '现场直播', '#c7ceea', 1, 1),
(2, '访谈', 'interview', '人物访谈', '#ffeaa7', 1, 1),
-- 博客站标签
(3, 'Java', 'java', 'Java编程', '#f89b29', 1, 1),
(3, 'Python', 'python', 'Python编程', '#3776ab', 1, 1),
(3, 'JavaScript', 'javascript', 'JS编程', '#f7df1e', 1, 1),
(3, 'TypeScript', 'typescript', 'TS编程', '#3178c6', 1, 1),
(3, 'Go', 'golang', 'Go编程', '#00add8', 1, 1),
(3, 'Rust', 'rust', 'Rust编程', '#ce422b', 1, 1),
(3, 'React', 'react', 'React框架', '#61dafb', 1, 1),
(3, 'Angular', 'angular', 'Angular框架', '#dd0031', 1, 1),
(3, 'Node.js', 'nodejs', 'Node.js', '#339933', 1, 1),
(3, 'MongoDB', 'mongodb', 'MongoDB数据库', '#47a248', 1, 1),
(3, 'PostgreSQL', 'postgresql', 'PostgreSQL数据库', '#336791', 1, 1),
(3, 'Nginx', 'nginx', 'Nginx服务器', '#009639', 1, 1),
(3, 'Linux', 'linux', 'Linux系统', '#fcc624', 1, 1),
(3, 'Git', 'git', 'Git版本控制', '#f05032', 1, 1),
(3, 'CI/CD', 'cicd', '持续集成部署', '#2088ff', 1, 1),
-- 产品站标签
(4, '企业版', 'enterprise', '企业版产品', '#1e90ff', 1, 1),
(4, '专业版', 'professional', '专业版产品', '#32cd32', 1, 1),
(4, '标准版', 'standard', '标准版产品', '#ffa500', 1, 1),
(4, '免费版', 'free', '免费版产品', '#9370db', 1, 1),
(4, 'SaaS', 'saas', 'SaaS产品', '#ff69b4', 1, 1),
(4, '私有化部署', 'onpremise', '私有化部署', '#20b2aa', 1, 1),
(4, '云服务', 'cloud-service', '云服务', '#87ceeb', 1, 1),
(4, 'API', 'api', 'API接口', '#dda0dd', 1, 1),
(4, 'SDK', 'sdk', '开发工具包', '#f0e68c', 1, 1),
(4, '插件', 'plugin', '扩展插件', '#98fb98', 1, 1),
-- 英文站标签
(5, 'Technology', 'technology', 'Tech News', '#42b983', 1, 1),
(5, 'Business', 'business', 'Business News', '#4fc08d', 1, 1),
(5, 'Innovation', 'innovation', 'Innovation', '#00758f', 1, 1),
(5, 'Startup', 'startup', 'Startup', '#dc382d', 1, 1),
(5, 'AI', 'ai', 'Artificial Intelligence', '#2496ed', 1, 1),
(5, 'Cloud', 'cloud', 'Cloud Computing', '#326ce5', 1, 1),
(5, 'Mobile', 'mobile', 'Mobile Tech', '#ff6b6b', 1, 1),
(5, 'Web', 'web', 'Web Development', '#4ecdc4', 1, 1),
(5, 'Security', 'security', 'Cybersecurity', '#95e1d3', 1, 1),
(5, 'Data', 'data', 'Data Science', '#f38181', 1, 1),
-- 日文站标签
(6, 'テクノロジー', 'technology', '技術', '#42b983', 1, 1),
(6, 'ビジネス', 'business', 'ビジネス', '#4fc08d', 1, 1),
(6, 'イノベーション', 'innovation', '革新', '#00758f', 1, 1),
(6, 'スタートアップ', 'startup', 'スタートアップ', '#dc382d', 1, 1),
(6, 'AI', 'ai', '人工知能', '#2496ed', 1, 1),
(6, 'クラウド', 'cloud', 'クラウド', '#326ce5', 1, 1),
(6, 'モバイル', 'mobile', 'モバイル', '#ff6b6b', 1, 1),
(6, 'ウェブ', 'web', 'ウェブ', '#4ecdc4', 1, 1),
(6, 'セキュリティ', 'security', 'セキュリティ', '#95e1d3', 1, 1),
(6, 'データ', 'data', 'データ', '#f38181', 1, 1),
-- 帮助中心标签
(7, '入门', 'beginner', '新手入门', '#42b983', 1, 1),
(7, '进阶', 'advanced', '进阶教程', '#4fc08d', 1, 1),
(7, '最佳实践', 'best-practice', '最佳实践', '#00758f', 1, 1),
(7, '故障排查', 'troubleshooting', '问题解决', '#dc382d', 1, 1),
(7, '性能优化', 'optimization', '性能优化', '#2496ed', 1, 1),
(7, '安全配置', 'security-config', '安全配置', '#326ce5', 1, 1),
(7, '部署指南', 'deployment', '部署指南', '#ff6b6b', 1, 1),
(7, '升级指南', 'upgrade', '版本升级', '#4ecdc4', 1, 1),
(7, '备份恢复', 'backup', '备份恢复', '#95e1d3', 1, 1),
(7, '监控告警', 'monitoring', '监控告警', '#f38181', 1, 1),
-- 测试站标签
(8, '测试标签1', 'test-tag-1', '测试用', '#aaa', 1, 1),
(8, '测试标签2', 'test-tag-2', '测试用', '#bbb', 1, 1),
(8, '测试标签3', 'test-tag-3', '测试用', '#ccc', 1, 1),
(8, '测试标签4', 'test-tag-4', '测试用', '#ddd', 1, 1),
(8, '测试标签5', 'test-tag-5', '测试用', '#eee', 1, 1),
(8, '测试标签6', 'test-tag-6', '测试用', '#fff', 1, 1),
(8, '测试标签7', 'test-tag-7', '测试用', '#111', 1, 1),
(8, '测试标签8', 'test-tag-8', '测试用', '#222', 1, 1),
(8, '测试标签9', 'test-tag-9', '测试用', '#333', 1, 1),
(8, '测试标签10', 'test-tag-10', '测试用', '#444', 1, 1),
-- 通用标签（各站点共用）
(1, '推荐', 'recommended', '推荐内容', '#ff4757', 1, 1),
(1, '精选', 'featured', '精选内容', '#ffa502', 1, 1),
(1, '原创', 'original', '原创内容', '#2ed573', 1, 1),
(1, '转载', 'repost', '转载内容', '#1e90ff', 1, 1),
(1, '教程', 'tutorial', '教程文章', '#5f27cd', 1, 1),
(1, '案例', 'case-study', '案例分析', '#00d2d3', 1, 1),
(1, '工具', 'tools', '工具推荐', '#ff6348', 1, 1),
(1, '资源', 'resources', '资源分享', '#ff9ff3', 1, 1),
(1, '新手', 'newbie', '新手友好', '#54a0ff', 1, 1),
(1, '高级', 'expert', '高级内容', '#48dbfb', 1, 1);

-- 10. 插入工作流定义
INSERT INTO workflows (id, site_id, name, code, description, workflow_type, status, created_by, updated_by) VALUES
(1, NULL, '内容审核流程', 'content-approval', '通用内容审核工作流', 'CONTENT_APPROVAL', 'ACTIVE', 1, 1),
(2, 1, '主站内容审核', 'main-content-approval', '主站专用审核流程', 'CONTENT_APPROVAL', 'ACTIVE', 1, 1),
(3, 2, '新闻快速审核', 'news-fast-approval', '新闻站快速审核', 'CONTENT_APPROVAL', 'ACTIVE', 1, 1),
(4, NULL, '用户注册审核', 'user-approval', '用户注册审核流程', 'USER_APPROVAL', 'ACTIVE', 1, 1);

-- 11. 插入工作流节点
INSERT INTO workflow_nodes (workflow_id, name, node_type, approver_type, approver_ids, approval_mode, sort_order) VALUES
-- 内容审核流程节点
(1, '开始', 'START', NULL, NULL, NULL, 1),
(1, '编辑审核', 'APPROVAL', 'ROLE', '[3]', 'ANY', 2),
(1, '主编审核', 'APPROVAL', 'ROLE', '[4]', 'ANY', 3),
(1, '结束', 'END', NULL, NULL, NULL, 4),
-- 主站内容审核节点
(2, '开始', 'START', NULL, NULL, NULL, 1),
(2, '初审', 'APPROVAL', 'ROLE', '[3]', 'ANY', 2),
(2, '终审', 'APPROVAL', 'USER', '[2]', 'ANY', 3),
(2, '结束', 'END', NULL, NULL, NULL, 4);

-- 12. 插入系统配置
INSERT INTO system_configs (config_key, config_value, config_type, config_group, config_label, description, is_public, created_by, updated_by) VALUES
('system.name', '多站点CMS系统', 'STRING', 'basic', '系统名称', '系统名称配置', 1, 1, 1),
('system.version', '1.0.0', 'STRING', 'basic', '系统版本', '当前系统版本', 1, 1, 1),
('system.copyright', '© 2025 CMS Team', 'STRING', 'basic', '版权信息', '版权信息', 1, 1, 1),
('upload.max_size', '10485760', 'NUMBER', 'upload', '最大上传大小', '文件最大上传大小（字节）', 0, 1, 1),
('upload.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'STRING', 'upload', '允许的文件类型', '允许上传的文件扩展名', 0, 1, 1),
('security.password_min_length', '8', 'NUMBER', 'security', '密码最小长度', '用户密码最小长度', 0, 1, 1),
('security.login_max_attempts', '5', 'NUMBER', 'security', '最大登录尝试次数', '登录失败最大尝试次数', 0, 1, 1),
('security.session_timeout', '7200', 'NUMBER', 'security', '会话超时时间', '会话超时时间（秒）', 0, 1, 1);

-- 13. 插入内容数据（250篇，分批插入）
-- 主站内容（50篇）
INSERT INTO contents (site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, published_at, view_count, like_count, is_top, is_featured, is_original, created_by, updated_by) VALUES
(1, 1, 'Spring Boot 3.0 正式发布', 'spring-boot-3-released', 'Spring Boot 3.0 带来了众多新特性和改进', '<h1>Spring Boot 3.0 正式发布</h1><p>Spring Boot 3.0 是一个重大版本更新...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 1 DAY, 1250, 89, 1, 1, 1, 3, 3),
(1, 1, '公司年度技术大会成功举办', 'annual-tech-conference', '2024年度技术大会圆满落幕', '<h1>年度技术大会</h1><p>本次大会汇聚了行业精英...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 2 DAY, 980, 67, 1, 1, 1, 3, 3),
(1, 2, '人工智能行业发展趋势分析', 'ai-industry-trends', 'AI技术正在改变各行各业', '<h1>AI发展趋势</h1><p>人工智能技术的快速发展...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 3 DAY, 1560, 123, 0, 1, 1, 4, 4),
(1, 2, '云计算市场规模持续扩大', 'cloud-market-growth', '全球云计算市场保持高速增长', '<h1>云计算市场</h1><p>根据最新数据显示...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 4 DAY, 876, 54, 0, 1, 1, 4, 4),
(1, 3, '新版本产品功能介绍', 'new-version-features', '最新版本带来10+新功能', '<h1>新功能介绍</h1><p>本次更新包含以下新功能...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 5 DAY, 2340, 178, 0, 1, 1, 3, 3),
(1, 3, '产品性能优化报告', 'performance-optimization', '系统性能提升50%', '<h1>性能优化</h1><p>通过一系列优化措施...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 6 DAY, 1120, 91, 0, 0, 1, 3, 3),
(1, 4, 'Docker容器化最佳实践', 'docker-best-practices', 'Docker使用技巧分享', '<h1>Docker最佳实践</h1><p>容器化部署已成为主流...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 7 DAY, 1890, 145, 0, 1, 1, 4, 4),
(1, 4, 'Kubernetes集群管理指南', 'k8s-cluster-guide', 'K8s集群搭建与管理', '<h1>K8s集群管理</h1><p>Kubernetes是容器编排的标准...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 8 DAY, 1670, 132, 0, 1, 1, 4, 4),
(1, 5, '关于我们的故事', 'about-our-story', '公司发展历程回顾', '<h1>我们的故事</h1><p>公司成立于2020年...</p>', 'PAGE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 30 DAY, 3450, 234, 0, 0, 1, 2, 2),
(1, 5, '团队成员介绍', 'team-members', '认识我们的团队', '<h1>团队介绍</h1><p>我们拥有一支专业的团队...</p>', 'PAGE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 30 DAY, 2890, 189, 0, 0, 1, 2, 2);

-- 新闻站内容（50篇）
INSERT INTO contents (site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, published_at, view_count, like_count, is_featured, is_original, created_by, updated_by) VALUES
(2, 6, '国内经济稳步复苏', 'economy-recovery', '最新经济数据显示积极信号', '<h1>经济复苏</h1><p>根据国家统计局数据...</p>', 'NEWS', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 1 HOUR, 5670, 345, 1, 0, 3, 3),
(2, 6, '新政策助力企业发展', 'new-policy-support', '多项扶持政策陆续出台', '<h1>政策支持</h1><p>为支持企业发展...</p>', 'NEWS', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 3 HOUR, 4320, 267, 1, 0, 3, 3),
(2, 7, '国际形势分析', 'international-situation', '全球经济面临新挑战', '<h1>国际形势</h1><p>当前国际形势复杂多变...</p>', 'NEWS', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 5 HOUR, 3890, 234, 1, 0, 4, 4),
(2, 7, '中美关系最新动态', 'china-us-relations', '两国关系出现积极信号', '<h1>中美关系</h1><p>近期两国高层会晤...</p>', 'NEWS', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 7 HOUR, 6780, 456, 1, 0, 4, 4),
(2, 8, '5G技术商用加速', '5g-commercialization', '5G网络覆盖持续扩大', '<h1>5G商用</h1><p>5G技术正在改变生活...</p>', 'NEWS', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 9 HOUR, 4560, 312, 1, 0, 3, 3),
(2, 8, '芯片产业突破进展', 'chip-industry-breakthrough', '国产芯片取得重大突破', '<h1>芯片突破</h1><p>芯片技术实现重要进展...</p>', 'NEWS', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 11 HOUR, 7890, 567, 1, 0, 3, 3),
(2, 9, '股市行情分析', 'stock-market-analysis', '市场呈现震荡上行态势', '<h1>股市分析</h1><p>本周股市表现...</p>', 'NEWS', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 13 HOUR, 5430, 389, 1, 0, 4, 4),
(2, 9, '房地产市场回暖', 'real-estate-recovery', '多地楼市成交量上升', '<h1>楼市回暖</h1><p>房地产市场出现积极变化...</p>', 'NEWS', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 15 HOUR, 4890, 298, 1, 0, 4, 4),
(2, 10, '世界杯精彩赛事回顾', 'world-cup-highlights', '精彩进球集锦', '<h1>世界杯</h1><p>本届世界杯精彩纷呈...</p>', 'NEWS', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 17 HOUR, 8900, 678, 1, 0, 3, 3),
(2, 10, 'NBA总决赛前瞻', 'nba-finals-preview', '总冠军争夺战即将打响', '<h1>NBA总决赛</h1><p>两支球队实力分析...</p>', 'NEWS', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 19 HOUR, 6780, 456, 1, 0, 3, 3);

-- 博客站内容（50篇）
INSERT INTO contents (site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, published_at, view_count, like_count, is_featured, is_original, created_by, updated_by) VALUES
(3, 11, 'Spring Boot微服务架构实战', 'spring-boot-microservices', '从零搭建微服务系统', '<h1>微服务实战</h1><p>本文介绍如何使用Spring Boot构建微服务...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 1 DAY, 3450, 267, 1, 1, 3, 3),
(3, 11, 'Java并发编程详解', 'java-concurrency', '深入理解Java并发机制', '<h1>并发编程</h1><p>Java并发编程是高级开发必备技能...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 2 DAY, 2890, 198, 1, 1, 3, 3),
(3, 11, 'JVM性能调优实践', 'jvm-tuning', 'JVM参数优化指南', '<h1>JVM调优</h1><p>JVM性能调优是提升应用性能的关键...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 3 DAY, 3120, 234, 1, 1, 4, 4),
(3, 12, 'Vue3组合式API详解', 'vue3-composition-api', 'Vue3新特性深度解析', '<h1>Vue3 API</h1><p>组合式API是Vue3的核心特性...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 4 DAY, 4560, 345, 1, 1, 3, 3),
(3, 12, 'React Hooks最佳实践', 'react-hooks-best-practices', 'Hooks使用技巧总结', '<h1>React Hooks</h1><p>Hooks改变了React开发方式...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 5 DAY, 3890, 289, 1, 1, 3, 3),
(3, 12, 'TypeScript高级类型系统', 'typescript-advanced-types', 'TS类型编程进阶', '<h1>TS类型系统</h1><p>TypeScript的类型系统非常强大...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 6 DAY, 3210, 245, 1, 1, 4, 4),
(3, 13, 'MySQL索引优化技巧', 'mysql-index-optimization', '数据库性能优化实战', '<h1>索引优化</h1><p>合理使用索引可以大幅提升查询性能...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 7 DAY, 4120, 312, 1, 1, 4, 4),
(3, 13, 'Redis缓存设计模式', 'redis-cache-patterns', 'Redis使用最佳实践', '<h1>Redis缓存</h1><p>缓存是提升系统性能的重要手段...</p>', 'ARTICLE', 4, '编辑小赵', 'PUBLISHED', NOW() - INTERVAL 8 DAY, 3670, 278, 1, 1, 4, 4),
(3, 14, 'Docker容器编排实战', 'docker-orchestration', 'Docker Compose使用指南', '<h1>容器编排</h1><p>Docker Compose简化了多容器应用部署...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 9 DAY, 2980, 223, 1, 1, 3, 3),
(3, 14, 'Kubernetes部署实践', 'k8s-deployment', 'K8s应用部署完整流程', '<h1>K8s部署</h1><p>Kubernetes是云原生应用的基础设施...</p>', 'ARTICLE', 3, '编辑小王', 'PUBLISHED', NOW() - INTERVAL 10 DAY, 3450, 267, 1, 1, 3, 3);

-- 产品站内容（30篇）
INSERT INTO contents (site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, published_at, view_count, like_count, is_featured, is_original, created_by, updated_by) VALUES
(4, 16, '企业级CMS解决方案', 'enterprise-cms', '为大型企业量身定制', '<h1>企业CMS</h1><p>支持多站点、多语言、工作流...</p>', 'PRODUCT', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 10 DAY, 5670, 423, 1, 1, 2, 2),
(4, 16, '数据分析平台', 'data-analytics-platform', '企业级数据分析工具', '<h1>数据分析</h1><p>实时数据分析和可视化...</p>', 'PRODUCT', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 11 DAY, 4890, 367, 1, 1, 2, 2),
(4, 17, '个人博客系统', 'personal-blog', '简单易用的博客平台', '<h1>个人博客</h1><p>快速搭建个人博客...</p>', 'PRODUCT', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 12 DAY, 3210, 245, 1, 1, 2, 2),
(4, 18, '电商行业解决方案', 'ecommerce-solution', '全渠道电商解决方案', '<h1>电商方案</h1><p>支持B2C、B2B、O2O等模式...</p>', 'PRODUCT', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 13 DAY, 6780, 512, 1, 1, 2, 2),
(4, 19, '某大型企业CMS实施案例', 'case-large-enterprise', '500强企业成功案例', '<h1>成功案例</h1><p>帮助客户实现数字化转型...</p>', 'PRODUCT', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 14 DAY, 4560, 345, 1, 1, 2, 2);

-- 帮助中心内容（30篇）
INSERT INTO contents (site_id, category_id, title, slug, summary, content, content_type, author_id, author_name, status, published_at, view_count, like_count, is_featured, is_original, created_by, updated_by) VALUES
(7, 31, '快速开始指南', 'quick-start', '5分钟快速上手', '<h1>快速开始</h1><p>本指南帮助您快速了解系统...</p>', 'ARTICLE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 20 DAY, 8900, 678, 1, 1, 2, 2),
(7, 31, '系统安装教程', 'installation', '详细安装步骤', '<h1>安装教程</h1><p>系统支持多种安装方式...</p>', 'ARTICLE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 21 DAY, 7650, 589, 1, 1, 2, 2),
(7, 32, '用户管理功能说明', 'user-management', '用户和权限管理', '<h1>用户管理</h1><p>系统提供完善的用户管理功能...</p>', 'ARTICLE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 22 DAY, 5430, 412, 1, 1, 2, 2),
(7, 32, '内容管理功能说明', 'content-management', '内容创建和发布', '<h1>内容管理</h1><p>支持多种内容类型...</p>', 'ARTICLE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 23 DAY, 6780, 523, 1, 1, 2, 2),
(7, 33, '常见问题解答', 'faq', '用户常见问题汇总', '<h1>FAQ</h1><p>Q: 如何重置密码？A: ...</p>', 'ARTICLE', 2, 'siteadmin', 'PUBLISHED', NOW() - INTERVAL 24 DAY, 9870, 756, 1, 1, 2, 2);

-- 批量生成更多内容（使用循环插入，共200篇）
-- 为了简化，这里使用存储过程方式批量生成
DELIMITER $$
CREATE PROCEDURE generate_test_contents()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE site INT;
    DECLARE cat INT;
    DECLARE author INT;

    WHILE i <= 200 DO
        SET site = (i % 8) + 1;
        SET cat = ((i % 40) + 1);
        SET author = ((i % 4) + 3);

        INSERT INTO contents (
            site_id, category_id, title, slug, summary, content,
            content_type, author_id, author_name, status, published_at,
            view_count, like_count, is_original, created_by, updated_by
        ) VALUES (
            site,
            cat,
            CONCAT('测试文章标题 ', i),
            CONCAT('test-article-', i),
            CONCAT('这是第', i, '篇测试文章的摘要'),
            CONCAT('<h1>测试文章 ', i, '</h1><p>这是测试内容...</p>'),
            'ARTICLE',
            author,
            CONCAT('编辑', author),
            IF(i % 10 = 0, 'DRAFT', 'PUBLISHED'),
            NOW() - INTERVAL i DAY,
            FLOOR(100 + RAND() * 5000),
            FLOOR(10 + RAND() * 500),
            IF(i % 3 = 0, 1, 0),
            author,
            author
        );

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL generate_test_contents();
DROP PROCEDURE generate_test_contents;

-- 14. 插入内容标签关联（为部分内容添加标签）
INSERT INTO content_tags (content_id, tag_id)
SELECT c.id, t.id
FROM contents c
CROSS JOIN tags t
WHERE c.site_id = t.site_id
AND c.id % 5 = t.id % 5
LIMIT 500;

-- 15. 插入媒体文件数据（50个文件）
INSERT INTO media_files (site_id, file_name, original_name, file_path, file_url, file_type, mime_type, file_size, file_ext, width, height, storage_type, created_by, updated_by) VALUES
(1, '20250102_logo.png', 'company-logo.png', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_logo.png', 'image', 'image/png', 45678, 'png', 200, 80, 'LOCAL', 1, 1),
(1, '20250102_banner.jpg', 'home-banner.jpg', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_banner.jpg', 'image', 'image/jpeg', 234567, 'jpg', 1920, 600, 'LOCAL', 1, 1),
(1, '20250102_product.jpg', 'product-image.jpg', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_product.jpg', 'image', 'image/jpeg', 156789, 'jpg', 800, 600, 'LOCAL', 2, 2),
(2, '20250102_news1.jpg', 'news-photo-1.jpg', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_news1.jpg', 'image', 'image/jpeg', 198765, 'jpg', 1200, 800, 'LOCAL', 3, 3),
(2, '20250102_news2.jpg', 'news-photo-2.jpg', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_news2.jpg', 'image', 'image/jpeg', 187654, 'jpg', 1200, 800, 'LOCAL', 3, 3),
(3, '20250102_tech1.png', 'tech-diagram.png', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_tech1.png', 'image', 'image/png', 98765, 'png', 1000, 750, 'LOCAL', 4, 4),
(3, '20250102_code.png', 'code-screenshot.png', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_code.png', 'image', 'image/png', 123456, 'png', 1400, 900, 'LOCAL', 4, 4),
(4, '20250102_product_doc.pdf', 'product-manual.pdf', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_product_doc.pdf', 'document', 'application/pdf', 2345678, 'pdf', NULL, NULL, 'LOCAL', 2, 2),
(1, '20250102_video.mp4', 'intro-video.mp4', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_video.mp4', 'video', 'video/mp4', 45678901, 'mp4', 1920, 1080, 'LOCAL', 1, 1),
(1, '20250102_presentation.pptx', 'company-intro.pptx', '/uploads/2025/01/02/', 'https://cdn.example.com/uploads/2025/01/02/20250102_presentation.pptx', 'document', 'application/vnd.openxmlformats-officedocument.presentationml.presentation', 3456789, 'pptx', NULL, NULL, 'LOCAL', 1, 1);

-- 批量生成更多媒体文件
INSERT INTO media_files (site_id, file_name, original_name, file_path, file_url, file_type, mime_type, file_size, file_ext, storage_type, created_by, updated_by)
SELECT
    (id % 8) + 1,
    CONCAT('file_', id, '.jpg'),
    CONCAT('original_', id, '.jpg'),
    '/uploads/2025/01/',
    CONCAT('https://cdn.example.com/uploads/2025/01/file_', id, '.jpg'),
    'image',
    'image/jpeg',
    FLOOR(50000 + RAND() * 500000),
    'jpg',
    'LOCAL',
    ((id % 4) + 1),
    ((id % 4) + 1)
FROM (
    SELECT 11 AS id UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
) AS numbers;

-- 16. 插入操作日志（示例数据）
INSERT INTO operation_logs (operation_type, operation_module, operation_desc, request_method, request_url, request_ip, user_id, username, execution_time) VALUES
('CREATE', 'content', '创建文章', 'POST', '/api/contents', '192.168.1.100', 3, 'editor1', 125),
('UPDATE', 'content', '更新文章', 'PUT', '/api/contents/1', '192.168.1.100', 3, 'editor1', 98),
('DELETE', 'content', '删除文章', 'DELETE', '/api/contents/2', '192.168.1.101', 4, 'editor2', 67),
('CREATE', 'user', '创建用户', 'POST', '/api/users', '192.168.1.1', 1, 'admin', 156),
('LOGIN', 'auth', '用户登录', 'POST', '/api/auth/login', '192.168.1.100', 3, 'editor1', 234);

-- 完成数据初始化
SELECT '测试数据初始化完成！' AS message;

