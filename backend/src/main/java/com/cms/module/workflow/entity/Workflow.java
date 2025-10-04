package com.cms.module.workflow.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 工作流定义实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "workflows")
public class Workflow extends BaseEntity {

    /**
     * 站点ID（NULL表示全局工作流）
     */
    @Column(name = "site_id")
    private Long siteId;

    /**
     * 工作流名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 工作流代码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * 工作流描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 工作流类型
     */
    @Column(name = "workflow_type", length = 20)
    private String workflowType;

    /**
     * 触发事件
     */
    @Column(name = "trigger_event", length = 50)
    private String triggerEvent;

    /**
     * 状态: ACTIVE, INACTIVE, DRAFT
     */
    @Column(length = 20)
    private String status;

    /**
     * 版本号
     */
    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer version;
}

