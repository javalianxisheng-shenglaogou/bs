package com.cms.module.workflow.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工作流任务实体
 */
@Data
@Entity
@Table(name = "workflow_tasks")
public class WorkflowTask {

    /**
     * 任务ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 实例ID
     */
    @Column(name = "instance_id", nullable = false)
    private Long instanceId;

    /**
     * 节点ID
     */
    @Column(name = "node_id", nullable = false)
    private Long nodeId;

    /**
     * 任务名称
     */
    @Column(name = "task_name", nullable = false, length = 100)
    private String taskName;

    /**
     * 任务类型: APPROVAL, NOTIFICATION, CUSTOM
     */
    @Column(name = "task_type", length = 20)
    private String taskType;

    /**
     * 处理人ID
     */
    @Column(name = "assignee_id", nullable = false)
    private Long assigneeId;

    /**
     * 处理人名称
     */
    @Column(name = "assignee_name", length = 50)
    private String assigneeName;

    /**
     * 状态: PENDING, IN_PROGRESS, APPROVED, REJECTED, CANCELLED
     */
    @Column(length = 20)
    private String status;

    /**
     * 操作: APPROVE, REJECT, TRANSFER
     */
    @Column(length = 20)
    private String action;

    /**
     * 处理意见
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * 处理时间
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * 转办给
     */
    @Column(name = "transferred_to")
    private Long transferredTo;

    /**
     * 转办时间
     */
    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;

    /**
     * 转办原因
     */
    @Column(name = "transfer_reason", length = 500)
    private String transferReason;

    /**
     * 截止时间
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 删除标记
     */
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
        if (status == null) {
            status = "PENDING";
        }
        if (taskType == null) {
            taskType = "APPROVAL";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

