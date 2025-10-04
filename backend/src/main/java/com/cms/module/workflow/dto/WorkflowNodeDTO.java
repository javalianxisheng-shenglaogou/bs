package com.cms.module.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流节点DTO
 */
@Data
public class WorkflowNodeDTO {

    private Long id;
    private Long workflowId;
    private String name;
    private String nodeType;
    private String approverType;
    private List<Long> approverIds;
    private String approvalMode;
    private String conditionExpression;
    private Integer positionX;
    private Integer positionY;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

