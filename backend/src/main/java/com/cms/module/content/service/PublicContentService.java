package com.cms.module.content.service;

import com.cms.module.category.dto.CategoryTreeDTO;
import com.cms.module.category.entity.Category;
import com.cms.module.category.repository.CategoryRepository;
import com.cms.module.content.dto.*;
import com.cms.module.content.entity.Content;
import com.cms.module.content.repository.ContentRepository;
import com.cms.module.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公开内容服务（访客端）
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublicContentService {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final SiteRepository siteRepository;

    /**
     * 获取首页数据
     */
    public HomePageDTO getHomePageData(Long siteId) {
        log.info("获取首页数据: siteId={}", siteId);

        HomePageDTO homePageDTO = new HomePageDTO();

        // 1. 获取分类树（含内容统计）
        homePageDTO.setCategories(getCategoryTreeWithCount(siteId));

        // 2. 获取置顶内容（最多6条）
        List<PublicContentDTO> topContents = getPublishedContentsByCondition(siteId, null, true, null, 6);
        homePageDTO.setTopContents(topContents);

        // 3. 获取推荐内容（最多8条）
        List<PublicContentDTO> featuredContents = getPublishedContentsByCondition(siteId, null, null, true, 8);
        homePageDTO.setFeaturedContents(featuredContents);

        return homePageDTO;
    }

    /**
     * 分页查询已发布内容
     */
    public Page<PublicContentDTO> getPublishedContents(PublicContentQueryDTO queryDTO) {
        log.info("分页查询已发布内容: {}", queryDTO);

        // 构建查询条件
        Specification<Content> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 必须是已发布且未删除
            predicates.add(cb.equal(root.get("status"), "PUBLISHED"));
            predicates.add(cb.equal(root.get("deleted"), false));

            // 站点ID
            if (queryDTO.getSiteId() != null) {
                predicates.add(cb.equal(root.get("siteId"), queryDTO.getSiteId()));
            }

            // 分类ID
            if (queryDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), queryDTO.getCategoryId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 按发布时间倒序
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);

        Page<Content> contentPage = contentRepository.findAll(spec, pageable);

        return contentPage.map(this::convertToPublicDTO);
    }

    /**
     * 全文搜索（标题+内容）
     */
    public Page<PublicContentDTO> searchContents(PublicContentQueryDTO queryDTO) {
        log.info("搜索内容: keyword={}, siteId={}", queryDTO.getKeyword(), queryDTO.getSiteId());

        if (queryDTO.getKeyword() == null || queryDTO.getKeyword().trim().isEmpty()) {
            throw new RuntimeException("搜索关键词不能为空");
        }

        String keyword = queryDTO.getKeyword().trim();

        // 构建查询条件
        Specification<Content> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 必须是已发布且未删除
            predicates.add(cb.equal(root.get("status"), "PUBLISHED"));
            predicates.add(cb.equal(root.get("deleted"), false));

            // 站点ID
            if (queryDTO.getSiteId() != null) {
                predicates.add(cb.equal(root.get("siteId"), queryDTO.getSiteId()));
            }

            // 关键词匹配：标题或内容包含关键词
            Predicate keywordPredicate = cb.or(
                cb.like(root.get("title"), "%" + keyword + "%"),
                cb.like(root.get("content"), "%" + keyword + "%")
            );
            predicates.add(keywordPredicate);

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 按发布时间倒序
        Sort sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);

        Page<Content> contentPage = contentRepository.findAll(spec, pageable);

        return contentPage.map(this::convertToPublicDTO);
    }

    /**
     * 获取内容详情并增加浏览量
     */
    @Transactional
    public PublicContentDetailDTO getContentDetail(Long id) {
        log.info("获取内容详情: id={}", id);

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在"));

        // 检查是否已发布
        if (!"PUBLISHED".equals(content.getStatus()) || content.getDeleted()) {
            throw new RuntimeException("内容不可访问");
        }

        // 增加浏览量
        content.setViewCount(content.getViewCount() + 1);
        contentRepository.save(content);

        return convertToDetailDTO(content);
    }

    /**
     * 获取相关推荐
     */
    public List<PublicContentDTO> getRelatedContents(Long contentId, Integer limit) {
        log.info("获取相关内容: contentId={}, limit={}", contentId, limit);

        Content current = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在"));

        // 查询同分类的其他已发布内容
        Specification<Content> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("deleted"), false));
            predicates.add(cb.equal(root.get("status"), "PUBLISHED"));
            predicates.add(cb.equal(root.get("categoryId"), current.getCategoryId()));
            predicates.add(cb.notEqual(root.get("id"), contentId));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        Pageable pageable = PageRequest.of(0, limit, sort);

        return contentRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取分类树（含内容统计）
     */
    private List<CategoryTreeDTO> getCategoryTreeWithCount(Long siteId) {
        // 获取所有可见分类
        List<Category> categories = categoryRepository.findBySiteIdAndIsVisibleAndDeletedFalseOrderBySortOrderAsc(siteId, true);

        // 统计每个分类下的已发布内容数量
        Map<Long, Long> contentCountMap = new HashMap<>();
        for (Category category : categories) {
            long count = countPublishedContentsByCategory(siteId, category.getId());
            contentCountMap.put(category.getId(), count);
        }

        // 构建树形结构
        Map<Long, CategoryTreeDTO> dtoMap = new HashMap<>();
        for (Category category : categories) {
            CategoryTreeDTO dto = new CategoryTreeDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setCode(category.getCode());
            dto.setIconUrl(category.getIconUrl());
            dto.setContentCount(contentCountMap.getOrDefault(category.getId(), 0L));
            dto.setChildren(new ArrayList<>());
            dtoMap.put(category.getId(), dto);
        }

        // 组装父子关系
        List<CategoryTreeDTO> rootNodes = new ArrayList<>();
        for (Category category : categories) {
            CategoryTreeDTO dto = dtoMap.get(category.getId());
            if (category.getParentId() == null) {
                rootNodes.add(dto);
            } else {
                CategoryTreeDTO parent = dtoMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        return rootNodes;
    }

    /**
     * 统计分类下的已发布内容数量
     */
    private long countPublishedContentsByCategory(Long siteId, Long categoryId) {
        Specification<Content> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("siteId"), siteId),
                cb.equal(root.get("categoryId"), categoryId),
                cb.equal(root.get("status"), "PUBLISHED"),
                cb.equal(root.get("deleted"), false)
        );
        return contentRepository.count(spec);
    }

    /**
     * 根据条件获取已发布内容
     */
    private List<PublicContentDTO> getPublishedContentsByCondition(Long siteId, Long categoryId, Boolean isTop, Boolean isFeatured, int limit) {
        Specification<Content> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), "PUBLISHED"));
            predicates.add(cb.equal(root.get("deleted"), false));
            predicates.add(cb.equal(root.get("siteId"), siteId));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (isTop != null) {
                predicates.add(cb.equal(root.get("isTop"), isTop));
            }
            if (isFeatured != null) {
                predicates.add(cb.equal(root.get("isFeatured"), isFeatured));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "publishedAt");
        Pageable pageable = PageRequest.of(0, limit, sort);

        return contentRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为公开内容DTO
     */
    private PublicContentDTO convertToPublicDTO(Content content) {
        PublicContentDTO dto = new PublicContentDTO();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setSummary(content.getSummary());
        dto.setCoverImage(content.getCoverImage());
        dto.setCategoryId(content.getCategoryId());
        dto.setAuthorName(content.getAuthorName());
        dto.setViewCount(content.getViewCount());
        dto.setPublishedAt(content.getPublishedAt());
        dto.setIsTop(content.getIsTop());
        dto.setIsFeatured(content.getIsFeatured());

        // 获取分类名称
        if (content.getCategoryId() != null) {
            categoryRepository.findById(content.getCategoryId())
                    .ifPresent(category -> dto.setCategoryName(category.getName()));
        }

        return dto;
    }

    /**
     * 转换为详情DTO
     */
    private PublicContentDetailDTO convertToDetailDTO(Content content) {
        PublicContentDetailDTO dto = new PublicContentDetailDTO();
        dto.setId(content.getId());
        dto.setSiteId(content.getSiteId());
        dto.setTitle(content.getTitle());
        dto.setContent(content.getContent());
        dto.setSummary(content.getSummary());
        dto.setCoverImage(content.getCoverImage());
        dto.setCategoryId(content.getCategoryId());
        dto.setAuthorName(content.getAuthorName());
        dto.setViewCount(content.getViewCount());
        dto.setPublishedAt(content.getPublishedAt());
        dto.setIsTop(content.getIsTop());
        dto.setIsFeatured(content.getIsFeatured());
        dto.setIsOriginal(content.getIsOriginal());

        // 获取站点名称
        siteRepository.findById(content.getSiteId())
                .ifPresent(site -> dto.setSiteName(site.getName()));

        // 获取分类名称
        if (content.getCategoryId() != null) {
            categoryRepository.findById(content.getCategoryId())
                    .ifPresent(category -> dto.setCategoryName(category.getName()));
        }

        return dto;
    }
}

