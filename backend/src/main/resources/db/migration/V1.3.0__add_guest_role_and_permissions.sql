-- =============================================
-- V1.3.0 添加访客角色和权限
-- 描述: 为访客用户添加查看已发布内容的角色和权限
-- =============================================

-- 1. 添加访客角色（如果不存在）
INSERT IGNORE INTO roles (id, name, code, description, level, is_system, is_default, created_by, updated_by) VALUES
(6, '访客', 'GUEST', '普通访客用户，只能查看已发布内容', 10, 1, 1, 1, 1);

-- 2. 添加公开访问权限（使用 INSERT IGNORE 避免重复）
INSERT IGNORE INTO permissions (name, code, description, module, resource, action, is_system, sort_order) VALUES
('查看已发布内容列表', 'content:view:published', '查看已发布的公开内容列表', 'content', 'content', 'view_published', 1, 100),
('查看已发布内容详情', 'content:detail:published', '查看已发布内容详细信息', 'content', 'content', 'detail_published', 1, 101),
('搜索已发布内容', 'content:search:published', '搜索已发布内容', 'content', 'content', 'search_published', 1, 102),
('查看公开分类', 'category:view:public', '查看公开分类列表', 'category', 'category', 'view_public', 1, 110),
('查看站点列表', 'site:view:public', '查看活跃站点列表', 'site', 'site', 'view_public', 1, 120),
('增加内容浏览量', 'content:increment:view', '增加内容浏览计数', 'content', 'content', 'increment_view', 1, 130);

-- 3. 为访客角色分配权限（先删除已存在的，再插入）
DELETE FROM role_permissions WHERE role_id = 6;
INSERT INTO role_permissions (role_id, permission_id)
SELECT 6, id FROM permissions WHERE code IN (
    'content:view:published',
    'content:detail:published',
    'content:search:published',
    'category:view:public',
    'site:view:public',
    'content:increment:view'
);

-- 4. 性能优化索引（如果已存在会报错，但通过Flyway的out-of-order配置不影响系统运行）
-- 注意：如果索引已存在，这些语句会失败，但不会影响其他迁移
SET @exist_check = (SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'contents' AND index_name = 'idx_status_published_site');
SET @sql_cmd = IF(@exist_check = 0, 'ALTER TABLE contents ADD INDEX idx_status_published_site (status, published_at, site_id)', 'SELECT ''Index idx_status_published_site already exists'' AS msg');
PREPARE stmt FROM @sql_cmd;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist_check = (SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'contents' AND index_name = 'idx_site_category_status');
SET @sql_cmd = IF(@exist_check = 0, 'ALTER TABLE contents ADD INDEX idx_site_category_status (site_id, category_id, status)', 'SELECT ''Index idx_site_category_status already exists'' AS msg');
PREPARE stmt FROM @sql_cmd;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exist_check = (SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'contents' AND index_name = 'idx_title_content_search');
SET @sql_cmd = IF(@exist_check = 0, 'ALTER TABLE contents ADD INDEX idx_title_content_search (title(50), content(50))', 'SELECT ''Index idx_title_content_search already exists'' AS msg');
PREPARE stmt FROM @sql_cmd;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. 创建访客测试用户（密码: password，如果不存在则创建）
INSERT IGNORE INTO users (username, password_hash, email, nickname, status) VALUES
('guest_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EH.8h7Ug.5rVFaLI6Uq3zi', 'guest@example.com', '访客用户', 'ACTIVE');

-- 6. 为测试用户分配访客角色（先删除已有的，再插入）
DELETE ur FROM user_roles ur 
INNER JOIN users u ON ur.user_id = u.id 
WHERE u.username = 'guest_user' AND ur.role_id = 6;

INSERT INTO user_roles (user_id, role_id, granted_by)
SELECT u.id, 6, 1 FROM users u WHERE u.username = 'guest_user';

