package com.multisite.cms.service;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.Site;
import com.multisite.cms.enums.SiteStatus;

import java.util.List;
import java.util.Optional;

/**
 * 站点服务接口
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface SiteService {

    /**
     * 分页查询站点
     *
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序字段
     * @param direction 排序方向
     * @param status 站点状态
     * @param keyword 搜索关键字
     * @return 站点分页结果
     */
    PageResult<Site> getSites(int page, int size, String sort, String direction,
                            SiteStatus status, String keyword);

    /**
     * 根据ID获取站点
     *
     * @param siteId 站点ID
     * @return 站点信息
     */
    Optional<Site> getSiteById(Long siteId);

    /**
     * 根据代码获取站点
     *
     * @param code 站点代码
     * @return 站点信息
     */
    Optional<Site> getSiteByCode(String code);

    /**
     * 根据域名获取站点
     *
     * @param domain 域名
     * @return 站点信息
     */
    Optional<Site> getSiteByDomain(String domain);

    /**
     * 获取子站点列表
     *
     * @param parentSiteId 父站点ID
     * @return 子站点列表
     */
    List<Site> getChildSites(Long parentSiteId);

    /**
     * 获取根站点列表
     *
     * @return 根站点列表
     */
    List<Site> getRootSites();

    /**
     * 创建站点
     *
     * @param site 站点信息
     * @param createdBy 创建者
     * @return 创建的站点
     */
    Site createSite(Site site, String createdBy);

    /**
     * 更新站点
     *
     * @param siteId 站点ID
     * @param updateSite 更新的站点信息
     * @param updatedBy 更新者
     * @return 更新后的站点
     */
    Site updateSite(Long siteId, Site updateSite, String updatedBy);

    /**
     * 删除站点（软删除）
     *
     * @param siteId 站点ID
     * @param deletedBy 删除者
     */
    void deleteSite(Long siteId, String deletedBy);

    /**
     * 批量删除站点
     *
     * @param siteIds 站点ID列表
     * @param deletedBy 删除者
     */
    void deleteSites(List<Long> siteIds, String deletedBy);

    /**
     * 激活站点
     *
     * @param siteId 站点ID
     * @param activatedBy 激活者
     */
    void activateSite(Long siteId, String activatedBy);

    /**
     * 停用站点
     *
     * @param siteId 站点ID
     * @param deactivatedBy 停用者
     */
    void deactivateSite(Long siteId, String deactivatedBy);

    /**
     * 检查站点代码是否可用
     *
     * @param code 站点代码
     * @param excludeSiteId 排除的站点ID
     * @return 是否可用
     */
    boolean isSiteCodeAvailable(String code, Long excludeSiteId);

    /**
     * 检查域名是否可用
     *
     * @param domain 域名
     * @param excludeSiteId 排除的站点ID
     * @return 是否可用
     */
    boolean isDomainAvailable(String domain, Long excludeSiteId);

    /**
     * 获取站点统计信息
     *
     * @return 统计信息
     */
    SiteStats getSiteStats();

    /**
     * 更新站点状态
     *
     * @param siteId 站点ID
     * @param status 新状态
     * @param updatedBy 更新者
     */
    Site updateSiteStatus(Long siteId, SiteStatus status, String updatedBy);

    /**
     * 根据用户ID获取管理的站点列表
     *
     * @param userId 用户ID
     * @return 站点列表
     */
    List<Site> getManagedSitesByUserId(Long userId);

    /**
     * 检查站点代码是否存在
     *
     * @param code 站点代码
     * @param excludeId 排除的站点ID
     * @return 是否存在
     */
    boolean existsByCode(String code, Long excludeId);

    /**
     * 检查域名是否存在
     *
     * @param domain 域名
     * @param excludeId 排除的站点ID
     * @return 是否存在
     */
    boolean existsByDomain(String domain, Long excludeId);

    /**
     * 站点统计信息类
     */
    class SiteStats {
        private long totalSites;
        private long activeSites;
        private long inactiveSites;
        private long maintenanceSites;

        // Getters and Setters
        public long getTotalSites() { return totalSites; }
        public void setTotalSites(long totalSites) { this.totalSites = totalSites; }

        public long getActiveSites() { return activeSites; }
        public void setActiveSites(long activeSites) { this.activeSites = activeSites; }

        public long getInactiveSites() { return inactiveSites; }
        public void setInactiveSites(long inactiveSites) { this.inactiveSites = inactiveSites; }

        public long getMaintenanceSites() { return maintenanceSites; }
        public void setMaintenanceSites(long maintenanceSites) { this.maintenanceSites = maintenanceSites; }
    }
}