-- =============================================
-- 修复审批员权限问题
-- 1. 添加缺失的工作流查看权限
-- 2. 添加内容更新权限（用于审批操作）
-- 3. 确保审批员可以正常使用审批功能
-- =============================================

-- 删除审批员现有权限
DELETE FROM role_permissions WHERE role_id = 4;

-- 重新为审批员分配完整权限
-- 审批员权限：内容管理、工作流管理、站点查看
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT 4, id, 1 FROM permissions WHERE code IN (
    -- 内容管理权限（审批员需要查看、审核、发布、下线内容）
    'content:list',
    'content:view',
    'content:update',      -- 新增：审批时可能需要修改内容
    'content:review',
    'content:publish',
    'content:unpublish',
    
    -- 工作流管理权限（审批员需要查看工作流列表、实例、任务，并进行审批）
    'workflow:list',
    'workflow:view',       -- 新增：查看工作流详情
    'workflow:approve',    -- 审批任务
    
    -- 站点查看权限
    'site:list',
    'site:view'
);

