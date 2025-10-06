-- =============================================
-- 修复字符集和任务名称乱码问题
-- =============================================

-- 1. 修复workflow_tasks表的字符集
ALTER TABLE workflow_tasks CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 修复workflow_nodes表的字符集
ALTER TABLE workflow_nodes CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 修复workflow_instances表的字符集
ALTER TABLE workflow_instances CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. 修复workflow_approval_history表的字符集
ALTER TABLE workflow_approval_history CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. 更新已有的乱码任务名称
UPDATE workflow_tasks SET task_name = '技术主管审批' WHERE task_name LIKE '%鎶?湳涓荤?%';
UPDATE workflow_tasks SET task_name = '部门主管审批' WHERE task_name LIKE '%部门主管%' AND task_name NOT LIKE '部门主管审批';
UPDATE workflow_tasks SET task_name = '总编辑审批' WHERE task_name LIKE '%总编辑%' AND task_name NOT LIKE '总编辑审批';

-- 6. 更新workflow_nodes表中的乱码节点名称
UPDATE workflow_nodes SET name = '技术主管审批' WHERE name LIKE '%鎶?湳涓荤?%';
UPDATE workflow_nodes SET name = '部门主管审批' WHERE name LIKE '%部门主管%' AND name NOT LIKE '部门主管审批';
UPDATE workflow_nodes SET name = '总编辑审批' WHERE name LIKE '%总编辑%' AND name NOT LIKE '总编辑审批';

