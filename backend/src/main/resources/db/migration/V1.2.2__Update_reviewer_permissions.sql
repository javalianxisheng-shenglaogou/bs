-- =============================================
-- 更新审核者权限
-- 确保审核者可以访问工作流、内容和站点管理
-- =============================================

-- 删除审核者现有权限
DELETE FROM role_permissions WHERE role_id = 4;

-- 重新为审核者分配权限
-- 审核者权限：内容管理、工作流管理、站点查看
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 4, id, 1 FROM permissions WHERE code IN (
    -- 内容管理权限
    'content:list',
    'content:view',
    'content:review',
    'content:publish',
    'content:unpublish',
    -- 工作流管理权限
    'workflow:list',
    'workflow:approve',
    -- 站点查看权限
    'site:list',
    'site:view'
);

