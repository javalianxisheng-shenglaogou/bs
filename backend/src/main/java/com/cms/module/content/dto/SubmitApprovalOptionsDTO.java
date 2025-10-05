package com.cms.module.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 提交审批选项DTO
 */
@Data
@Schema(description = "提交审批选项")
public class SubmitApprovalOptionsDTO {

    @NotNull(message = "内容ID不能为空")
    @Schema(description = "内容ID")
    private Long contentId;

    @NotBlank(message = "工作流代码不能为空")
    @Schema(description = "工作流代码")
    private String workflowCode;

    @NotBlank(message = "审批模式不能为空")
    @Schema(description = "审批模式：SERIAL-串行审批，PARALLEL-并行审批")
    private String approvalMode;

    @NotEmpty(message = "审批人列表不能为空")
    @Schema(description = "审批人ID列表")
    private List<Long> approverIds;

    @Schema(description = "审批说明")
    private String comment;
}

