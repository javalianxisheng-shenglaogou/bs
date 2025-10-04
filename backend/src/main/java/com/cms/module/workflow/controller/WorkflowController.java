package com.cms.module.workflow.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.workflow.dto.WorkflowDTO;
import com.cms.module.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流控制器
 */
@Slf4j
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
@Tag(name = "工作流管理", description = "工作流定义管理相关接口")
public class WorkflowController {

    private final WorkflowService workflowService;

    /**
     * 创建工作流
     */
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:create')")
    @Operation(summary = "创建工作流", description = "创建新的工作流定义")
    public ApiResponse<WorkflowDTO> createWorkflow(@Validated @RequestBody WorkflowDTO dto) {
        log.info("创建工作流请求: {}", dto);
        WorkflowDTO result = workflowService.createWorkflow(dto);
        return ApiResponse.success(result);
    }

    /**
     * 更新工作流
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:update')")
    @Operation(summary = "更新工作流", description = "更新工作流定义")
    public ApiResponse<WorkflowDTO> updateWorkflow(
            @PathVariable Long id,
            @Validated @RequestBody WorkflowDTO dto) {
        log.info("更新工作流请求: id={}", id);
        WorkflowDTO result = workflowService.updateWorkflow(id, dto);
        return ApiResponse.success(result);
    }

    /**
     * 删除工作流
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:delete')")
    @Operation(summary = "删除工作流", description = "删除工作流定义")
    public ApiResponse<Void> deleteWorkflow(@PathVariable Long id) {
        log.info("删除工作流请求: id={}", id);
        workflowService.deleteWorkflow(id);
        return ApiResponse.success();
    }

    /**
     * 获取工作流详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:view')")
    @Operation(summary = "获取工作流详情", description = "根据ID获取工作流详细信息")
    public ApiResponse<WorkflowDTO> getWorkflow(@PathVariable Long id) {
        log.info("获取工作流详情请求: id={}", id);
        WorkflowDTO result = workflowService.getWorkflowById(id);
        return ApiResponse.success(result);
    }

    /**
     * 根据代码获取工作流
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('workflow:view')")
    @Operation(summary = "根据代码获取工作流", description = "根据工作流代码获取工作流信息")
    public ApiResponse<WorkflowDTO> getWorkflowByCode(@PathVariable String code) {
        log.info("根据代码获取工作流请求: code={}", code);
        WorkflowDTO result = workflowService.getWorkflowByCode(code);
        return ApiResponse.success(result);
    }

    /**
     * 获取所有工作流
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('workflow:list')")
    @Operation(summary = "获取所有工作流", description = "获取所有工作流列表（不分页）")
    public ApiResponse<List<WorkflowDTO>> getAllWorkflows() {
        log.info("获取所有工作流列表请求");
        List<WorkflowDTO> result = workflowService.getAllWorkflows();
        return ApiResponse.success(result);
    }

    /**
     * 分页查询工作流
     */
    @GetMapping
    @PreAuthorize("hasAuthority('workflow:list')")
    @Operation(summary = "分页查询工作流", description = "根据条件分页查询工作流列表")
    public ApiResponse<Page<WorkflowDTO>> getWorkflows(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("分页查询工作流请求: page={}, size={}", page, size);
        Page<WorkflowDTO> result = workflowService.getWorkflows(page, size, sortBy, sortDir);
        return ApiResponse.success(result);
    }

    /**
     * 更新工作流状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('workflow:update')")
    @Operation(summary = "更新工作流状态", description = "更新工作流的状态")
    public ApiResponse<Void> updateWorkflowStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("更新工作流状态请求: id={}, status={}", id, status);
        workflowService.updateWorkflowStatus(id, status);
        return ApiResponse.success();
    }
}

