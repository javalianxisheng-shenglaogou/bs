package com.cms.module.site.service;

import com.cms.module.site.dto.SiteDTO;
import com.cms.module.site.dto.SiteQueryDTO;
import com.cms.module.site.entity.Site;
import com.cms.module.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站点服务
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    // 注入其他Repository用于统计
    // private final ContentRepository contentRepository;
    // private final CategoryRepository categoryRepository;

    /**
     * 创建站点
     */
    @Transactional
    public SiteDTO createSite(SiteDTO siteDTO) {
        log.info("创建站点: {}", siteDTO.getName());

        // 检查代码是否已存在
        if (siteRepository.existsByCodeAndDeletedFalse(siteDTO.getCode())) {
            throw new RuntimeException("站点代码已存在: " + siteDTO.getCode());
        }

        // 检查域名是否已存在
        if (siteRepository.existsByDomainAndDeletedFalse(siteDTO.getDomain())) {
            throw new RuntimeException("站点域名已存在: " + siteDTO.getDomain());
        }

        // 如果设置为默认站点，需要取消其他站点的默认状态
        if (Boolean.TRUE.equals(siteDTO.getIsDefault())) {
            siteRepository.findByIsDefaultTrueAndDeletedFalse().ifPresent(site -> {
                site.setIsDefault(false);
                siteRepository.save(site);
            });
        }

        Site site = new Site();
        BeanUtils.copyProperties(siteDTO, site);
        
        // 设置默认值
        if (site.getLanguage() == null) {
            site.setLanguage("zh_CN");
        }
        if (site.getTimezone() == null) {
            site.setTimezone("Asia/Shanghai");
        }
        if (site.getStatus() == null) {
            site.setStatus("ACTIVE");
        }
        if (site.getIsDefault() == null) {
            site.setIsDefault(false);
        }

        site = siteRepository.save(site);
        log.info("站点创建成功: id={}, code={}", site.getId(), site.getCode());

        return convertToDTO(site);
    }

    /**
     * 更新站点
     */
    @Transactional
    public SiteDTO updateSite(Long id, SiteDTO siteDTO) {
        log.info("更新站点: id={}", id);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("站点不存在: " + id));

        if (site.getDeleted()) {
            throw new RuntimeException("站点已删除: " + id);
        }

        // 检查代码是否已被其他站点使用
        if (!site.getCode().equals(siteDTO.getCode()) &&
                siteRepository.existsByCodeAndIdNotAndDeletedFalse(siteDTO.getCode(), id)) {
            throw new RuntimeException("站点代码已存在: " + siteDTO.getCode());
        }

        // 检查域名是否已被其他站点使用
        if (!site.getDomain().equals(siteDTO.getDomain()) &&
                siteRepository.existsByDomainAndIdNotAndDeletedFalse(siteDTO.getDomain(), id)) {
            throw new RuntimeException("站点域名已存在: " + siteDTO.getDomain());
        }

        // 如果设置为默认站点，需要取消其他站点的默认状态
        if (Boolean.TRUE.equals(siteDTO.getIsDefault()) && !site.getIsDefault()) {
            siteRepository.findByIsDefaultTrueAndDeletedFalse().ifPresent(defaultSite -> {
                if (!defaultSite.getId().equals(id)) {
                    defaultSite.setIsDefault(false);
                    siteRepository.save(defaultSite);
                }
            });
        }

        // 复制属性（排除审计字段）
        BeanUtils.copyProperties(siteDTO, site, "id", "createdAt", "createdBy", "version");

        site = siteRepository.save(site);
        log.info("站点更新成功: id={}, code={}", site.getId(), site.getCode());

        return convertToDTO(site);
    }

    /**
     * 删除站点（逻辑删除）
     */
    @Transactional
    public void deleteSite(Long id) {
        log.info("删除站点: id={}", id);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("站点不存在: " + id));

        if (site.getDeleted()) {
            throw new RuntimeException("站点已删除: " + id);
        }

        if (Boolean.TRUE.equals(site.getIsDefault())) {
            throw new RuntimeException("不能删除默认站点");
        }

        site.delete();
        siteRepository.save(site);
        log.info("站点删除成功: id={}", id);
    }

    /**
     * 根据ID获取站点
     */
    public SiteDTO getSiteById(Long id) {
        log.debug("获取站点: id={}", id);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("站点不存在: " + id));

        if (site.getDeleted()) {
            throw new RuntimeException("站点已删除: " + id);
        }

        return convertToDTO(site);
    }

    /**
     * 根据代码获取站点
     */
    public SiteDTO getSiteByCode(String code) {
        log.debug("根据代码获取站点: code={}", code);

        Site site = siteRepository.findByCodeAndDeletedFalse(code)
                .orElseThrow(() -> new RuntimeException("站点不存在: " + code));

        return convertToDTO(site);
    }

    /**
     * 获取默认站点
     */
    public SiteDTO getDefaultSite() {
        log.debug("获取默认站点");

        Site site = siteRepository.findByIsDefaultTrueAndDeletedFalse()
                .orElseThrow(() -> new RuntimeException("默认站点不存在"));

        return convertToDTO(site);
    }

    /**
     * 分页查询站点
     */
    public Page<SiteDTO> getSites(SiteQueryDTO queryDTO) {
        log.debug("分页查询站点: {}", queryDTO);

        // 构建排序
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(queryDTO.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                queryDTO.getSortBy()
        );

        // 构建分页（前端传递的page从0开始，Spring Data JPA也是从0开始）
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);

        // 构建查询条件
        Specification<Site> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 未删除
            predicates.add(cb.equal(root.get("deleted"), false));

            // 名称模糊查询
            if (StringUtils.hasText(queryDTO.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + queryDTO.getName() + "%"));
            }

            // 代码模糊查询
            if (StringUtils.hasText(queryDTO.getCode())) {
                predicates.add(cb.like(root.get("code"), "%" + queryDTO.getCode() + "%"));
            }

            // 域名模糊查询
            if (StringUtils.hasText(queryDTO.getDomain())) {
                predicates.add(cb.like(root.get("domain"), "%" + queryDTO.getDomain() + "%"));
            }

            // 状态精确查询
            if (StringUtils.hasText(queryDTO.getStatus())) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            // 是否默认站点
            if (queryDTO.getIsDefault() != null) {
                predicates.add(cb.equal(root.get("isDefault"), queryDTO.getIsDefault()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Site> sitePage = siteRepository.findAll(spec, pageable);
        return sitePage.map(this::convertToDTO);
    }

    /**
     * 获取所有站点列表
     */
    public List<SiteDTO> getAllSites() {
        log.debug("获取所有站点列表");

        List<Site> sites = siteRepository.findByDeletedFalse();
        return sites.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新站点状态
     */
    @Transactional
    public void updateSiteStatus(Long id, String status) {
        log.info("更新站点状态: id={}, status={}", id, status);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("站点不存在: " + id));

        if (site.getDeleted()) {
            throw new RuntimeException("站点已删除: " + id);
        }

        site.setStatus(status);
        siteRepository.save(site);
        log.info("站点状态更新成功: id={}, status={}", id, status);
    }

    /**
     * 设置默认站点
     */
    @Transactional
    public void setDefaultSite(Long id) {
        log.info("设置默认站点: id={}", id);

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("站点不存在: " + id));

        if (site.getDeleted()) {
            throw new RuntimeException("站点已删除: " + id);
        }

        // 取消其他站点的默认状态
        siteRepository.findByIsDefaultTrueAndDeletedFalse().ifPresent(defaultSite -> {
            if (!defaultSite.getId().equals(id)) {
                defaultSite.setIsDefault(false);
                siteRepository.save(defaultSite);
            }
        });

        // 设置当前站点为默认
        site.setIsDefault(true);
        siteRepository.save(site);
        log.info("默认站点设置成功: id={}", id);
    }

    /**
     * 转换为DTO
     */
    private SiteDTO convertToDTO(Site site) {
        SiteDTO dto = new SiteDTO();
        BeanUtils.copyProperties(site, dto);
        return dto;
    }
}

