-- 修复权限问题：为SUPER_ADMIN角色添加缺失的权限

-- 1. 获取SUPER_ADMIN角色ID和permission相关权限ID
SET @super_admin_role_id = (SELECT id FROM roles WHERE code = 'SUPER_ADMIN');
SET @permission_view_id = (SELECT id FROM permissions WHERE code = 'permission:view');
SET @permission_list_id = (SELECT id FROM permissions WHERE code = 'permission:list');
SET @permission_create_id = (SELECT id FROM permissions WHERE code = 'permission:create');
SET @permission_update_id = (SELECT id FROM permissions WHERE code = 'permission:update');
SET @permission_delete_id = (SELECT id FROM permissions WHERE code = 'permission:delete');

-- 2. 添加权限（如果不存在）
INSERT IGNORE INTO role_permissions (role_id, permission_id)
VALUES 
    (@super_admin_role_id, @permission_view_id),
    (@super_admin_role_id, @permission_list_id),
    (@super_admin_role_id, @permission_create_id),
    (@super_admin_role_id, @permission_update_id),
    (@super_admin_role_id, @permission_delete_id);

-- 3. 验证结果
SELECT 
    r.code AS role_code,
    r.name AS role_name,
    p.code AS permission_code,
    p.name AS permission_name
FROM roles r
INNER JOIN role_permissions rp ON r.id = rp.role_id
INNER JOIN permissions p ON rp.permission_id = p.id
WHERE r.code = 'SUPER_ADMIN' AND p.code LIKE 'permission:%'
ORDER BY p.code;

