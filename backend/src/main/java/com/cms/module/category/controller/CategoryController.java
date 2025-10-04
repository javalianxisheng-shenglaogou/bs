package com.cms.module.category.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.category.dto.CategoryDTO;
import com.cms.module.category.dto.CategoryQueryDTO;
import com.cms.module.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 分类Controller
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Tag(name = "分类管理", description = "分类管理相关接口")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树", description = "获取指定站点的分类树形结构")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('category:list')")
    public ApiResponse<List<CategoryDTO>> getCategoryTree(@RequestParam Long siteId) {
        return ApiResponse.success(categoryService.getCategoryTree(siteId));
    }

    @Operation(summary = "获取可见分类树", description = "获取指定站点的可见分类树形结构")
    @GetMapping("/tree/visible")
    public ApiResponse<List<CategoryDTO>> getVisibleCategoryTree(@RequestParam Long siteId) {
        return ApiResponse.success(categoryService.getVisibleCategoryTree(siteId));
    }

    @Operation(summary = "分页查询分类", description = "根据条件分页查询分类")
    @GetMapping
    @PreAuthorize("hasAuthority('category:list')")
    public ApiResponse<Page<CategoryDTO>> getCategories(
            CategoryQueryDTO queryDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.success(categoryService.getCategories(queryDTO, pageable));
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('category:list')")
    public ApiResponse<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategoryById(id));
    }

    @Operation(summary = "创建分类", description = "创建新的分类")
    @PostMapping
    @PreAuthorize("hasAuthority('category:create')")
    public ApiResponse<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        return ApiResponse.success(categoryService.createCategory(dto));
    }

    @Operation(summary = "更新分类", description = "更新分类信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('category:update')")
    public ApiResponse<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto
    ) {
        return ApiResponse.success(categoryService.updateCategory(id, dto));
    }

    @Operation(summary = "删除分类", description = "删除指定分类(软删除)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success();
    }

    @Operation(summary = "更新分类可见性", description = "更新分类的可见性状态")
    @PutMapping("/{id}/visibility")
    @PreAuthorize("hasAuthority('category:update')")
    public ApiResponse<Void> updateVisibility(
            @PathVariable Long id,
            @RequestBody CategoryDTO dto
    ) {
        categoryService.updateVisibility(id, dto.getIsVisible());
        return ApiResponse.success();
    }
}

