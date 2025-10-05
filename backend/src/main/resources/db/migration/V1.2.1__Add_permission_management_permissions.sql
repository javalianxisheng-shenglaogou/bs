-- 添加权限管理相关的权限

-- 插入权限管理权限（使用INSERT IGNORE避免重复）
INSERT IGNORE INTO permissions (id, code, name, description, module, resource, action, sort_order, is_system, deleted, created_at, created_by, updated_at, updated_by)
VALUES
    (59, 'permission:view', '查看权限', '查看权限详情', 'permission', 'permission', 'view', 1, TRUE, FALSE, NOW(), 1, NOW(), 1),
    (60, 'permission:list', '权限列表', '查看权限列表', 'permission', 'permission', 'list', 2, TRUE, FALSE, NOW(), 1, NOW(), 1),
    (61, 'permission:create', '创建权限', '创建新权限', 'permission', 'permission', 'create', 3, TRUE, FALSE, NOW(), 1, NOW(), 1),
    (62, 'permission:update', '更新权限', '更新权限信息', 'permission', 'permission', 'update', 4, TRUE, FALSE, NOW(), 1, NOW(), 1),
    (63, 'permission:delete', '删除权限', '删除权限', 'permission', 'permission', 'delete', 5, TRUE, FALSE, NOW(), 1, NOW(), 1);

-- 为SUPER_ADMIN角色分配权限管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'SUPER_ADMIN'
  AND p.code IN ('permission:view', 'permission:list', 'permission:create', 'permission:update', 'permission:delete')
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 为ADMIN角色分配权限查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN'
  AND p.code IN ('permission:view', 'permission:list')
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

