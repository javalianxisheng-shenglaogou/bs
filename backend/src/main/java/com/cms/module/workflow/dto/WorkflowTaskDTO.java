package com.cms.module.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流任务DTO
 */
@Data
public class WorkflowTaskDTO {

    private Long id;
    private Long instanceId;
    private Long nodeId;
    private String taskName;
    private String taskType;
    private Long assigneeId;
    private String assigneeName;
    private String status;
    private String action;
    private String comment;
    private LocalDateTime processedAt;
    private Long transferredTo;
    private String transferredToName;
    private LocalDateTime transferredAt;
    private String transferReason;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 关联的工作流实例信息
     */
    private String workflowName;
    private String businessType;
    private Long businessId;
    private String businessTitle;
    private String instanceStatus;
}

