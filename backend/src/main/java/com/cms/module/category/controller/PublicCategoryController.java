package com.cms.module.category.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.category.dto.CategoryTreeDTO;
import com.cms.module.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开分类控制器（访客端）
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/guest/categories")
@RequiredArgsConstructor
@Tag(name = "访客端-分类管理", description = "访客端分类查看接口")
public class PublicCategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树
     */
    @GetMapping
    @PreAuthorize("hasAuthority('category:view:public')")
    @Operation(summary = "获取分类树", description = "获取指定站点的公开分类树结构")
    public ApiResponse<List<CategoryTreeDTO>> getCategoryTree(@RequestParam Long siteId) {
        log.info("获取分类树请求: siteId={}", siteId);
        List<CategoryTreeDTO> categoryTree = categoryService.getCategoryTreeForGuest(siteId);
        return ApiResponse.success(categoryTree);
    }
}

