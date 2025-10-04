package com.cms.module.workflow.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 审批历史实体
 */
@Data
@Entity
@Table(name = "workflow_approval_history")
public class WorkflowApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 工作流实例ID
     */
    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    /**
     * 任务ID
     */
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    /**
     * 节点ID
     */
    @Column(name = "node_id", nullable = false)
    private Long nodeId;

    /**
     * 节点名称
     */
    @Column(name = "node_name", nullable = false, length = 100)
    private String nodeName;

    /**
     * 审批人ID
     */
    @Column(name = "approver_id", nullable = false)
    private Long approverId;

    /**
     * 审批人姓名
     */
    @Column(name = "approver_name", nullable = false, length = 100)
    private String approverName;

    /**
     * 操作: APPROVE-通过, REJECT-拒绝, TRANSFER-转交
     */
    @Column(nullable = false, length = 20)
    private String action;

    /**
     * 审批意见
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * 附件
     */
    @Column(columnDefinition = "JSON")
    private String attachments;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

