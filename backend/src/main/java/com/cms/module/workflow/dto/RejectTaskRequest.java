package com.cms.module.workflow.dto;

import lombok.Data;

/**
 * 拒绝任务请求
 */
@Data
public class RejectTaskRequest {

    /**
     * 拒绝原因
     */
    private String comment;
}

