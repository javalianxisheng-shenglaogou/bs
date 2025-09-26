package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import com.multisite.cms.common.PageResult;
import com.multisite.cms.dto.content.ContentCreateRequest;
import com.multisite.cms.dto.content.ContentResponse;
import com.multisite.cms.dto.content.ContentUpdateRequest;
import com.multisite.cms.entity.Content;
import com.multisite.cms.entity.ContentCategory;
import com.multisite.cms.entity.Site;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;
import com.multisite.cms.service.ContentCategoryService;
import com.multisite.cms.service.ContentService;
import com.multisite.cms.service.SiteService;
import com.multisite.cms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容管理控制器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
@Tag(name = "内容管理", description = "内容管理相关接口")
@SecurityRequirement(name = "bearerAuth")
public class ContentController {

    private final ContentService contentService;
    private final ContentCategoryService contentCategoryService;
    private final SiteService siteService;
    private final UserService userService;

    /**
     * 分页查询内容
     */
    @GetMapping
    @Operation(summary = "分页查询内容", description = "分页查询内容列表，支持多种筛选条件")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<PageResult<ContentResponse>> getContents(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "排序方向", example = "desc") @RequestParam(defaultValue = "desc") String direction,
            @Parameter(description = "站点ID") @RequestParam(required = false) Long siteId,
            @Parameter(description = "内容状态") @RequestParam(required = false) ContentStatus status,
            @Parameter(description = "内容类型") @RequestParam(required = false) ContentType contentType,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword) {
        
        log.debug("Querying contents with filters: siteId={}, status={}, type={}, categoryId={}, keyword={}", 
                siteId, status, contentType, categoryId, keyword);
        
        PageResult<Content> contentPage = contentService.getContents(
                siteId, page, size, sort, direction, status, contentType, categoryId, keyword);
        
        // 转换为响应DTO
        List<ContentResponse> contentResponses = contentPage.getContent().stream()
                .map(ContentResponse::fromSummary) // 使用摘要版本，不包含完整内容
                .collect(Collectors.toList());

        PageResult<ContentResponse> responsePage = PageResult.<ContentResponse>builder()
                .content(contentResponses)
                .totalElements(contentPage.getTotalElements())
                .page(contentPage.getPage())
                .size(contentPage.getSize())
                .totalPages(contentPage.getTotalPages())
                .build();
        
        return ApiResponse.success("查询成功", responsePage);
    }

    /**
     * 获取已发布的内容
     */
    @GetMapping("/published")
    @Operation(summary = "获取已发布的内容", description = "获取已发布的内容列表")
    public ApiResponse<PageResult<ContentResponse>> getPublishedContents(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "publishAt") @RequestParam(defaultValue = "publishAt") String sort,
            @Parameter(description = "排序方向", example = "desc") @RequestParam(defaultValue = "desc") String direction,
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId) {
        
        log.debug("Querying published contents for site: {}", siteId);
        
        // 暂时使用getContents方法，后续实现getPublishedContents
        PageResult<Content> contentPage = contentService.getContents(siteId, page, size, sort, direction, ContentStatus.PUBLISHED, null, null, null);
        
        // 转换为响应DTO
        List<ContentResponse> contentResponses = contentPage.getContent().stream()
                .map(ContentResponse::fromSummary)
                .collect(Collectors.toList());

        PageResult<ContentResponse> responsePage = PageResult.<ContentResponse>builder()
                .content(contentResponses)
                .totalElements(contentPage.getTotalElements())
                .page(contentPage.getPage())
                .size(contentPage.getSize())
                .totalPages(contentPage.getTotalPages())
                .build();
        
        return ApiResponse.success("查询成功", responsePage);
    }

    /**
     * 根据ID获取内容详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取内容详情", description = "根据ID获取内容的详细信息")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<ContentResponse> getContentById(
            @Parameter(description = "内容ID", required = true) @PathVariable Long id) {
        
        log.debug("Getting content by ID: {}", id);
        
        Content content = contentService.getContentById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));
        
        ContentResponse response = ContentResponse.from(content);
        return ApiResponse.success("获取成功", response);
    }

    /**
     * 根据slug获取内容详情
     */
    @GetMapping("/slug/{siteId}/{slug}")
    @Operation(summary = "根据slug获取内容", description = "根据站点ID和slug获取内容详情")
    public ApiResponse<ContentResponse> getContentBySlug(
            @Parameter(description = "站点ID", required = true) @PathVariable Long siteId,
            @Parameter(description = "URL别名", required = true) @PathVariable String slug) {
        
        log.debug("Getting content by slug: siteId={}, slug={}", siteId, slug);
        
        Content content = contentService.getContentBySlug(siteId, slug)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + slug));
        
        // 增加浏览次数 - 暂时注释，后续实现
        // contentService.incrementViewCount(content.getId());
        content.incrementViewCount();
        
        ContentResponse response = ContentResponse.from(content);
        return ApiResponse.success("获取成功", response);
    }

    /**
     * 创建内容
     */
    @PostMapping
    @Operation(summary = "创建内容", description = "创建新的内容")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<ContentResponse> createContent(
            @Valid @RequestBody ContentCreateRequest request,
            Authentication authentication) {
        
        log.info("Creating content: {}", request.getTitle());
        
        String currentUser = authentication.getName();
        
        // 验证站点是否存在
        Site site = siteService.getSiteById(request.getSiteId())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + request.getSiteId()));
        
        // 验证分类是否存在（如果提供了分类ID）
        ContentCategory category = null;
        if (request.getCategoryId() != null) {
            category = contentCategoryService.getCategoryById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在: " + request.getCategoryId()));
        }
        
        // 获取当前用户信息
        User author = userService.getUserByUsername(currentUser)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + currentUser));
        
        // 构建内容实体
        Content content = Content.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .summary(request.getSummary())
                .content(request.getContent())
                .contentType(request.getContentType())
                .status(request.getStatus())
                .site(site)
                .category(category)
                .author(author)
                .isTop(request.getIsTop())
                .publishAt(request.getPublishAt())
                .featuredImage(request.getFeaturedImage())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .seoKeywords(request.getSeoKeywords())
                .build();
        
        Content savedContent = contentService.createContent(content, currentUser);
        ContentResponse response = ContentResponse.from(savedContent);
        
        return ApiResponse.success("创建成功", response);
    }

    /**
     * 更新内容
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新内容", description = "更新指定ID的内容")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<ContentResponse> updateContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ContentUpdateRequest request,
            Authentication authentication) {
        
        log.info("Updating content: {}", id);
        
        String currentUser = authentication.getName();
        
        // 验证分类是否存在（如果提供了分类ID）
        ContentCategory category = null;
        if (request.getCategoryId() != null) {
            category = contentCategoryService.getCategoryById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在: " + request.getCategoryId()));
        }
        
        // 构建更新内容实体
        Content updateContent = Content.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .summary(request.getSummary())
                .content(request.getContent())
                .contentType(request.getContentType())
                .status(request.getStatus())
                .category(category)
                .isTop(request.getIsTop())
                .publishAt(request.getPublishAt())
                .featuredImage(request.getFeaturedImage())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .seoKeywords(request.getSeoKeywords())
                .build();
        
        Content savedContent = contentService.updateContent(id, updateContent, currentUser);
        ContentResponse response = ContentResponse.from(savedContent);
        
        return ApiResponse.success("更新成功", response);
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除内容", description = "删除指定ID的内容（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> deleteContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Deleting content: {}", id);
        
        String currentUser = authentication.getName();
        contentService.deleteContent(id, currentUser);
        
        return ApiResponse.success("删除成功");
    }

    /**
     * 批量删除内容
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除内容", description = "批量删除多个内容（软删除）")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> deleteContents(
            @Parameter(description = "内容ID列表", required = true) @RequestBody List<Long> contentIds,
            Authentication authentication) {
        
        log.info("Batch deleting contents: {}", contentIds);
        
        String currentUser = authentication.getName();
        contentService.deleteContents(contentIds, currentUser);
        
        return ApiResponse.success("批量删除成功");
    }

    /**
     * 发布内容
     */
    @PostMapping("/{id}/publish")
    @Operation(summary = "发布内容", description = "发布指定ID的内容")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> publishContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long id,
            @Parameter(description = "发布时间") @RequestParam(required = false) LocalDateTime publishAt,
            Authentication authentication) {
        
        log.info("Publishing content: {} at {}", id, publishAt);
        
        String currentUser = authentication.getName();
        contentService.publishContent(id, currentUser);
        
        return ApiResponse.success("发布成功");
    }

    /**
     * 批量发布内容
     */
    @PostMapping("/batch/publish")
    @Operation(summary = "批量发布内容", description = "批量发布多个内容")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> publishContents(
            @Parameter(description = "内容ID列表", required = true) @RequestBody List<Long> contentIds,
            @Parameter(description = "发布时间") @RequestParam(required = false) LocalDateTime publishAt) {
        
        log.info("Batch publishing contents: {} at {}", contentIds, publishAt);

        // 暂时注释，后续实现批量发布
        // contentService.publishContents(contentIds, publishAt);

        return ApiResponse.success("批量发布功能暂未实现");
    }

    /**
     * 点赞内容
     */
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞内容", description = "为指定内容点赞")
    public ApiResponse<Void> likeContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long id) {
        
        log.debug("Liking content: {}", id);

        // 暂时注释，后续实现点赞功能
        // contentService.likeContent(id);

        return ApiResponse.success("点赞功能暂未实现");
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞", description = "取消对指定内容的点赞")
    public ApiResponse<Void> unlikeContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long id) {
        
        log.debug("Unliking content: {}", id);

        // 暂时注释，后续实现取消点赞功能
        // contentService.unlikeContent(id);

        return ApiResponse.success("取消点赞功能暂未实现");
    }

    /**
     * 检查slug是否可用
     */
    @GetMapping("/check-slug")
    @Operation(summary = "检查slug可用性", description = "检查URL别名是否可用")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR') or hasRole('AUTHOR')")
    public ApiResponse<Boolean> checkSlugAvailability(
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId,
            @Parameter(description = "URL别名", required = true) @RequestParam String slug,
            @Parameter(description = "排除的内容ID") @RequestParam(required = false) Long excludeContentId) {
        
        log.debug("Checking slug availability: siteId={}, slug={}, excludeId={}", siteId, slug, excludeContentId);

        // 暂时返回true，后续实现slug检查功能
        boolean available = true; // contentService.isSlugAvailable(siteId, slug, excludeContentId);

        return ApiResponse.success("检查完成", available);
    }

    /**
     * 获取内容统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取内容统计", description = "获取指定站点的内容统计信息")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<ContentService.ContentStatistics> getContentStats(
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId) {

        log.debug("Getting content stats for site: {}", siteId);

        ContentService.ContentStatistics stats = contentService.getContentStats(siteId);
        
        return ApiResponse.success("获取成功", stats);
    }
}
