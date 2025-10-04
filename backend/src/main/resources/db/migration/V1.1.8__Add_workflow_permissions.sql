-- 添加工作流相关权限

-- 插入工作流权限
INSERT INTO permissions (name, code, description, created_at, updated_at, deleted) VALUES
('工作流列表', 'workflow:list', '查看工作流列表', NOW(), NOW(), 0),
('工作流查看', 'workflow:view', '查看工作流详情', NOW(), NOW(), 0),
('工作流创建', 'workflow:create', '创建工作流', NOW(), NOW(), 0),
('工作流更新', 'workflow:update', '更新工作流', NOW(), NOW(), 0),
('工作流删除', 'workflow:delete', '删除工作流', NOW(), NOW(), 0);

-- 为管理员角色添加工作流权限
INSERT INTO role_permissions (role_id, permission_id, granted_at, granted_by)
SELECT 
    r.id,
    p.id,
    NOW(),
    1
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN'
AND p.code IN ('workflow:list', 'workflow:view', 'workflow:create', 'workflow:update', 'workflow:delete')
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

