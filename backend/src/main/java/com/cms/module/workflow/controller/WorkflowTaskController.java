package com.cms.module.workflow.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.workflow.dto.ApproveTaskRequest;
import com.cms.module.workflow.dto.RejectTaskRequest;
import com.cms.module.workflow.dto.WorkflowTaskDTO;
import com.cms.module.workflow.service.WorkflowTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 工作流任务控制器
 */
@Slf4j
@RestController
@RequestMapping("/workflow-tasks")
@RequiredArgsConstructor
@Tag(name = "工作流任务", description = "工作流任务管理相关接口")
public class WorkflowTaskController {

    private final WorkflowTaskService taskService;

    /**
     * 获取我的待办任务
     */
    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取我的待办任务", description = "获取当前用户的待办任务列表")
    public ApiResponse<Page<WorkflowTaskDTO>> getMyPendingTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取我的待办任务请求: page={}, size={}", page, size);
        Page<WorkflowTaskDTO> result = taskService.getMyPendingTasks(page, size);
        return ApiResponse.success(result);
    }

    /**
     * 获取我的已办任务
     */
    @GetMapping("/completed")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取我的已办任务", description = "获取当前用户的已办任务列表")
    public ApiResponse<Page<WorkflowTaskDTO>> getMyCompletedTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取我的已办任务请求: page={}, size={}", page, size);
        Page<WorkflowTaskDTO> result = taskService.getMyCompletedTasks(page, size);
        return ApiResponse.success(result);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取任务详情", description = "根据ID获取任务详细信息")
    public ApiResponse<WorkflowTaskDTO> getTask(@PathVariable Long id) {
        log.info("获取任务详情请求: id={}", id);
        WorkflowTaskDTO result = taskService.getTaskById(id);
        return ApiResponse.success(result);
    }

    /**
     * 通过任务
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "通过任务", description = "审批通过任务")
    public ApiResponse<Void> approveTask(
            @PathVariable Long id,
            @Validated @RequestBody ApproveTaskRequest request) {
        log.info("通过任务请求: id={}", id);
        taskService.approveTask(id, request);
        return ApiResponse.success();
    }

    /**
     * 拒绝任务
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "拒绝任务", description = "审批拒绝任务")
    public ApiResponse<Void> rejectTask(
            @PathVariable Long id,
            @Validated @RequestBody RejectTaskRequest request) {
        log.info("拒绝任务请求: id={}", id);
        taskService.rejectTask(id, request);
        return ApiResponse.success();
    }
}

