-- 修复 user_roles 表的 granted_by 字段
-- 问题：granted_by 字段是 NOT NULL 但没有默认值，导致插入失败
-- 解决方案：将 granted_by 字段改为可空，或者添加默认值

-- 方案1：将 granted_by 改为可空（推荐）
ALTER TABLE user_roles MODIFY COLUMN granted_by BIGINT NULL COMMENT '授权人ID';

-- 更新现有数据，将 NULL 值设置为系统管理员ID（1）
UPDATE user_roles SET granted_by = 1 WHERE granted_by IS NULL;

