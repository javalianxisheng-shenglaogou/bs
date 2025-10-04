-- 添加日志管理权限
INSERT INTO permissions (code, name, description, module, resource, action, is_system, sort_order, created_at, updated_at) VALUES
('log:list', 'View log list', 'View system log list', 'log', 'log', 'list', 1, 1, NOW(), NOW()),
('log:view', 'View log details', 'View system log details', 'log', 'log', 'view', 1, 2, NOW(), NOW()),
('log:delete', 'Delete logs', 'Delete system logs', 'log', 'log', 'delete', 1, 3, NOW(), NOW());

-- 为管理员角色添加日志权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions WHERE code IN ('log:list', 'log:view', 'log:delete');

