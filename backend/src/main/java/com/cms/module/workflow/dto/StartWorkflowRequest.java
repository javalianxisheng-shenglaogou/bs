package com.cms.module.workflow.dto;

import lombok.Data;
import java.util.List;

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

    /**
     * 审批模式：SERIAL-串行审批，PARALLEL-并行审批
     */
    private String approvalMode;

    /**
     * 审批人ID列表
     */
    private List<Long> approverIds;

    /**
     * 审批说明
     */
    private String comment;
}

