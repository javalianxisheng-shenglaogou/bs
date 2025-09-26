package com.multisite.cms.service.impl;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.Content;
import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;
import com.multisite.cms.repository.ContentRepository;
import com.multisite.cms.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 内容服务实现类
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;

    /**
     * 分页查询内容
     * 
     * @param siteId 站点ID
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序字段
     * @param direction 排序方向
     * @param status 内容状态
     * @param contentType 内容类型
     * @param categoryId 分类ID
     * @param keyword 搜索关键字
     * @return 内容分页结果
     */
    @Transactional(readOnly = true)
    public PageResult<Content> getContents(Long siteId, int page, int size, String sort, String direction, 
                                         ContentStatus status, ContentType contentType, Long categoryId, String keyword) {
        log.debug("Querying contents: siteId={}, page={}, size={}, sort={}, direction={}, status={}, type={}, categoryId={}, keyword={}", 
                siteId, page, size, sort, direction, status, contentType, categoryId, keyword);
        
        // 创建排序对象
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(sortDirection, sort != null ? sort : "createdAt");
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, sortObj);
        
        Page<Content> contentPage;
        
        if (StringUtils.hasText(keyword)) {
            // 按关键字搜索
            if (siteId != null) {
                contentPage = contentRepository.findBySiteIdAndKeyword(siteId, keyword, pageable);
            } else {
                contentPage = contentRepository.findByKeyword(keyword, pageable);
            }
        } else if (status != null && siteId != null) {
            // 按状态查询
            contentPage = contentRepository.findBySiteIdAndStatusAndDeletedFalse(siteId, status, pageable);
        } else if (contentType != null && siteId != null) {
            // 按类型查询
            contentPage = contentRepository.findBySiteIdAndContentTypeAndDeletedFalse(siteId, contentType, pageable);
        } else if (categoryId != null) {
            // 按分类查询
            contentPage = contentRepository.findByCategoryIdAndDeletedFalse(categoryId, pageable);
        } else if (siteId != null) {
            // 按站点查询
            contentPage = contentRepository.findBySiteIdAndDeletedFalse(siteId, pageable);
        } else {
            // 查询所有
            contentPage = contentRepository.findAll(pageable);
        }
        
        return PageResult.of(contentPage);
    }

    /**
     * 获取已发布的内容
     * 
     * @param siteId 站点ID
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序字段
     * @param direction 排序方向
     * @return 内容分页结果
     */
    @Transactional(readOnly = true)
    public PageResult<Content> getPublishedContents(Long siteId, int page, int size, String sort, String direction) {
        log.debug("Querying published contents: siteId={}, page={}, size={}", siteId, page, size);
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(sortDirection, sort != null ? sort : "publishAt");
        
        Pageable pageable = PageRequest.of(page - 1, size, sortObj);
        
        Page<Content> contentPage = contentRepository.findPublishedBySiteId(siteId, LocalDateTime.now(), pageable);
        return PageResult.of(contentPage);
    }

    /**
     * 根据ID获取内容
     * 
     * @param contentId 内容ID
     * @return 内容信息
     */
    @Transactional(readOnly = true)
    public Optional<Content> getContentById(Long contentId) {
        log.debug("Getting content by ID: {}", contentId);
        return contentRepository.findById(contentId)
                .filter(content -> !content.isDeleted());
    }

    /**
     * 根据站点ID和slug获取内容
     * 
     * @param siteId 站点ID
     * @param slug URL别名
     * @return 内容信息
     */
    @Transactional(readOnly = true)
    public Optional<Content> getContentBySlug(Long siteId, String slug) {
        log.debug("Getting content by slug: siteId={}, slug={}", siteId, slug);
        return contentRepository.findBySiteIdAndSlugAndDeletedFalse(siteId, slug);
    }

    /**
     * 创建内容
     * 
     * @param content 内容信息
     * @param createdBy 创建者
     * @return 创建的内容
     */
    @Transactional
    public Content createContent(Content content, String createdBy) {
        log.info("Creating content: siteId={}, title={}", content.getSite().getId(), content.getTitle());
        
        // 验证slug唯一性
        if (StringUtils.hasText(content.getSlug()) && 
            contentRepository.existsBySiteIdAndSlugAndDeletedFalse(content.getSite().getId(), content.getSlug())) {
            throw new RuntimeException("URL别名已存在: " + content.getSlug());
        }
        
        // 如果没有提供slug，自动生成
        if (!StringUtils.hasText(content.getSlug())) {
            content.setSlug(generateSlug(content.getTitle()));
        }
        
        // 设置创建信息
        content.setCreatedBy(createdBy);
        content.setUpdatedBy(createdBy);
        
        // 设置默认状态
        if (content.getStatus() == null) {
            content.setStatus(ContentStatus.DRAFT);
        }
        
        // 初始化计数器
        if (content.getViewCount() == null) {
            content.setViewCount(0);
        }
        if (content.getLikeCount() == null) {
            content.setLikeCount(0);
        }
        
        Content savedContent = contentRepository.save(content);
        log.info("Content created successfully: {} (ID: {})", savedContent.getTitle(), savedContent.getId());
        
        return savedContent;
    }

    /**
     * 更新内容
     * 
     * @param contentId 内容ID
     * @param updateContent 更新的内容信息
     * @param updatedBy 更新者
     * @return 更新后的内容
     */
    @Transactional
    public Content updateContent(Long contentId, Content updateContent, String updatedBy) {
        log.info("Updating content: {}", contentId);
        
        Content existingContent = contentRepository.findById(contentId)
                .filter(content -> !content.isDeleted())
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        
        // 验证slug唯一性（排除当前内容）
        if (StringUtils.hasText(updateContent.getSlug()) && 
            !updateContent.getSlug().equals(existingContent.getSlug()) &&
            contentRepository.existsBySiteIdAndSlugAndIdNotAndDeletedFalse(
                    existingContent.getSite().getId(), updateContent.getSlug(), contentId)) {
            throw new RuntimeException("URL别名已存在: " + updateContent.getSlug());
        }
        
        // 更新字段
        if (StringUtils.hasText(updateContent.getTitle())) {
            existingContent.setTitle(updateContent.getTitle());
        }
        if (StringUtils.hasText(updateContent.getSlug())) {
            existingContent.setSlug(updateContent.getSlug());
        }
        if (StringUtils.hasText(updateContent.getSummary())) {
            existingContent.setSummary(updateContent.getSummary());
        }
        if (StringUtils.hasText(updateContent.getContent())) {
            existingContent.setContent(updateContent.getContent());
        }
        if (updateContent.getContentType() != null) {
            existingContent.setContentType(updateContent.getContentType());
        }
        if (updateContent.getStatus() != null) {
            existingContent.setStatus(updateContent.getStatus());
        }
        if (updateContent.getCategory() != null) {
            existingContent.setCategory(updateContent.getCategory());
        }
        if (updateContent.getIsTop() != null) {
            existingContent.setIsTop(updateContent.getIsTop());
        }
        if (updateContent.getPublishAt() != null) {
            existingContent.setPublishAt(updateContent.getPublishAt());
        }
        if (StringUtils.hasText(updateContent.getFeaturedImage())) {
            existingContent.setFeaturedImage(updateContent.getFeaturedImage());
        }
        if (StringUtils.hasText(updateContent.getSeoTitle())) {
            existingContent.setSeoTitle(updateContent.getSeoTitle());
        }
        if (StringUtils.hasText(updateContent.getSeoDescription())) {
            existingContent.setSeoDescription(updateContent.getSeoDescription());
        }
        if (StringUtils.hasText(updateContent.getSeoKeywords())) {
            existingContent.setSeoKeywords(updateContent.getSeoKeywords());
        }
        
        existingContent.setUpdatedBy(updatedBy);
        
        Content savedContent = contentRepository.save(existingContent);
        log.info("Content updated successfully: {} (ID: {})", savedContent.getTitle(), savedContent.getId());
        
        return savedContent;
    }

    /**
     * 删除内容（软删除）
     * 
     * @param contentId 内容ID
     * @param deletedBy 删除者
     */
    @Transactional
    public void deleteContent(Long contentId, String deletedBy) {
        log.info("Deleting content: {}", contentId);
        
        Content content = contentRepository.findById(contentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        
        content.markAsDeleted();
        content.setUpdatedBy(deletedBy);
        contentRepository.save(content);
        
        log.info("Content deleted successfully: {} (ID: {})", content.getTitle(), contentId);
    }

    /**
     * 批量删除内容
     * 
     * @param contentIds 内容ID列表
     * @param deletedBy 删除者
     */
    @Transactional
    public void deleteContents(List<Long> contentIds, String deletedBy) {
        log.info("Batch deleting contents: {}", contentIds);
        
        List<Content> contents = contentRepository.findAllById(contentIds);
        contents.forEach(content -> {
            if (!content.isDeleted()) {
                content.markAsDeleted();
                content.setUpdatedBy(deletedBy);
            }
        });
        
        contentRepository.saveAll(contents);
        log.info("Batch delete completed for {} contents", contents.size());
    }

    /**
     * 发布内容
     * 
     * @param contentId 内容ID
     * @param publishAt 发布时间
     * @param publishedBy 发布者
     */
    @Transactional
    public void publishContent(Long contentId, LocalDateTime publishAt, String publishedBy) {
        log.info("Publishing content: {} at {}", contentId, publishAt);
        
        Content content = contentRepository.findById(contentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        
        content.setStatus(ContentStatus.PUBLISHED);
        content.setPublishAt(publishAt != null ? publishAt : LocalDateTime.now());
        content.setUpdatedBy(publishedBy);
        
        contentRepository.save(content);
        log.info("Content published successfully: {} (ID: {})", content.getTitle(), contentId);
    }

    /**
     * 批量发布内容
     * 
     * @param contentIds 内容ID列表
     * @param publishAt 发布时间
     */
    @Transactional
    public void publishContents(List<Long> contentIds, LocalDateTime publishAt) {
        log.info("Batch publishing contents: {} at {}", contentIds, publishAt);
        contentRepository.publishByIds(contentIds, publishAt != null ? publishAt : LocalDateTime.now());
        log.info("Batch publish completed for {} contents", contentIds.size());
    }

    /**
     * 增加浏览次数
     * 
     * @param contentId 内容ID
     */
    @Transactional
    public void incrementViewCount(Long contentId) {
        log.debug("Incrementing view count for content: {}", contentId);
        contentRepository.incrementViewCount(contentId);
    }

    /**
     * 点赞内容
     * 
     * @param contentId 内容ID
     */
    @Transactional
    public void likeContent(Long contentId) {
        log.debug("Liking content: {}", contentId);
        contentRepository.incrementLikeCount(contentId);
    }

    /**
     * 取消点赞
     * 
     * @param contentId 内容ID
     */
    @Transactional
    public void unlikeContent(Long contentId) {
        log.debug("Unliking content: {}", contentId);
        contentRepository.decrementLikeCount(contentId);
    }

    /**
     * 检查slug是否可用
     * 
     * @param siteId 站点ID
     * @param slug URL别名
     * @param excludeContentId 排除的内容ID
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isSlugAvailable(Long siteId, String slug, Long excludeContentId) {
        if (excludeContentId != null) {
            return !contentRepository.existsBySiteIdAndSlugAndIdNotAndDeletedFalse(siteId, slug, excludeContentId);
        } else {
            return !contentRepository.existsBySiteIdAndSlugAndDeletedFalse(siteId, slug);
        }
    }

    /**
     * 获取内容统计信息
     * 
     * @param siteId 站点ID
     * @return 统计信息
     */
    @Transactional(readOnly = true)
    public ContentService.ContentStatistics getContentStats(Long siteId) {
        long totalContents = contentRepository.countBySiteIdAndDeletedFalse(siteId);
        long publishedContents = contentRepository.countBySiteIdAndStatusAndDeletedFalse(siteId, ContentStatus.PUBLISHED);
        long draftContents = contentRepository.countBySiteIdAndStatusAndDeletedFalse(siteId, ContentStatus.DRAFT);
        long pendingContents = contentRepository.countBySiteIdAndStatusAndDeletedFalse(siteId, ContentStatus.PENDING);
        
        return new ContentService.ContentStatistics(
                totalContents, publishedContents, draftContents, pendingContents, 0L, 0L, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Content> getRelatedContents(Long contentId, int limit) {
        // 暂时返回空列表，后续实现相关内容推荐算法
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Content> searchContents(Long siteId, String keyword, int page, int size) {
        // 暂时返回空结果，后续实现搜索功能
        return PageResult.empty(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Content> getPopularContents(Long siteId, int limit) {
        // 暂时返回空列表，后续实现热门内容算法
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Content> getRecentlyUpdatedContents(Long siteId, int limit) {
        // 暂时返回空列表，后续实现最近更新内容查询
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public ContentService.ContentStatistics getContentStatistics(Long siteId) {
        // 复用getContentStats方法
        return getContentStats(siteId);
    }

    @Override
    @Transactional
    public Content archiveContent(Long contentId, String updatedBy) {
        log.debug("Archiving content: {}", contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));

        content.setStatus(ContentStatus.ARCHIVED);
        content.setUpdatedBy(updatedBy);
        Content savedContent = contentRepository.save(content);

        log.info("Content archived: {}", contentId);
        return savedContent;
    }

    @Override
    @Transactional
    public Content unpublishContent(Long contentId, String updatedBy) {
        log.debug("Unpublishing content: {}", contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));

        content.setStatus(ContentStatus.DRAFT);
        content.setUpdatedBy(updatedBy);
        Content savedContent = contentRepository.save(content);

        log.info("Content unpublished: {}", contentId);
        return savedContent;
    }

    @Override
    @Transactional
    public Content publishContent(Long contentId, String publishedBy) {
        log.debug("Publishing content: {}", contentId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));

        content.setStatus(ContentStatus.PUBLISHED);
        content.setPublishAt(LocalDateTime.now());
        content.setUpdatedBy(publishedBy);
        Content savedContent = contentRepository.save(content);

        log.info("Content published: {}", contentId);
        return savedContent;
    }

    /**
     * 生成slug
     * 
     * @param title 标题
     * @return slug
     */
    private String generateSlug(String title) {
        if (!StringUtils.hasText(title)) {
            return "untitled-" + System.currentTimeMillis();
        }
        
        // 简单的slug生成逻辑，实际项目中可能需要更复杂的处理
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    /**
     * 内容统计信息类
     */
    @lombok.Data
    @lombok.Builder
    public static class ContentStats {
        private long totalContents;
        private long publishedContents;
        private long draftContents;
        private long pendingContents;
    }
}
