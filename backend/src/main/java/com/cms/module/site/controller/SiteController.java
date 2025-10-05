package com.cms.module.site.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.site.dto.SiteDTO;
import com.cms.module.site.dto.SiteQueryDTO;
import com.cms.module.site.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 站点控制器
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
@Tag(name = "站点管理", description = "站点管理相关接口")
public class SiteController {

    private final SiteService siteService;

    /**
     * 创建站点
     */
    @PostMapping
    @PreAuthorize("hasAuthority('site:create')")
    @Operation(summary = "创建站点", description = "创建新的站点")
    public ApiResponse<SiteDTO> createSite(@Validated @RequestBody SiteDTO siteDTO) {
        log.info("创建站点请求: {}", siteDTO);
        SiteDTO result = siteService.createSite(siteDTO);
        return ApiResponse.success(result);
    }

    /**
     * 更新站点
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('site:update')")
    @Operation(summary = "更新站点", description = "更新站点信息")
    public ApiResponse<SiteDTO> updateSite(
            @PathVariable Long id,
            @Validated @RequestBody SiteDTO siteDTO) {
        log.info("更新站点请求: id={}, data={}", id, siteDTO);
        SiteDTO result = siteService.updateSite(id, siteDTO);
        return ApiResponse.success(result);
    }

    /**
     * 删除站点
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('site:delete')")
    @Operation(summary = "删除站点", description = "逻辑删除站点")
    public ApiResponse<Void> deleteSite(@PathVariable Long id) {
        log.info("删除站点请求: id={}", id);
        siteService.deleteSite(id);
        return ApiResponse.success();
    }

    /**
     * 获取站点详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('site:view')")
    @Operation(summary = "获取站点详情", description = "根据ID获取站点详细信息")
    public ApiResponse<SiteDTO> getSite(@PathVariable Long id) {
        log.info("获取站点详情请求: id={}", id);
        SiteDTO result = siteService.getSiteById(id);
        return ApiResponse.success(result);
    }

    /**
     * 根据代码获取站点
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('site:view')")
    @Operation(summary = "根据代码获取站点", description = "根据站点代码获取站点信息")
    public ApiResponse<SiteDTO> getSiteByCode(@PathVariable String code) {
        log.info("根据代码获取站点请求: code={}", code);
        SiteDTO result = siteService.getSiteByCode(code);
        return ApiResponse.success(result);
    }

    /**
     * 获取默认站点
     */
    @GetMapping("/default")
    @Operation(summary = "获取默认站点", description = "获取默认站点信息")
    public ApiResponse<SiteDTO> getDefaultSite() {
        log.info("获取默认站点请求");
        SiteDTO result = siteService.getDefaultSite();
        return ApiResponse.success(result);
    }

    /**
     * 分页查询站点
     */
    @GetMapping
    @PreAuthorize("hasAuthority('site:list')")
    @Operation(summary = "分页查询站点", description = "根据条件分页查询站点列表")
    public ApiResponse<Page<SiteDTO>> getSites(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isDefault,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("分页查询站点请求: name={}, code={}, domain={}, status={}, isDefault={}, page={}, size={}",
                name, code, domain, status, isDefault, page, size);

        SiteQueryDTO queryDTO = new SiteQueryDTO();
        queryDTO.setName(name);
        queryDTO.setCode(code);
        queryDTO.setDomain(domain);
        queryDTO.setStatus(status);
        queryDTO.setIsDefault(isDefault);
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setSortBy(sortBy);
        queryDTO.setSortDir(sortDir);

        Page<SiteDTO> result = siteService.getSites(queryDTO);
        return ApiResponse.success(result);
    }

    /**
     * 获取所有站点列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('site:list')")
    @Operation(summary = "获取所有站点", description = "获取所有站点列表（不分页）")
    public ApiResponse<List<SiteDTO>> getAllSites() {
        log.info("获取所有站点列表请求");
        List<SiteDTO> result = siteService.getAllSites();
        return ApiResponse.success(result);
    }

    /**
     * 更新站点状态
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('site:update')")
    @Operation(summary = "更新站点状态", description = "更新站点的状态")
    public ApiResponse<Void> updateSiteStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        log.info("更新站点状态请求: id={}, status={}", id, status);
        siteService.updateSiteStatus(id, status);
        return ApiResponse.success();
    }

    /**
     * 设置默认站点
     */
    @PatchMapping("/{id}/default")
    @PreAuthorize("hasAuthority('site:update')")
    @Operation(summary = "设置默认站点", description = "将指定站点设置为默认站点")
    public ApiResponse<Void> setDefaultSite(@PathVariable Long id) {
        log.info("设置默认站点请求: id={}", id);
        siteService.setDefaultSite(id);
        return ApiResponse.success();
    }

    /**
     * 获取站点统计信息
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasAuthority('site:view')")
    @Operation(summary = "获取站点统计", description = "获取站点的统计信息")
    public ApiResponse<Map<String, Object>> getSiteStatistics(@PathVariable Long id) {
        log.info("获取站点统计请求: id={}", id);
        // 临时返回模拟数据
        Map<String, Object> statistics = Map.of(
            "contentCount", 0L,
            "publishedContentCount", 0L,
            "draftContentCount", 0L,
            "categoryCount", 0L,
            "userCount", 0L,
            "todayVisits", 0L,
            "totalVisits", 0L
        );
        return ApiResponse.success(statistics);
    }
}

