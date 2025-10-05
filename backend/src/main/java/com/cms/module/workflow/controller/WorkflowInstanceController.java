package com.cms.module.workflow.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.workflow.dto.StartWorkflowRequest;
import com.cms.module.workflow.dto.WorkflowInstanceDTO;
import com.cms.module.workflow.service.WorkflowInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 工作流实例控制器
 */
@Slf4j
@RestController
@RequestMapping("/workflow-instances")
@RequiredArgsConstructor
@Tag(name = "工作流实例", description = "工作流实例管理相关接口")
public class WorkflowInstanceController {

    private final WorkflowInstanceService instanceService;

    /**
     * 启动工作流
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "启动工作流", description = "启动一个新的工作流实例")
    public ApiResponse<WorkflowInstanceDTO> startWorkflow(@Validated @RequestBody StartWorkflowRequest request) {
        log.info("启动工作流请求: {}", request);
        WorkflowInstanceDTO result = instanceService.startWorkflow(request);
        return ApiResponse.success(result);
    }

    /**
     * 获取实例详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取实例详情", description = "根据ID获取工作流实例详细信息")
    public ApiResponse<WorkflowInstanceDTO> getInstance(@PathVariable Long id) {
        log.info("获取实例详情请求: id={}", id);
        WorkflowInstanceDTO result = instanceService.getInstanceById(id);
        return ApiResponse.success(result);
    }

    /**
     * 分页查询实例
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "分页查询实例", description = "根据条件分页查询工作流实例列表")
    public ApiResponse<Page<WorkflowInstanceDTO>> getInstances(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("分页查询实例请求: page={}, size={}", page, size);
        Page<WorkflowInstanceDTO> result = instanceService.getInstances(page, size, sortBy, sortDir);
        return ApiResponse.success(result);
    }

    /**
     * 取消实例
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "取消实例", description = "取消一个运行中的工作流实例")
    public ApiResponse<Void> cancelInstance(@PathVariable Long id) {
        log.info("取消实例请求: id={}", id);
        instanceService.cancelInstance(id);
        return ApiResponse.success();
    }
}

