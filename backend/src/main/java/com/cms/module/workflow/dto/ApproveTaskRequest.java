package com.cms.module.workflow.dto;

import lombok.Data;

/**
 * 审批任务请求
 */
@Data
public class ApproveTaskRequest {

    /**
     * 审批意见
     */
    private String comment;
}

