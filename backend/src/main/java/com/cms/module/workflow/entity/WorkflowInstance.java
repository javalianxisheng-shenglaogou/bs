package com.cms.module.workflow.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工作流实例实体
 */
@Data
@Entity
@Table(name = "workflow_instances")
public class WorkflowInstance {

    /**
     * 实例ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 工作流ID
     */
    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    /**
     * 业务类型
     */
    @Column(name = "business_type", nullable = false, length = 50)
    private String businessType;

    /**
     * 业务ID
     */
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    /**
     * 业务标题
     */
    @Column(name = "business_title", length = 200)
    private String businessTitle;

    /**
     * 状态: RUNNING, APPROVED, REJECTED, CANCELLED, ERROR
     */
    @Column(length = 20)
    private String status;

    /**
     * 当前节点ID
     */
    @Column(name = "current_node_id")
    private Long currentNodeId;

    /**
     * 发起人ID
     */
    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId;

    /**
     * 发起时间
     */
    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    /**
     * 完成时间
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 完成备注
     */
    @Column(name = "completion_note", columnDefinition = "TEXT")
    private String completionNote;

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
        if (initiatedAt == null) {
            initiatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "RUNNING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

