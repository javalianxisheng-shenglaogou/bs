package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import com.multisite.cms.common.PageResult;
import com.multisite.cms.dto.site.SiteCreateRequest;
import com.multisite.cms.dto.site.SiteResponse;
import com.multisite.cms.dto.site.SiteUpdateRequest;
import com.multisite.cms.entity.Site;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.SiteStatus;
import com.multisite.cms.security.UserPrincipal;
import com.multisite.cms.service.SiteService;
import com.multisite.cms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 站点管理控制器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
@Tag(name = "站点管理", description = "站点管理相关接口")
@SecurityRequirement(name = "bearerAuth")
public class SiteController {

    private final SiteService siteService;
    private final UserService userService;

    /**
     * 分页查询站点
     */
    @GetMapping
    @Operation(summary = "分页查询站点", description = "分页查询站点列表，支持状态过滤和关键字搜索")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'REVIEWER')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "查询成功",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<PageResult<SiteResponse>> getSites(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "sortOrder")
            @RequestParam(defaultValue = "sortOrder") String sort,
            @Parameter(description = "排序方向", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "站点状态")
            @RequestParam(required = false) SiteStatus status,
            @Parameter(description = "搜索关键字")
            @RequestParam(required = false) String keyword) {
        
        log.debug("Query sites: page={}, size={}, sort={}, direction={}, status={}, keyword={}", 
                page, size, sort, direction, status, keyword);
        
        try {
            PageResult<Site> sitePage = siteService.getSites(page, size, sort, direction, status, keyword);
            
            // 转换为响应DTO
            List<SiteResponse> siteResponses = sitePage.getContent().stream()
                    .map(SiteResponse::from)
                    .collect(Collectors.toList());
            
            PageResult<SiteResponse> result = PageResult.of(
                    siteResponses, 
                    sitePage.getPage(), 
                    sitePage.getSize(), 
                    sitePage.getTotalElements()
            );
            
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("Query sites failed", e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 根据ID获取站点
     */
    @GetMapping("/{siteId}")
    @Operation(summary = "根据ID获取站点", description = "根据站点ID获取站点详细信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'REVIEWER')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "站点不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<SiteResponse> getSiteById(
            @Parameter(description = "站点ID", required = true)
            @PathVariable Long siteId) {
        
        log.debug("Get site by ID: {}", siteId);
        
        try {
            Site site = siteService.getSiteById(siteId)
                    .orElseThrow(() -> new RuntimeException("站点不存在"));
            
            // 获取子站点
            List<Site> childSites = siteService.getChildSites(siteId);
            SiteResponse response = SiteResponse.from(site, childSites);
            
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            log.error("Get site failed: {}", siteId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 根据代码获取站点
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "根据代码获取站点", description = "根据站点代码获取站点信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'REVIEWER')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "站点不存在"
            )
    })
    public ApiResponse<SiteResponse> getSiteByCode(
            @Parameter(description = "站点代码", required = true)
            @PathVariable String code) {
        
        log.debug("Get site by code: {}", code);
        
        try {
            Site site = siteService.getSiteByCode(code)
                    .orElseThrow(() -> new RuntimeException("站点不存在"));
            
            SiteResponse response = SiteResponse.from(site);
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            log.error("Get site by code failed: {}", code, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 创建站点
     */
    @PostMapping
    @Operation(summary = "创建站点", description = "创建新站点")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "创建成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误或站点已存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<SiteResponse> createSite(
            @Valid @RequestBody SiteCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Create site: {}", request.getCode());
        
        try {
            Site.SiteBuilder siteBuilder = Site.builder()
                    .name(request.getName())
                    .code(request.getCode())
                    .domain(request.getDomain())
                    .description(request.getDescription())
                    .status(request.getStatus())
                    .sortOrder(request.getSortOrder());
            
            // 设置父站点
            if (request.getParentSiteId() != null) {
                Site parentSite = siteService.getSiteById(request.getParentSiteId())
                        .orElseThrow(() -> new RuntimeException("父站点不存在"));
                siteBuilder.parentSite(parentSite);
            }
            
            // 设置所有者
            if (request.getOwnerId() != null) {
                User owner = userService.getUserById(request.getOwnerId())
                        .orElseThrow(() -> new RuntimeException("所有者不存在"));
                siteBuilder.owner(owner);
            } else {
                // 默认设置当前用户为所有者
                User currentUserEntity = userService.getUserById(currentUser.getId())
                        .orElseThrow(() -> new RuntimeException("当前用户不存在"));
                siteBuilder.owner(currentUserEntity);
            }
            
            Site site = siteBuilder.build();
            Site createdSite = siteService.createSite(site, currentUser.getUsername());
            SiteResponse response = SiteResponse.from(createdSite);
            
            return ApiResponse.success("站点创建成功", response);
        } catch (Exception e) {
            log.error("Create site failed: {}", request.getCode(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 更新站点
     */
    @PutMapping("/{siteId}")
    @Operation(summary = "更新站点", description = "更新站点信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "更新成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "站点不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<SiteResponse> updateSite(
            @Parameter(description = "站点ID", required = true)
            @PathVariable Long siteId,
            @Valid @RequestBody SiteUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Update site: {}", siteId);
        
        try {
            Site.SiteBuilder updateSiteBuilder = Site.builder()
                    .name(request.getName())
                    .code(request.getCode())
                    .domain(request.getDomain())
                    .description(request.getDescription())
                    .status(request.getStatus())
                    .sortOrder(request.getSortOrder());
            
            // 设置父站点
            if (request.getParentSiteId() != null) {
                Site parentSite = siteService.getSiteById(request.getParentSiteId())
                        .orElseThrow(() -> new RuntimeException("父站点不存在"));
                updateSiteBuilder.parentSite(parentSite);
            }
            
            // 设置所有者
            if (request.getOwnerId() != null) {
                User owner = userService.getUserById(request.getOwnerId())
                        .orElseThrow(() -> new RuntimeException("所有者不存在"));
                updateSiteBuilder.owner(owner);
            }
            
            Site updateSite = updateSiteBuilder.build();
            Site updatedSite = siteService.updateSite(siteId, updateSite, currentUser.getUsername());
            SiteResponse response = SiteResponse.from(updatedSite);
            
            return ApiResponse.success("站点更新成功", response);
        } catch (Exception e) {
            log.error("Update site failed: {}", siteId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 删除站点
     */
    @DeleteMapping("/{siteId}")
    @Operation(summary = "删除站点", description = "删除站点（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "删除成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "站点不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> deleteSite(
            @Parameter(description = "站点ID", required = true)
            @PathVariable Long siteId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Delete site: {}", siteId);
        
        try {
            siteService.deleteSite(siteId, currentUser.getUsername());
            return ApiResponse.success("站点删除成功");
        } catch (Exception e) {
            log.error("Delete site failed: {}", siteId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 批量删除站点
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除站点", description = "批量删除站点（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "删除成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> deleteSites(
            @Parameter(description = "站点ID列表", required = true)
            @RequestBody List<Long> siteIds,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Batch delete sites: {}", siteIds);
        
        try {
            siteService.deleteSites(siteIds, currentUser.getUsername());
            return ApiResponse.success("批量删除成功");
        } catch (Exception e) {
            log.error("Batch delete sites failed: {}", siteIds, e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 更新站点状态
     */
    @PatchMapping("/{siteId}/status")
    @Operation(summary = "更新站点状态", description = "更新站点状态")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "更新成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "站点不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> updateSiteStatus(
            @Parameter(description = "站点ID", required = true)
            @PathVariable Long siteId,
            @Parameter(description = "站点状态", required = true)
            @RequestParam SiteStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Update site status: {} -> {}", siteId, status);
        
        try {
            siteService.updateSiteStatus(siteId, status, currentUser.getUsername());
            return ApiResponse.success("状态更新成功");
        } catch (Exception e) {
            log.error("Update site status failed: {}", siteId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取根站点列表
     */
    @GetMapping("/roots")
    @Operation(summary = "获取根站点列表", description = "获取所有根站点（无父站点）")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'REVIEWER')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            )
    })
    public ApiResponse<List<SiteResponse>> getRootSites() {
        log.debug("Get root sites");
        
        try {
            List<Site> rootSites = siteService.getRootSites();
            List<SiteResponse> responses = rootSites.stream()
                    .map(SiteResponse::from)
                    .collect(Collectors.toList());
            
            return ApiResponse.success("获取成功", responses);
        } catch (Exception e) {
            log.error("Get root sites failed", e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取子站点列表
     */
    @GetMapping("/{siteId}/children")
    @Operation(summary = "获取子站点列表", description = "获取指定站点的所有子站点")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'REVIEWER')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            )
    })
    public ApiResponse<List<SiteResponse>> getChildSites(
            @Parameter(description = "父站点ID", required = true)
            @PathVariable Long siteId) {
        
        log.debug("Get child sites for parent: {}", siteId);
        
        try {
            List<Site> childSites = siteService.getChildSites(siteId);
            List<SiteResponse> responses = childSites.stream()
                    .map(SiteResponse::from)
                    .collect(Collectors.toList());
            
            return ApiResponse.success("获取成功", responses);
        } catch (Exception e) {
            log.error("Get child sites failed for parent: {}", siteId, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取用户管理的站点
     */
    @GetMapping("/managed")
    @Operation(summary = "获取用户管理的站点", description = "获取当前用户管理的所有站点")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            )
    })
    public ApiResponse<List<SiteResponse>> getManagedSites(@AuthenticationPrincipal UserPrincipal currentUser) {
        log.debug("Get managed sites for user: {}", currentUser.getId());
        
        try {
            List<Site> managedSites = siteService.getManagedSitesByUserId(currentUser.getId());
            List<SiteResponse> responses = managedSites.stream()
                    .map(SiteResponse::from)
                    .collect(Collectors.toList());
            
            return ApiResponse.success("获取成功", responses);
        } catch (Exception e) {
            log.error("Get managed sites failed for user: {}", currentUser.getId(), e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 检查站点代码是否可用
     */
    @GetMapping("/check-code")
    @Operation(summary = "检查站点代码是否可用", description = "检查站点代码是否已被使用")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "检查完成"
            )
    })
    public ApiResponse<Boolean> checkSiteCodeAvailable(
            @Parameter(description = "站点代码", required = true)
            @RequestParam String code,
            @Parameter(description = "排除的站点ID")
            @RequestParam(required = false) Long excludeSiteId) {
        
        log.debug("Check site code available: {}", code);
        
        try {
            boolean available = siteService.isSiteCodeAvailable(code, excludeSiteId);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            log.error("Check site code failed: {}", code, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 检查域名是否可用
     */
    @GetMapping("/check-domain")
    @Operation(summary = "检查域名是否可用", description = "检查域名是否已被使用")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "检查完成"
            )
    })
    public ApiResponse<Boolean> checkDomainAvailable(
            @Parameter(description = "域名", required = true)
            @RequestParam String domain,
            @Parameter(description = "排除的站点ID")
            @RequestParam(required = false) Long excludeSiteId) {
        
        log.debug("Check domain available: {}", domain);
        
        try {
            boolean available = siteService.isDomainAvailable(domain, excludeSiteId);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            log.error("Check domain failed: {}", domain, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取站点统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取站点统计信息", description = "获取站点数量统计信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<SiteService.SiteStats> getSiteStats() {
        log.debug("Get site stats");
        
        try {
            SiteService.SiteStats stats = siteService.getSiteStats();
            return ApiResponse.success("获取成功", stats);
        } catch (Exception e) {
            log.error("Get site stats failed", e);
            return ApiResponse.internalError(e.getMessage());
        }
    }
}
