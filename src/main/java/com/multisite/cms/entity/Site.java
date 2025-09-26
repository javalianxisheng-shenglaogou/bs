package com.multisite.cms.entity;

import com.multisite.cms.enums.SiteStatus;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * 站点实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "sites", indexes = {
    @Index(name = "idx_code", columnList = "code"),
    @Index(name = "idx_domain", columnList = "domain"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_owner_id", columnList = "owner_id"),
    @Index(name = "idx_parent_site_id", columnList = "parent_site_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "parentSite", "childSites", "siteConfigs", "contents", "userRoles"})
@ToString(callSuper = true, exclude = {"owner", "parentSite", "childSites", "siteConfigs", "contents", "userRoles"})
public class Site extends BaseEntity {

    /**
     * 站点名称
     */
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 100, message = "站点名称长度不能超过100")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * 站点代码（唯一标识）
     */
    @NotBlank(message = "站点代码不能为空")
    @Size(max = 50, message = "站点代码长度不能超过50")
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    /**
     * 主域名
     */
    @Size(max = 255, message = "域名长度不能超过255")
    @Column(name = "domain")
    private String domain;

    /**
     * 站点描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Logo URL
     */
    @Size(max = 500, message = "Logo URL长度不能超过500")
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /**
     * Favicon URL
     */
    @Size(max = 500, message = "Favicon URL长度不能超过500")
    @Column(name = "favicon_url", length = 500)
    private String faviconUrl;

    /**
     * 主题
     */
    @Size(max = 50, message = "主题名称长度不能超过50")
    @Column(name = "theme", length = 50)
    @Builder.Default
    private String theme = "default";

    /**
     * 默认语言
     */
    @Size(max = 10, message = "语言代码长度不能超过10")
    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "zh-CN";

    /**
     * 时区
     */
    @Size(max = 50, message = "时区长度不能超过50")
    @Column(name = "timezone", length = 50)
    @Builder.Default
    private String timezone = "Asia/Shanghai";

    /**
     * 站点状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SiteStatus status = SiteStatus.ACTIVE;

    /**
     * 站点所有者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sites_owner"))
    private User owner;

    /**
     * 父站点（支持站点层级）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_site_id", foreignKey = @ForeignKey(name = "fk_sites_parent"))
    private Site parentSite;

    /**
     * 子站点集合
     */
    @OneToMany(mappedBy = "parentSite", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Site> childSites = new HashSet<>();

    /**
     * 排序
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 站点配置
     */
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SiteConfig> siteConfigs = new HashSet<>();

    /**
     * 站点内容
     */
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Content> contents = new HashSet<>();

    /**
     * 用户角色关联（站点级权限）
     */
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * 判断站点是否激活
     */
    @Transient
    public boolean isActive() {
        return SiteStatus.ACTIVE.equals(this.status) && !isDeleted();
    }

    /**
     * 判断站点是否维护中
     */
    @Transient
    public boolean isMaintenance() {
        return SiteStatus.MAINTENANCE.equals(this.status);
    }

    /**
     * 判断站点是否未激活
     */
    @Transient
    public boolean isInactive() {
        return SiteStatus.INACTIVE.equals(this.status);
    }

    /**
     * 判断是否为根站点
     */
    @Transient
    public boolean isRootSite() {
        return this.parentSite == null;
    }

    /**
     * 判断是否为子站点
     */
    @Transient
    public boolean isChildSite() {
        return this.parentSite != null;
    }

    /**
     * 获取站点层级
     */
    @Transient
    public int getLevel() {
        int level = 1;
        Site parent = this.parentSite;
        while (parent != null) {
            level++;
            parent = parent.getParentSite();
        }
        return level;
    }

    /**
     * 获取站点路径
     */
    @Transient
    public String getSitePath() {
        StringBuilder path = new StringBuilder();
        Site current = this;
        while (current != null) {
            if (path.length() > 0) {
                path.insert(0, "/");
            }
            path.insert(0, current.getCode());
            current = current.getParentSite();
        }
        return "/" + path.toString();
    }

    /**
     * 激活站点
     */
    public void activate() {
        this.status = SiteStatus.ACTIVE;
    }

    /**
     * 停用站点
     */
    public void deactivate() {
        this.status = SiteStatus.INACTIVE;
    }

    /**
     * 设置维护模式
     */
    public void setMaintenance() {
        this.status = SiteStatus.MAINTENANCE;
    }

    /**
     * 添加子站点
     */
    public void addChildSite(Site childSite) {
        this.childSites.add(childSite);
        childSite.setParentSite(this);
    }

    /**
     * 移除子站点
     */
    public void removeChildSite(Site childSite) {
        this.childSites.remove(childSite);
        childSite.setParentSite(null);
    }

    /**
     * 添加站点配置
     */
    public void addSiteConfig(SiteConfig siteConfig) {
        this.siteConfigs.add(siteConfig);
        siteConfig.setSite(this);
    }

    /**
     * 移除站点配置
     */
    public void removeSiteConfig(SiteConfig siteConfig) {
        this.siteConfigs.remove(siteConfig);
        siteConfig.setSite(null);
    }

    /**
     * 获取站点配置值
     */
    public String getConfigValue(String key) {
        return siteConfigs.stream()
                .filter(config -> key.equals(config.getConfigKey()))
                .map(SiteConfig::getConfigValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取站点配置值（带默认值）
     */
    public String getConfigValue(String key, String defaultValue) {
        String value = getConfigValue(key);
        return value != null ? value : defaultValue;
    }
}
