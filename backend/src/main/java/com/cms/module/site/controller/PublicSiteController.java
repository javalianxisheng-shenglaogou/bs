package com.cms.module.site.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.site.dto.SiteDTO;
import com.cms.module.site.service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开站点控制器（访客端）
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Slf4j
@RestController
@RequestMapping("/guest/sites")
@RequiredArgsConstructor
@Tag(name = "访客端-站点管理", description = "访客端站点查看接口")
public class PublicSiteController {

    private final SiteService siteService;

    /**
     * 获取活跃站点列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('site:view:public')")
    @Operation(summary = "获取活跃站点列表", description = "获取所有状态为ACTIVE的站点")
    public ApiResponse<List<SiteDTO>> getActiveSites() {
        log.info("获取活跃站点列表请求");
        List<SiteDTO> activeSites = siteService.getActiveSites();
        return ApiResponse.success(activeSites);
    }
}

