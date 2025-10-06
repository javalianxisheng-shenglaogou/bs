-- =============================================
-- 清理角色和用户数据
-- 1. 删除查看者角色（GUEST）和翻译者角色（TRANSLATOR）
-- 2. 格式化用户角色，只保留：超级管理员、站点管理员、编辑者、审批者
-- =============================================

-- 1. 删除用户与查看者角色、翻译者角色的关联
DELETE FROM user_roles WHERE role_id IN (5, 6);

-- 2. 删除角色权限关联
DELETE FROM role_permissions WHERE role_id IN (5, 6);

-- 3. 删除查看者角色和翻译者角色
DELETE FROM roles WHERE id IN (5, 6);

-- 4. 为没有角色的用户分配编辑者角色（ID=3）
-- 首先找出没有角色的用户
INSERT INTO user_roles (user_id, role_id, granted_by)
SELECT u.id, 3, 1
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id
)
AND u.deleted = 0;

-- 5. 更新角色说明
UPDATE roles SET description = '拥有系统所有权限，可以管理所有功能' WHERE id = 1;
UPDATE roles SET description = '管理指定站点的所有内容和用户' WHERE id = 2;
UPDATE roles SET description = '创建和编辑内容，提交审批' WHERE id = 3;
UPDATE roles SET description = '审核和发布内容，处理审批流程' WHERE id = 4;

