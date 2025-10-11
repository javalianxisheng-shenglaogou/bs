package com.cms.module.content.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.content.dto.HomePageDTO;
import com.cms.module.content.dto.PublicContentDTO;
import com.cms.module.content.dto.PublicContentDetailDTO;
import com.cms.module.content.dto.PublicContentQueryDTO;
import com.cms.module.content.service.PublicContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开内容控制器（访客端）
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/guest/contents")
@RequiredArgsConstructor
@Tag(name = "访客端-内容管理", description = "访客端内容查看接口")
public class PublicContentController {

    private final PublicContentService publicContentService;

    /**
     * 获取首页数据
     */
    @GetMapping("/home")
    @PreAuthorize("hasAuthority('content:view:published')")
    @Operation(summary = "获取首页数据", description = "获取首页展示的分类、置顶和推荐内容")
    public ApiResponse<HomePageDTO> getHomePage(@RequestParam Long siteId) {
        log.info("获取首页数据请求: siteId={}", siteId);
        HomePageDTO homePageData = publicContentService.getHomePageData(siteId);
        return ApiResponse.success(homePageData);
    }

    /**
     * 分页查询已发布内容
     */
    @GetMapping
    @PreAuthorize("hasAuthority('content:view:published')")
    @Operation(summary = "查询已发布内容列表", description = "分页查询已发布的内容")
    public ApiResponse<Page<PublicContentDTO>> getPublishedContents(
            @RequestParam Long siteId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询已发布内容请求: siteId={}, categoryId={}, page={}, size={}", 
                siteId, categoryId, page, size);
        
        PublicContentQueryDTO queryDTO = new PublicContentQueryDTO();
        queryDTO.setSiteId(siteId);
        queryDTO.setCategoryId(categoryId);
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        
        Page<PublicContentDTO> contents = publicContentService.getPublishedContents(queryDTO);
        return ApiResponse.success(contents);
    }

    /**
     * 搜索内容
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('content:search:published')")
    @Operation(summary = "搜索内容", description = "根据关键词搜索已发布的内容（标题+正文）")
    public ApiResponse<Page<PublicContentDTO>> searchContents(
            @RequestParam Long siteId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("搜索内容请求: siteId={}, keyword={}, page={}, size={}", 
                siteId, keyword, page, size);
        
        PublicContentQueryDTO queryDTO = new PublicContentQueryDTO();
        queryDTO.setSiteId(siteId);
        queryDTO.setKeyword(keyword);
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        
        Page<PublicContentDTO> searchResults = publicContentService.searchContents(queryDTO);
        return ApiResponse.success(searchResults);
    }

    /**
     * 获取内容详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('content:detail:published')")
    @Operation(summary = "获取内容详情", description = "获取指定内容的详细信息，并增加浏览量")
    public ApiResponse<PublicContentDetailDTO> getContentDetail(@PathVariable Long id) {
        log.info("获取内容详情请求: id={}", id);
        PublicContentDetailDTO contentDetail = publicContentService.getContentDetail(id);
        return ApiResponse.success(contentDetail);
    }

    /**
     * 获取相关内容推荐
     */
    @GetMapping("/{id}/related")
    @PreAuthorize("hasAuthority('content:view:published')")
    @Operation(summary = "获取相关内容", description = "获取与指定内容相关的推荐内容")
    public ApiResponse<List<PublicContentDTO>> getRelatedContents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") Integer limit) {
        
        log.info("获取相关内容请求: id={}, limit={}", id, limit);
        List<PublicContentDTO> relatedContents = publicContentService.getRelatedContents(id, limit);
        return ApiResponse.success(relatedContents);
    }
}

