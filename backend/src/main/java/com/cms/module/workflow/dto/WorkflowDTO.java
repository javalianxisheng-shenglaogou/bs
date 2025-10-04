package com.cms.module.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流DTO
 */
@Data
public class WorkflowDTO {

    private Long id;
    private Long siteId;
    private String name;
    private String code;
    private String description;
    private String workflowType;
    private String triggerEvent;
    private String status;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    /**
     * 工作流节点列表
     */
    private List<WorkflowNodeDTO> nodes;
}

