package com.cms.module.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流实例DTO
 */
@Data
public class WorkflowInstanceDTO {

    private Long id;
    private Long workflowId;
    private String workflowName;
    private String businessType;
    private Long businessId;
    private String businessTitle;
    private String status;
    private Long currentNodeId;
    private String currentNodeName;
    private Long initiatorId;
    private String initiatorName;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String completionNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

