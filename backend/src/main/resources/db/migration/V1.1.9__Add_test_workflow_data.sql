-- 添加测试工作流数据

-- 插入测试工作流
INSERT INTO workflows (id, site_id, name, code, description, workflow_type, trigger_event, status, version, created_at, updated_at, created_by, updated_by, deleted)
VALUES (1, NULL, '内容审批流程', 'content_approval', '用于审批新创建的内容', 'CONTENT_APPROVAL', 'content.created', 'ACTIVE', 1, NOW(), NOW(), 1, 1, 0);

-- 插入工作流节点
-- 开始节点
INSERT INTO workflow_nodes (id, workflow_id, name, node_type, approver_type, approver_ids, approval_mode, condition_expression, position_x, position_y, sort_order, created_at, updated_at, deleted)
VALUES (1, 1, '开始', 'START', NULL, NULL, NULL, NULL, 100, 100, 0, NOW(), NOW(), 0);

-- 审批节点1: 部门主管审批
INSERT INTO workflow_nodes (id, workflow_id, name, node_type, approver_type, approver_ids, approval_mode, condition_expression, position_x, position_y, sort_order, created_at, updated_at, deleted)
VALUES (2, 1, '部门主管审批', 'APPROVAL', 'USER', '[1]', 'ANY', NULL, 300, 100, 1, NOW(), NOW(), 0);

-- 审批节点2: 总编辑审批
INSERT INTO workflow_nodes (id, workflow_id, name, node_type, approver_type, approver_ids, approval_mode, condition_expression, position_x, position_y, sort_order, created_at, updated_at, deleted)
VALUES (3, 1, '总编辑审批', 'APPROVAL', 'USER', '[1]', 'ANY', NULL, 500, 100, 2, NOW(), NOW(), 0);

-- 结束节点
INSERT INTO workflow_nodes (id, workflow_id, name, node_type, approver_type, approver_ids, approval_mode, condition_expression, position_x, position_y, sort_order, created_at, updated_at, deleted)
VALUES (4, 1, '结束', 'END', NULL, NULL, NULL, NULL, 700, 100, 3, NOW(), NOW(), 0);

