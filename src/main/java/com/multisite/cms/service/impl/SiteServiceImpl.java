package com.multisite.cms.service.impl;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.Site;
import com.multisite.cms.enums.SiteStatus;
import com.multisite.cms.repository.SiteRepository;
import com.multisite.cms.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 站点服务实现类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<Site> getSites(int page, int size, String sort, String direction, 
                                   SiteStatus status, String keyword) {
        log.debug("Querying sites: page={}, size={}, sort={}, direction={}, status={}, keyword={}", 
                page, size, sort, direction, status, keyword);
        
        // 创建排序对象
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(sortDirection, sort != null ? sort : "createdAt");
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, sortObj);
        
        Page<Site> sitePage;
        
        if (StringUtils.hasText(keyword) && status != null) {
            // 按关键字和状态搜索
            sitePage = siteRepository.findByStatusAndKeyword(status, keyword, pageable);
        } else if (StringUtils.hasText(keyword)) {
            // 按关键字搜索
            sitePage = siteRepository.findByKeyword(keyword, pageable);
        } else if (status != null) {
            // 按状态查询
            sitePage = siteRepository.findByStatusAndDeletedFalse(status, pageable);
        } else {
            // 查询所有
            sitePage = siteRepository.findByDeletedFalse(pageable);
        }
        
        return PageResult.of(sitePage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Site> getSiteById(Long siteId) {
        log.debug("Getting site by ID: {}", siteId);
        return siteRepository.findById(siteId)
                .filter(site -> !site.isDeleted());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Site> getSiteByCode(String code) {
        log.debug("Getting site by code: {}", code);
        return siteRepository.findByCodeAndDeletedFalse(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Site> getSiteByDomain(String domain) {
        log.debug("Getting site by domain: {}", domain);
        return siteRepository.findByDomainAndDeletedFalse(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Site> getChildSites(Long parentSiteId) {
        log.debug("Getting child sites for parent: {}", parentSiteId);
        return siteRepository.findByParentSiteIdAndDeletedFalseOrderByCreatedAtAsc(parentSiteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Site> getRootSites() {
        log.debug("Getting root sites");
        return siteRepository.findByParentSiteIsNullAndDeletedFalseOrderByCreatedAtAsc();
    }

    @Override
    @Transactional
    public Site createSite(Site site, String createdBy) {
        log.info("Creating site: {}", site.getName());
        
        // 验证站点代码唯一性
        if (siteRepository.existsByCodeAndDeletedFalse(site.getCode())) {
            throw new RuntimeException("站点代码已存在: " + site.getCode());
        }
        
        // 验证域名唯一性（如果提供了域名）
        if (StringUtils.hasText(site.getDomain()) && 
            siteRepository.existsByDomainAndDeletedFalse(site.getDomain())) {
            throw new RuntimeException("域名已存在: " + site.getDomain());
        }
        
        // 设置创建信息
        site.setCreatedBy(createdBy);
        site.setUpdatedBy(createdBy);
        
        // 设置默认状态
        if (site.getStatus() == null) {
            site.setStatus(SiteStatus.ACTIVE);
        }
        
        Site savedSite = siteRepository.save(site);
        log.info("Site created successfully: {} (ID: {})", savedSite.getName(), savedSite.getId());
        
        return savedSite;
    }

    @Override
    @Transactional
    public Site updateSite(Long siteId, Site updateSite, String updatedBy) {
        log.info("Updating site: {}", siteId);
        
        Site existingSite = siteRepository.findById(siteId)
                .filter(site -> !site.isDeleted())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + siteId));
        
        // 验证站点代码唯一性（排除当前站点）
        if (StringUtils.hasText(updateSite.getCode()) && 
            !updateSite.getCode().equals(existingSite.getCode()) &&
            siteRepository.existsByCodeAndIdNotAndDeletedFalse(updateSite.getCode(), siteId)) {
            throw new RuntimeException("站点代码已存在: " + updateSite.getCode());
        }
        
        // 验证域名唯一性（排除当前站点）
        if (StringUtils.hasText(updateSite.getDomain()) && 
            !updateSite.getDomain().equals(existingSite.getDomain()) &&
            siteRepository.existsByDomainAndIdNotAndDeletedFalse(updateSite.getDomain(), siteId)) {
            throw new RuntimeException("域名已存在: " + updateSite.getDomain());
        }
        
        // 更新字段
        if (StringUtils.hasText(updateSite.getName())) {
            existingSite.setName(updateSite.getName());
        }
        if (StringUtils.hasText(updateSite.getCode())) {
            existingSite.setCode(updateSite.getCode());
        }
        if (StringUtils.hasText(updateSite.getDescription())) {
            existingSite.setDescription(updateSite.getDescription());
        }
        if (StringUtils.hasText(updateSite.getDomain())) {
            existingSite.setDomain(updateSite.getDomain());
        }
        if (updateSite.getStatus() != null) {
            existingSite.setStatus(updateSite.getStatus());
        }
        if (updateSite.getParentSite() != null) {
            existingSite.setParentSite(updateSite.getParentSite());
        }
        
        existingSite.setUpdatedBy(updatedBy);
        
        Site savedSite = siteRepository.save(existingSite);
        log.info("Site updated successfully: {} (ID: {})", savedSite.getName(), savedSite.getId());
        
        return savedSite;
    }

    @Override
    @Transactional
    public void deleteSite(Long siteId, String deletedBy) {
        log.info("Deleting site: {}", siteId);
        
        Site site = siteRepository.findById(siteId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + siteId));
        
        // 检查是否有子站点
        long childCount = siteRepository.countByParentSiteIdAndDeletedFalse(siteId);
        if (childCount > 0) {
            throw new RuntimeException("存在子站点，无法删除");
        }
        
        site.markAsDeleted();
        site.setUpdatedBy(deletedBy);
        siteRepository.save(site);
        
        log.info("Site deleted successfully: {} (ID: {})", site.getName(), siteId);
    }

    @Override
    @Transactional
    public void deleteSites(List<Long> siteIds, String deletedBy) {
        log.info("Batch deleting sites: {}", siteIds);
        
        List<Site> sites = siteRepository.findAllById(siteIds);
        for (Site site : sites) {
            if (!site.isDeleted()) {
                // 检查是否有子站点
                long childCount = siteRepository.countByParentSiteIdAndDeletedFalse(site.getId());
                if (childCount > 0) {
                    throw new RuntimeException("站点 " + site.getName() + " 存在子站点，无法删除");
                }
                
                site.markAsDeleted();
                site.setUpdatedBy(deletedBy);
            }
        }
        
        siteRepository.saveAll(sites);
        log.info("Batch delete completed for {} sites", sites.size());
    }

    @Override
    @Transactional
    public void activateSite(Long siteId, String activatedBy) {
        log.info("Activating site: {}", siteId);
        
        Site site = siteRepository.findById(siteId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + siteId));
        
        site.setStatus(SiteStatus.ACTIVE);
        site.setUpdatedBy(activatedBy);
        siteRepository.save(site);
        
        log.info("Site activated successfully: {} (ID: {})", site.getName(), siteId);
    }

    @Override
    @Transactional
    public void deactivateSite(Long siteId, String deactivatedBy) {
        log.info("Deactivating site: {}", siteId);
        
        Site site = siteRepository.findById(siteId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + siteId));
        
        site.setStatus(SiteStatus.INACTIVE);
        site.setUpdatedBy(deactivatedBy);
        siteRepository.save(site);
        
        log.info("Site deactivated successfully: {} (ID: {})", site.getName(), siteId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSiteCodeAvailable(String code, Long excludeSiteId) {
        if (excludeSiteId != null) {
            return !siteRepository.existsByCodeAndIdNotAndDeletedFalse(code, excludeSiteId);
        } else {
            return !siteRepository.existsByCodeAndDeletedFalse(code);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDomainAvailable(String domain, Long excludeSiteId) {
        if (excludeSiteId != null) {
            return !siteRepository.existsByDomainAndIdNotAndDeletedFalse(domain, excludeSiteId);
        } else {
            return !siteRepository.existsByDomainAndDeletedFalse(domain);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SiteStats getSiteStats() {
        long totalSites = siteRepository.countByDeletedFalse();
        long activeSites = siteRepository.countByStatusAndDeletedFalse(SiteStatus.ACTIVE);
        long inactiveSites = siteRepository.countByStatusAndDeletedFalse(SiteStatus.INACTIVE);
        long maintenanceSites = siteRepository.countByStatusAndDeletedFalse(SiteStatus.MAINTENANCE);

        SiteStats stats = new SiteStats();
        stats.setTotalSites(totalSites);
        stats.setActiveSites(activeSites);
        stats.setInactiveSites(inactiveSites);
        stats.setMaintenanceSites(maintenanceSites);

        return stats;
    }

    @Override
    @Transactional
    public Site updateSiteStatus(Long siteId, SiteStatus status, String updatedBy) {
        log.info("Updating site status: {} -> {}", siteId, status);

        Site site = siteRepository.findById(siteId)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new RuntimeException("站点不存在: " + siteId));

        site.setStatus(status);
        site.setUpdatedBy(updatedBy);
        Site savedSite = siteRepository.save(site);

        log.info("Site status updated successfully: {} (ID: {})", site.getName(), siteId);
        return savedSite;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Site> getManagedSitesByUserId(Long userId) {
        log.debug("Getting managed sites for user: {}", userId);
        // 这里可以根据用户角色权限来查询管理的站点
        // 暂时返回所有激活的站点，实际项目中需要根据用户权限过滤
        return siteRepository.findByStatusAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(SiteStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code, Long excludeId) {
        if (excludeId == null) {
            return siteRepository.existsByCodeAndDeletedFalse(code);
        } else {
            return siteRepository.existsByCodeAndIdNotAndDeletedFalse(code, excludeId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDomain(String domain, Long excludeId) {
        if (excludeId == null) {
            return siteRepository.existsByDomainAndDeletedFalse(domain);
        } else {
            return siteRepository.existsByDomainAndIdNotAndDeletedFalse(domain, excludeId);
        }
    }
}
