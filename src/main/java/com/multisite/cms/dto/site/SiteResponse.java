package com.multisite.cms.dto.site;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multisite.cms.entity.Site;
import com.multisite.cms.enums.SiteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站点响应DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "站点响应")
public class SiteResponse {

    /**
     * 站点ID
     */
    @Schema(description = "站点ID", example = "1")
    private Long id;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称", example = "示例站点")
    private String name;

    /**
     * 站点代码
     */
    @Schema(description = "站点代码", example = "example_site")
    private String code;

    /**
     * 域名
     */
    @Schema(description = "域名", example = "example.com")
    private String domain;

    /**
     * 站点描述
     */
    @Schema(description = "站点描述", example = "这是一个示例站点")
    private String description;

    /**
     * 站点状态
     */
    @Schema(description = "站点状态", example = "ACTIVE")
    private SiteStatus status;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "0")
    private Integer sortOrder;

    /**
     * 父站点信息
     */
    @Schema(description = "父站点信息")
    private SiteBasicInfo parentSite;

    /**
     * 所有者信息
     */
    @Schema(description = "所有者信息")
    private OwnerInfo owner;

    /**
     * 子站点列表
     */
    @Schema(description = "子站点列表")
    private List<SiteBasicInfo> childSites;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin")
    private String createdBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "admin")
    private String updatedBy;

    /**
     * 站点基本信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "站点基本信息")
    public static class SiteBasicInfo {
        @Schema(description = "站点ID", example = "1")
        private Long id;
        
        @Schema(description = "站点名称", example = "示例站点")
        private String name;
        
        @Schema(description = "站点代码", example = "example_site")
        private String code;
        
        @Schema(description = "站点状态", example = "ACTIVE")
        private SiteStatus status;
    }

    /**
     * 所有者信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "所有者信息")
    public static class OwnerInfo {
        @Schema(description = "用户ID", example = "1")
        private Long id;
        
        @Schema(description = "用户名", example = "admin")
        private String username;
        
        @Schema(description = "显示名称", example = "管理员")
        private String displayName;
    }

    /**
     * 从Site实体创建SiteResponse
     * 
     * @param site Site实体
     * @return SiteResponse
     */
    public static SiteResponse from(Site site) {
        SiteResponseBuilder builder = SiteResponse.builder()
                .id(site.getId())
                .name(site.getName())
                .code(site.getCode())
                .domain(site.getDomain())
                .description(site.getDescription())
                .status(site.getStatus())
                .sortOrder(site.getSortOrder())
                .createdAt(site.getCreatedAt())
                .updatedAt(site.getUpdatedAt())
                .createdBy(site.getCreatedBy())
                .updatedBy(site.getUpdatedBy());

        // 设置父站点信息
        if (site.getParentSite() != null) {
            builder.parentSite(SiteBasicInfo.builder()
                    .id(site.getParentSite().getId())
                    .name(site.getParentSite().getName())
                    .code(site.getParentSite().getCode())
                    .status(site.getParentSite().getStatus())
                    .build());
        }

        // 设置所有者信息
        if (site.getOwner() != null) {
            builder.owner(OwnerInfo.builder()
                    .id(site.getOwner().getId())
                    .username(site.getOwner().getUsername())
                    .displayName(site.getOwner().getDisplayName())
                    .build());
        }

        return builder.build();
    }

    /**
     * 从Site实体创建SiteResponse（包含子站点）
     * 
     * @param site Site实体
     * @param childSites 子站点列表
     * @return SiteResponse
     */
    public static SiteResponse from(Site site, List<Site> childSites) {
        SiteResponse response = from(site);
        
        if (childSites != null && !childSites.isEmpty()) {
            List<SiteBasicInfo> childSiteInfos = childSites.stream()
                    .map(child -> SiteBasicInfo.builder()
                            .id(child.getId())
                            .name(child.getName())
                            .code(child.getCode())
                            .status(child.getStatus())
                            .build())
                    .collect(Collectors.toList());
            response.setChildSites(childSiteInfos);
        }
        
        return response;
    }
}
