package com.cms.module.content.controller;

import com.cms.common.base.ApiResponse;
import com.cms.common.base.Page;
import com.cms.module.content.dto.ContentDTO;
import com.cms.module.content.dto.ContentQueryDTO;
import com.cms.module.content.dto.SubmitApprovalOptionsDTO;
import com.cms.module.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容控制器
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@Tag(name = "内容管理", description = "内容管理相关接口")
public class ContentController {

    private final ContentService contentService;

    /**
     * 创建内容
     */
    @PostMapping
    @PreAuthorize("hasAuthority('content:create')")
    @Operation(summary = "创建内容", description = "创建新的内容")
    public ApiResponse<ContentDTO> createContent(@Validated @RequestBody ContentDTO contentDTO) {
        log.info("创建内容请求: {}", contentDTO);
        ContentDTO result = contentService.createContent(contentDTO);
        return ApiResponse.success(result);
    }

    /**
     * 更新内容
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('content:update')")
    @Operation(summary = "更新内容", description = "更新指定ID的内容")
    public ApiResponse<ContentDTO> updateContent(
            @PathVariable Long id,
            @Validated @RequestBody ContentDTO contentDTO) {
        log.info("更新内容请求: id={}, data={}", id, contentDTO);
        ContentDTO result = contentService.updateContent(id, contentDTO);
        return ApiResponse.success(result);
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('content:delete')")
    @Operation(summary = "删除内容", description = "删除指定ID的内容（软删除）")
    public ApiResponse<Void> deleteContent(@PathVariable Long id) {
        log.info("删除内容请求: id={}", id);
        contentService.deleteContent(id);
        return ApiResponse.success(null);
    }

    /**
     * 根据ID获取内容
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "获取内容详情", description = "根据ID获取内容详情")
    public ApiResponse<ContentDTO> getContent(@PathVariable Long id) {
        log.info("获取内容详情请求: id={}", id);
        ContentDTO result = contentService.getContentById(id);
        return ApiResponse.success(result);
    }

    /**
     * 根据slug获取内容
     */
    @GetMapping("/slug/{siteId}/{slug}")
    @Operation(summary = "根据slug获取内容", description = "根据站点ID和slug获取内容")
    public ApiResponse<ContentDTO> getContentBySlug(
            @PathVariable Long siteId,
            @PathVariable String slug) {
        log.info("根据slug获取内容请求: siteId={}, slug={}", siteId, slug);
        ContentDTO result = contentService.getContentBySlug(siteId, slug);
        return ApiResponse.success(result);
    }

    /**
     * 分页查询内容
     */
    @GetMapping
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "分页查询内容", description = "根据条件分页查询内容列表")
    public ApiResponse<Page<ContentDTO>> getContents(
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Boolean isTop,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("分页查询内容请求: siteId={}, categoryId={}, title={}, contentType={}, status={}, page={}, size={}",
                siteId, categoryId, title, contentType, status, page, size);

        ContentQueryDTO queryDTO = new ContentQueryDTO();
        queryDTO.setSiteId(siteId);
        queryDTO.setCategoryId(categoryId);
        queryDTO.setTitle(title);
        queryDTO.setContentType(contentType);
        queryDTO.setStatus(status);
        queryDTO.setAuthorId(authorId);
        queryDTO.setIsTop(isTop);
        queryDTO.setIsFeatured(isFeatured);
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setSortBy(sortBy);
        queryDTO.setSortDir(sortDir);

        Page<ContentDTO> result = contentService.getContents(queryDTO);
        return ApiResponse.success(result);
    }

    /**
     * 获取所有内容列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "获取所有内容", description = "获取指定站点的所有内容列表（不分页）")
    public ApiResponse<List<ContentDTO>> getAllContents(@RequestParam Long siteId) {
        log.info("获取所有内容列表请求: siteId={}", siteId);
        List<ContentDTO> result = contentService.getAllContents(siteId);
        return ApiResponse.success(result);
    }

    /**
     * 更新内容状态
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('content:update')")
    @Operation(summary = "更新内容状态", description = "更新指定内容的状态")
    public ApiResponse<Void> updateContentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("更新内容状态请求: id={}, status={}", id, status);
        contentService.updateContentStatus(id, status);
        return ApiResponse.success(null);
    }

    /**
     * 提交审批（简单版本）
     */
    @PostMapping("/{id}/submit-approval")
    @PreAuthorize("hasAuthority('content:create')")
    @Operation(summary = "提交审批", description = "提交内容审批")
    public ApiResponse<Void> submitApproval(@PathVariable Long id) {
        log.info("提交内容审批请求: id={}", id);
        contentService.submitApproval(id);
        return ApiResponse.success(null);
    }

    /**
     * 提交审批（带选项）
     */
    @PostMapping("/{id}/submit-approval-with-options")
    @PreAuthorize("hasAuthority('content:create')")
    @Operation(summary = "提交审批（带选项）", description = "提交内容审批，支持选择工作流、审批人和审批模式")
    public ApiResponse<Void> submitApprovalWithOptions(
            @PathVariable Long id,
            @RequestBody @Validated SubmitApprovalOptionsDTO options) {
        log.info("提交内容审批请求（带选项）: id={}, options={}", id, options);
        contentService.submitApprovalWithOptions(id, options);
        return ApiResponse.success(null);
    }

    /**
     * 撤回审批
     */
    @PostMapping("/{id}/withdraw-approval")
    @PreAuthorize("hasAuthority('content:create')")
    @Operation(summary = "撤回审批", description = "撤回内容审批")
    public ApiResponse<Void> withdrawApproval(@PathVariable Long id) {
        log.info("撤回内容审批请求: id={}", id);
        contentService.withdrawApproval(id);
        return ApiResponse.success(null);
    }
}

