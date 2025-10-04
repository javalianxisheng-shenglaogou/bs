package com.cms.module.workflow.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 工作流节点实体
 */
@Data
@Entity
@Table(name = "workflow_nodes")
public class WorkflowNode {

    /**
     * 节点ID
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
     * 节点名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 节点类型: START, APPROVAL, CONDITION, END
     */
    @Column(name = "node_type", nullable = false, length = 20)
    private String nodeType;

    /**
     * 审批人类型: USER, ROLE, CUSTOM
     */
    @Column(name = "approver_type", length = 20)
    private String approverType;

    /**
     * 审批人ID列表(JSON格式)
     */
    @Column(name = "approver_ids", columnDefinition = "JSON")
    private String approverIds;

    /**
     * 审批模式: ANY, ALL, SEQUENTIAL
     */
    @Column(name = "approval_mode", length = 20)
    private String approvalMode;

    /**
     * 条件表达式
     */
    @Column(name = "condition_expression", columnDefinition = "TEXT")
    private String conditionExpression;

    /**
     * X坐标
     */
    @Column(name = "position_x")
    private Integer positionX;

    /**
     * Y坐标
     */
    @Column(name = "position_y")
    private Integer positionY;

    /**
     * 排序
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;

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
        if (sortOrder == null) {
            sortOrder = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

