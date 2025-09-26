package com.multisite.cms.service;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.Content;
import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;

import java.util.List;
import java.util.Optional;

/**
 * 内容服务接口
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface ContentService {

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
    PageResult<Content> getContents(Long siteId, int page, int size, String sort, String direction,
                                   ContentStatus status, ContentType contentType, Long categoryId, String keyword);

    /**
     * 根据ID获取内容
     * 
     * @param contentId 内容ID
     * @return 内容信息
     */
    Optional<Content> getContentById(Long contentId);

    /**
     * 根据站点ID和slug获取内容
     * 
     * @param siteId 站点ID
     * @param slug 内容slug
     * @return 内容信息
     */
    Optional<Content> getContentBySlug(Long siteId, String slug);

    /**
     * 创建内容
     * 
     * @param content 内容信息
     * @param createdBy 创建者
     * @return 创建的内容
     */
    Content createContent(Content content, String createdBy);

    /**
     * 更新内容
     * 
     * @param contentId 内容ID
     * @param content 内容信息
     * @param updatedBy 更新者
     * @return 更新的内容
     */
    Content updateContent(Long contentId, Content content, String updatedBy);

    /**
     * 删除内容
     * 
     * @param contentId 内容ID
     * @param deletedBy 删除者
     */
    void deleteContent(Long contentId, String deletedBy);

    /**
     * 批量删除内容
     * 
     * @param contentIds 内容ID列表
     * @param deletedBy 删除者
     */
    void deleteContents(List<Long> contentIds, String deletedBy);

    /**
     * 发布内容
     * 
     * @param contentId 内容ID
     * @param publishedBy 发布者
     * @return 发布的内容
     */
    Content publishContent(Long contentId, String publishedBy);

    /**
     * 取消发布内容
     * 
     * @param contentId 内容ID
     * @param unpublishedBy 取消发布者
     * @return 取消发布的内容
     */
    Content unpublishContent(Long contentId, String unpublishedBy);

    /**
     * 归档内容
     * 
     * @param contentId 内容ID
     * @param archivedBy 归档者
     * @return 归档的内容
     */
    Content archiveContent(Long contentId, String archivedBy);

    /**
     * 获取站点内容统计
     * 
     * @param siteId 站点ID
     * @return 内容统计信息
     */
    ContentStatistics getContentStatistics(Long siteId);

    /**
     * 获取最近更新的内容
     * 
     * @param siteId 站点ID
     * @param limit 限制数量
     * @return 最近更新的内容列表
     */
    List<Content> getRecentlyUpdatedContents(Long siteId, int limit);

    /**
     * 获取热门内容
     * 
     * @param siteId 站点ID
     * @param limit 限制数量
     * @return 热门内容列表
     */
    List<Content> getPopularContents(Long siteId, int limit);

    /**
     * 搜索内容
     * 
     * @param siteId 站点ID
     * @param keyword 搜索关键字
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    PageResult<Content> searchContents(Long siteId, String keyword, int page, int size);

    /**
     * 获取相关内容
     *
     * @param contentId 内容ID
     * @param limit 限制数量
     * @return 相关内容列表
     */
    List<Content> getRelatedContents(Long contentId, int limit);

    /**
     * 获取内容统计信息
     *
     * @param siteId 站点ID
     * @return 内容统计信息
     */
    ContentStatistics getContentStats(Long siteId);

    /**
     * 内容统计信息
     */
    class ContentStatistics {
        private long totalCount;
        private long publishedCount;
        private long draftCount;
        private long archivedCount;
        private long articleCount;
        private long pageCount;
        private long mediaCount;

        // 构造函数
        public ContentStatistics() {}

        public ContentStatistics(long totalCount, long publishedCount, long draftCount, 
                               long archivedCount, long articleCount, long pageCount, long mediaCount) {
            this.totalCount = totalCount;
            this.publishedCount = publishedCount;
            this.draftCount = draftCount;
            this.archivedCount = archivedCount;
            this.articleCount = articleCount;
            this.pageCount = pageCount;
            this.mediaCount = mediaCount;
        }

        // Getters and Setters
        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

        public long getPublishedCount() { return publishedCount; }
        public void setPublishedCount(long publishedCount) { this.publishedCount = publishedCount; }

        public long getDraftCount() { return draftCount; }
        public void setDraftCount(long draftCount) { this.draftCount = draftCount; }

        public long getArchivedCount() { return archivedCount; }
        public void setArchivedCount(long archivedCount) { this.archivedCount = archivedCount; }

        public long getArticleCount() { return articleCount; }
        public void setArticleCount(long articleCount) { this.articleCount = articleCount; }

        public long getPageCount() { return pageCount; }
        public void setPageCount(long pageCount) { this.pageCount = pageCount; }

        public long getMediaCount() { return mediaCount; }
        public void setMediaCount(long mediaCount) { this.mediaCount = mediaCount; }
    }
}
