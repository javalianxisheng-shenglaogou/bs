-- =============================================
-- 工作流模块表结构
-- =============================================

-- 1. 工作流定义表
CREATE TABLE workflows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工作流ID',
    site_id BIGINT COMMENT '站点ID（NULL表示全局工作流）',
    
    -- 基本信息
    name VARCHAR(100) NOT NULL COMMENT '工作流名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '工作流代码',
    description TEXT COMMENT '工作流描述',
    
    -- 工作流配置
    workflow_type ENUM('CONTENT_APPROVAL', 'USER_APPROVAL', 'CUSTOM') DEFAULT 'CONTENT_APPROVAL' COMMENT '工作流类型',
    trigger_event VARCHAR(50) COMMENT '触发事件',
    
    -- 工作流状态
    status ENUM('ACTIVE', 'INACTIVE', 'DRAFT') DEFAULT 'DRAFT' COMMENT '状态',
    version INT DEFAULT 1 COMMENT '版本号',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    INDEX idx_site_id (site_id),
    INDEX idx_code (code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流定义表';

-- 2. 工作流节点表
CREATE TABLE workflow_nodes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '节点ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    
    -- 节点信息
    name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_type ENUM('START', 'APPROVAL', 'CONDITION', 'END') NOT NULL COMMENT '节点类型',
    
    -- 节点配置
    approver_type ENUM('USER', 'ROLE', 'CUSTOM') COMMENT '审批人类型',
    approver_ids JSON COMMENT '审批人ID列表',
    approval_mode ENUM('ANY', 'ALL', 'SEQUENTIAL') DEFAULT 'ANY' COMMENT '审批模式',
    
    -- 条件配置
    condition_expression TEXT COMMENT '条件表达式',
    
    -- 节点位置
    position_x INT COMMENT 'X坐标',
    position_y INT COMMENT 'Y坐标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_node_type (node_type),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流节点表';

-- 3. 工作流实例表
CREATE TABLE workflow_instances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '实例ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    
    -- 业务关联
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型',
    business_id BIGINT NOT NULL COMMENT '业务ID',
    business_title VARCHAR(200) COMMENT '业务标题',
    
    -- 实例状态
    status ENUM('RUNNING', 'APPROVED', 'REJECTED', 'CANCELLED', 'ERROR') DEFAULT 'RUNNING' COMMENT '状态',
    current_node_id BIGINT COMMENT '当前节点ID',
    
    -- 发起信息
    initiator_id BIGINT NOT NULL COMMENT '发起人ID',
    initiated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发起时间',
    
    -- 完成信息
    completed_at DATETIME COMMENT '完成时间',
    completion_note TEXT COMMENT '完成备注',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_business (business_type, business_id),
    INDEX idx_status (status),
    INDEX idx_initiator_id (initiator_id),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流实例表';

-- 4. 工作流任务表
CREATE TABLE workflow_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    instance_id BIGINT NOT NULL COMMENT '实例ID',
    node_id BIGINT NOT NULL COMMENT '节点ID',
    
    -- 任务信息
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_type ENUM('APPROVAL', 'NOTIFICATION', 'CUSTOM') DEFAULT 'APPROVAL' COMMENT '任务类型',
    
    -- 任务分配
    assignee_id BIGINT NOT NULL COMMENT '处理人ID',
    assignee_name VARCHAR(50) COMMENT '处理人名称',
    
    -- 任务状态
    status ENUM('PENDING', 'IN_PROGRESS', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '状态',
    
    -- 处理信息
    action VARCHAR(20) COMMENT '操作：APPROVE/REJECT/TRANSFER',
    comment TEXT COMMENT '处理意见',
    processed_at DATETIME COMMENT '处理时间',
    
    -- 转办信息
    transferred_to BIGINT COMMENT '转办给',
    transferred_at DATETIME COMMENT '转办时间',
    transfer_reason VARCHAR(500) COMMENT '转办原因',
    
    -- 时限
    due_date DATETIME COMMENT '截止时间',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    INDEX idx_instance_id (instance_id),
    INDEX idx_node_id (node_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流任务表';

