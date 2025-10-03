package com.cms.module.content.service;

import com.cms.common.base.Page;
import com.cms.module.content.dto.ContentDTO;
import com.cms.module.content.dto.ContentQueryDTO;
import com.cms.module.content.entity.Content;
import com.cms.module.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容服务
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    /**
     * 创建内容
     */
    @Transactional
    public ContentDTO createContent(ContentDTO contentDTO) {
        log.info("创建内容: {}", contentDTO.getTitle());

        // 检查slug是否已存在
        if (contentRepository.existsBySiteIdAndSlugAndDeletedFalse(contentDTO.getSiteId(), contentDTO.getSlug())) {
            throw new RuntimeException("URL别名已存在: " + contentDTO.getSlug());
        }

        Content content = new Content();
        BeanUtils.copyProperties(contentDTO, content);

        // 如果状态是已发布，设置发布时间
        if ("PUBLISHED".equals(content.getStatus()) && content.getPublishedAt() == null) {
            content.setPublishedAt(LocalDateTime.now());
        }

        Content saved = contentRepository.save(content);
        log.info("内容创建成功: id={}", saved.getId());

        return convertToDTO(saved);
    }

    /**
     * 更新内容
     */
    @Transactional
    public ContentDTO updateContent(Long id, ContentDTO contentDTO) {
        log.info("更新内容: id={}", id);

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));

        if (content.getDeleted()) {
            throw new RuntimeException("内容已删除: " + id);
        }

        // 检查slug是否已被其他内容使用
        if (!content.getSlug().equals(contentDTO.getSlug()) &&
                contentRepository.existsBySiteIdAndSlugAndIdNotAndDeletedFalse(
                        contentDTO.getSiteId(), contentDTO.getSlug(), id)) {
            throw new RuntimeException("URL别名已存在: " + contentDTO.getSlug());
        }

        // 保存旧状态
        String oldStatus = content.getStatus();

        BeanUtils.copyProperties(contentDTO, content, "id", "createdAt", "createdBy");

        // 如果从非发布状态变为发布状态，设置发布时间
        if (!"PUBLISHED".equals(oldStatus) && "PUBLISHED".equals(content.getStatus()) 
                && content.getPublishedAt() == null) {
            content.setPublishedAt(LocalDateTime.now());
        }

        Content updated = contentRepository.save(content);
        log.info("内容更新成功: id={}", updated.getId());

        return convertToDTO(updated);
    }

    /**
     * 删除内容（软删除）
     */
    @Transactional
    public void deleteContent(Long id) {
        log.info("删除内容: id={}", id);

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));

        if (content.getDeleted()) {
            throw new RuntimeException("内容已删除: " + id);
        }

        content.setDeleted(true);
        contentRepository.save(content);

        log.info("内容删除成功: id={}", id);
    }

    /**
     * 根据ID获取内容
     */
    public ContentDTO getContentById(Long id) {
        log.debug("根据ID获取内容: id={}", id);

        Content content = contentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));

        return convertToDTO(content);
    }

    /**
     * 根据站点ID和slug获取内容
     */
    public ContentDTO getContentBySlug(Long siteId, String slug) {
        log.debug("根据slug获取内容: siteId={}, slug={}", siteId, slug);

        Content content = contentRepository.findBySiteIdAndSlugAndDeletedFalse(siteId, slug)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + slug));

        return convertToDTO(content);
    }

    /**
     * 分页查询内容
     */
    public Page<ContentDTO> getContents(ContentQueryDTO queryDTO) {
        log.debug("分页查询内容: {}", queryDTO);

        // 构建排序
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(queryDTO.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                queryDTO.getSortBy()
        );

        // 构建分页（页码从1开始，需要转换为从0开始）
        Pageable pageable = PageRequest.of(queryDTO.getPage() - 1, queryDTO.getSize(), sort);

        // 构建查询条件
        Specification<Content> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 未删除
            predicates.add(cb.equal(root.get("deleted"), false));

            // 站点ID
            if (queryDTO.getSiteId() != null) {
                predicates.add(cb.equal(root.get("siteId"), queryDTO.getSiteId()));
            }

            // 分类ID
            if (queryDTO.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), queryDTO.getCategoryId()));
            }

            // 标题模糊查询
            if (queryDTO.getTitle() != null && !queryDTO.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + queryDTO.getTitle() + "%"));
            }

            // 内容类型
            if (queryDTO.getContentType() != null && !queryDTO.getContentType().isEmpty()) {
                predicates.add(cb.equal(root.get("contentType"), queryDTO.getContentType()));
            }

            // 状态
            if (queryDTO.getStatus() != null && !queryDTO.getStatus().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), queryDTO.getStatus()));
            }

            // 作者ID
            if (queryDTO.getAuthorId() != null) {
                predicates.add(cb.equal(root.get("authorId"), queryDTO.getAuthorId()));
            }

            // 是否置顶
            if (queryDTO.getIsTop() != null) {
                predicates.add(cb.equal(root.get("isTop"), queryDTO.getIsTop()));
            }

            // 是否推荐
            if (queryDTO.getIsFeatured() != null) {
                predicates.add(cb.equal(root.get("isFeatured"), queryDTO.getIsFeatured()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        org.springframework.data.domain.Page<Content> pageResult = contentRepository.findAll(spec, pageable);

        List<ContentDTO> contentDTOs = pageResult.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new Page<>(
                contentDTOs,
                queryDTO.getPage(),
                queryDTO.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    /**
     * 获取所有内容列表
     */
    public List<ContentDTO> getAllContents(Long siteId) {
        log.debug("获取所有内容列表: siteId={}", siteId);

        List<Content> contents = contentRepository.findBySiteIdAndDeletedFalse(siteId);
        return contents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新内容状态
     */
    @Transactional
    public void updateContentStatus(Long id, String status) {
        log.info("更新内容状态: id={}, status={}", id, status);

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));

        if (content.getDeleted()) {
            throw new RuntimeException("内容已删除: " + id);
        }

        String oldStatus = content.getStatus();
        content.setStatus(status);

        // 如果从非发布状态变为发布状态，设置发布时间
        if (!"PUBLISHED".equals(oldStatus) && "PUBLISHED".equals(status) 
                && content.getPublishedAt() == null) {
            content.setPublishedAt(LocalDateTime.now());
        }

        contentRepository.save(content);
        log.info("内容状态更新成功: id={}, status={}", id, status);
    }

    /**
     * 转换为DTO
     */
    private ContentDTO convertToDTO(Content content) {
        ContentDTO dto = new ContentDTO();
        BeanUtils.copyProperties(content, dto);
        return dto;
    }
}

