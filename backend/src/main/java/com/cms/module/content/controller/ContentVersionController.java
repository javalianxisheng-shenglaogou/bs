package com.cms.module.content.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.content.dto.ContentVersionDTO;
import com.cms.module.content.service.ContentVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 内容版本控制接口
 */
@Slf4j
@RestController
@RequestMapping("/contents/{contentId}/versions")
@RequiredArgsConstructor
@Tag(name = "内容版本管理", description = "内容版本控制相关接口")
public class ContentVersionController {

    private final ContentVersionService versionService;

    @GetMapping
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "获取内容版本列表", description = "分页获取指定内容的所有历史版本")
    public ApiResponse<Page<ContentVersionDTO>> getVersionList(
            @Parameter(description = "内容ID") @PathVariable Long contentId,
            @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取版本列表请求: contentId={}, page={}, size={}", contentId, page, size);
        Page<ContentVersionDTO> result = versionService.getVersionList(contentId, page, size);
        return ApiResponse.success(result);
    }

    @GetMapping("/{versionNumber}")
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "获取指定版本", description = "获取内容的指定版本详情")
    public ApiResponse<ContentVersionDTO> getVersion(
            @Parameter(description = "内容ID") @PathVariable Long contentId,
            @Parameter(description = "版本号") @PathVariable Integer versionNumber) {
        
        log.info("获取版本详情请求: contentId={}, versionNumber={}", contentId, versionNumber);
        ContentVersionDTO result = versionService.getVersion(contentId, versionNumber);
        return ApiResponse.success(result);
    }

    @PostMapping("/{versionNumber}/restore")
    @PreAuthorize("hasAuthority('content:update')")
    @Operation(summary = "恢复到指定版本", description = "将内容恢复到指定的历史版本")
    public ApiResponse<Void> restoreVersion(
            @Parameter(description = "内容ID") @PathVariable Long contentId,
            @Parameter(description = "版本号") @PathVariable Integer versionNumber) {
        
        log.info("恢复版本请求: contentId={}, versionNumber={}", contentId, versionNumber);
        versionService.restoreVersion(contentId, versionNumber);
        return ApiResponse.success("版本恢复成功", null);
    }

    @GetMapping("/compare")
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "比较两个版本", description = "对比两个版本之间的差异")
    public ApiResponse<Map<String, Object>> compareVersions(
            @Parameter(description = "内容ID") @PathVariable Long contentId,
            @Parameter(description = "版本1") @RequestParam Integer version1,
            @Parameter(description = "版本2") @RequestParam Integer version2) {
        
        log.info("比较版本请求: contentId={}, version1={}, version2={}", contentId, version1, version2);
        Map<String, Object> result = versionService.compareVersions(contentId, version1, version2);
        return ApiResponse.success(result);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('content:view')")
    @Operation(summary = "获取版本统计", description = "获取内容的版本统计信息")
    public ApiResponse<Map<String, Object>> getVersionStatistics(
            @Parameter(description = "内容ID") @PathVariable Long contentId) {
        
        log.info("获取版本统计请求: contentId={}", contentId);
        Map<String, Object> result = versionService.getVersionStatistics(contentId);
        return ApiResponse.success(result);
    }

    @PostMapping("/{versionNumber}/tag")
    @PreAuthorize("hasAuthority('content:update')")
    @Operation(summary = "为指定版本打标签", description = "为某个历史版本设置标签，方便标记里程碑或重要版本")
    public ApiResponse<ContentVersionDTO> tagVersion(
            @Parameter(description = "内容ID") @PathVariable Long contentId,
            @Parameter(description = "版本号") @PathVariable Integer versionNumber,
            @Parameter(description = "标签名称") @RequestParam String tagName) {
        log.info("标记版本请求: contentId={}, versionNumber={}, tagName={}", contentId, versionNumber, tagName);
        ContentVersionDTO result = versionService.tagVersion(contentId, versionNumber, tagName);
        return ApiResponse.success("版本已打标", result);
    }

    @DeleteMapping("/{versionNumber}")
    @PreAuthorize("hasAuthority('content:delete')")
    @Operation(summary = "删除指定版本", description = "删除某个历史版本记录（不可恢复）")
    public ApiResponse<Void> deleteVersion(
            @Parameter(description = "内容ID") @PathVariable Long contentId,
            @Parameter(description = "版本号") @PathVariable Integer versionNumber) {
        log.info("删除版本请求: contentId={}, versionNumber={}", contentId, versionNumber);
        versionService.deleteVersion(contentId, versionNumber);
        return ApiResponse.success("版本删除成功", null);
    }
}

