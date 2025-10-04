package com.cms.module.workflow.dto;

import lombok.Data;

/**
 * 启动工作流请求
 */
@Data
public class StartWorkflowRequest {

    /**
     * 工作流ID或代码
     */
    private String workflowCode;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务标题
     */
    private String businessTitle;
}

