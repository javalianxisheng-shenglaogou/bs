package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import com.multisite.cms.common.PageResult;
import com.multisite.cms.dto.category.CategoryCreateRequest;
import com.multisite.cms.dto.category.CategoryResponse;
import com.multisite.cms.dto.category.CategoryUpdateRequest;
import com.multisite.cms.entity.ContentCategory;
import com.multisite.cms.entity.Site;
import com.multisite.cms.service.ContentCategoryService;
import com.multisite.cms.service.SiteService;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容分类控制器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "内容分类管理", description = "内容分类管理相关接口")
@SecurityRequirement(name = "bearerAuth")
public class ContentCategoryController {

    private final ContentCategoryService contentCategoryService;
    private final SiteService siteService;

    /**
     * 分页查询分类
     */
    @GetMapping
    @Operation(summary = "分页查询分类", description = "分页查询内容分类列表")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<PageResult<CategoryResponse>> getCategories(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "sortOrder") @RequestParam(defaultValue = "sortOrder") String sort,
            @Parameter(description = "排序方向", example = "asc") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword) {
        
        log.debug("Querying categories: siteId={}, page={}, size={}, keyword={}", siteId, page, size, keyword);
        
        PageResult<ContentCategory> categoryPage = contentCategoryService.getCategories(
                siteId, page, size, sort, direction, keyword);
        
        // 转换为响应DTO
        List<CategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(CategoryResponse::fromSimple)
                .collect(Collectors.toList());

        PageResult<CategoryResponse> responsePage = PageResult.<CategoryResponse>builder()
                .content(categoryResponses)
                .totalElements(categoryPage.getTotalElements())
                .page(categoryPage.getPage())
                .size(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .build();
        
        return ApiResponse.success("查询成功", responsePage);
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取指定站点的分类树结构")
    public ApiResponse<List<CategoryResponse>> getCategoryTree(
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId) {
        
        log.debug("Getting category tree for site: {}", siteId);
        
        List<ContentCategory> rootCategories = contentCategoryService.getRootCategories(siteId);
        
        List<CategoryResponse> categoryTree = rootCategories.stream()
                .map(category -> CategoryResponse.fromTree(category, true))
                .collect(Collectors.toList());
        
        return ApiResponse.success("获取成功", categoryTree);
    }

    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类的详细信息")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<CategoryResponse> getCategoryById(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        
        log.debug("Getting category by ID: {}", id);
        
        ContentCategory category = contentCategoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));
        
        CategoryResponse response = CategoryResponse.from(category);
        return ApiResponse.success("获取成功", response);
    }

    /**
     * 根据代码获取分类
     */
    @GetMapping("/code/{siteId}/{code}")
    @Operation(summary = "根据代码获取分类", description = "根据站点ID和分类代码获取分类信息")
    public ApiResponse<CategoryResponse> getCategoryByCode(
            @Parameter(description = "站点ID", required = true) @PathVariable Long siteId,
            @Parameter(description = "分类代码", required = true) @PathVariable String code) {
        
        log.debug("Getting category by code: siteId={}, code={}", siteId, code);
        
        ContentCategory category = contentCategoryService.getCategoryByCode(siteId, code)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + code));
        
        CategoryResponse response = CategoryResponse.from(category);
        return ApiResponse.success("获取成功", response);
    }

    /**
     * 根据路径获取分类
     */
    @GetMapping("/path/{siteId}")
    @Operation(summary = "根据路径获取分类", description = "根据站点ID和分类路径获取分类信息")
    public ApiResponse<CategoryResponse> getCategoryByPath(
            @Parameter(description = "站点ID", required = true) @PathVariable Long siteId,
            @Parameter(description = "分类路径", required = true) @RequestParam String path) {
        
        log.debug("Getting category by path: siteId={}, path={}", siteId, path);
        
        ContentCategory category = contentCategoryService.getCategoryByPath(siteId, path)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + path));
        
        CategoryResponse response = CategoryResponse.from(category);
        return ApiResponse.success("获取成功", response);
    }

    /**
     * 获取子分类列表
     */
    @GetMapping("/{parentId}/children")
    @Operation(summary = "获取子分类", description = "获取指定父分类的子分类列表")
    public ApiResponse<List<CategoryResponse>> getChildCategories(
            @Parameter(description = "父分类ID", required = true) @PathVariable Long parentId,
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId) {
        
        log.debug("Getting child categories: parentId={}, siteId={}", parentId, siteId);
        
        List<ContentCategory> childCategories = contentCategoryService.getChildCategories(siteId, parentId);
        
        List<CategoryResponse> responses = childCategories.stream()
                .map(CategoryResponse::fromSimple)
                .collect(Collectors.toList());
        
        return ApiResponse.success("获取成功", responses);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "创建新的内容分类")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            Authentication authentication) {
        
        log.info("Creating category: {}", request.getName());
        
        String currentUser = authentication.getName();
        
        // 验证站点是否存在
        Site site = siteService.getSiteById(request.getSiteId())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + request.getSiteId()));
        
        // 验证父分类是否存在（如果提供了父分类ID）
        ContentCategory parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = contentCategoryService.getCategoryById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("父分类不存在: " + request.getParentCategoryId()));
        }
        
        // 构建分类实体
        ContentCategory category = ContentCategory.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .site(site)
                .parent(parentCategory)
                .sortOrder(request.getSortOrder())
                .build();
        
        ContentCategory savedCategory = contentCategoryService.createCategory(category, currentUser);
        CategoryResponse response = CategoryResponse.from(savedCategory);
        
        return ApiResponse.success("创建成功", response);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新指定ID的分类")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<CategoryResponse> updateCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request,
            Authentication authentication) {
        
        log.info("Updating category: {}", id);
        
        String currentUser = authentication.getName();
        
        // 验证父分类是否存在（如果提供了父分类ID）
        ContentCategory parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = contentCategoryService.getCategoryById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("父分类不存在: " + request.getParentCategoryId()));
        }
        
        // 构建更新分类实体
        ContentCategory updateCategory = ContentCategory.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .parent(parentCategory)
                .sortOrder(request.getSortOrder())
                .build();
        
        ContentCategory savedCategory = contentCategoryService.updateCategory(id, updateCategory, currentUser);
        CategoryResponse response = CategoryResponse.from(savedCategory);
        
        return ApiResponse.success("更新成功", response);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除指定ID的分类（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Deleting category: {}", id);
        
        String currentUser = authentication.getName();
        contentCategoryService.deleteCategory(id, currentUser);
        
        return ApiResponse.success("删除成功");
    }

    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除分类", description = "批量删除多个分类（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> deleteCategories(
            @Parameter(description = "分类ID列表", required = true) @RequestBody List<Long> categoryIds,
            Authentication authentication) {
        
        log.info("Batch deleting categories: {}", categoryIds);
        
        String currentUser = authentication.getName();
        contentCategoryService.deleteCategories(categoryIds, currentUser);
        
        return ApiResponse.success("批量删除成功");
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/{id}/sort")
    @Operation(summary = "更新分类排序", description = "更新指定分类的排序值")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Void> updateCategorySortOrder(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id,
            @Parameter(description = "排序值", required = true) @RequestParam Integer sortOrder) {
        
        log.info("Updating category sort order: {} -> {}", id, sortOrder);
        
        contentCategoryService.updateCategorySortOrder(id, sortOrder);
        
        return ApiResponse.success("排序更新成功");
    }

    /**
     * 检查分类代码是否可用
     */
    @GetMapping("/check-code")
    @Operation(summary = "检查分类代码可用性", description = "检查分类代码是否可用")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<Boolean> checkCodeAvailability(
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId,
            @Parameter(description = "分类代码", required = true) @RequestParam String code,
            @Parameter(description = "排除的分类ID") @RequestParam(required = false) Long excludeCategoryId) {
        
        log.debug("Checking category code availability: siteId={}, code={}, excludeId={}", siteId, code, excludeCategoryId);
        
        boolean available = contentCategoryService.isCategoryCodeAvailable(siteId, code, excludeCategoryId);
        
        return ApiResponse.success("检查完成", available);
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取分类统计", description = "获取指定站点的分类统计信息")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('SITE_ADMIN') or hasRole('EDITOR')")
    public ApiResponse<ContentCategoryService.CategoryStats> getCategoryStats(
            @Parameter(description = "站点ID", required = true) @RequestParam Long siteId) {
        
        log.debug("Getting category stats for site: {}", siteId);
        
        ContentCategoryService.CategoryStats stats = contentCategoryService.getCategoryStats(siteId);
        
        return ApiResponse.success("获取成功", stats);
    }
}
