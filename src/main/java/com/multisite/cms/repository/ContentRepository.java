package com.multisite.cms.repository;

import com.multisite.cms.entity.Content;
import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 内容Repository接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

    /**
     * 根据站点ID和slug查找内容
     * 
     * @param siteId 站点ID
     * @param slug URL别名
     * @return 内容Optional
     */
    Optional<Content> findBySiteIdAndSlugAndDeletedFalse(Long siteId, String slug);

    /**
     * 检查slug在站点内是否存在
     * 
     * @param siteId 站点ID
     * @param slug URL别名
     * @return 是否存在
     */
    boolean existsBySiteIdAndSlugAndDeletedFalse(Long siteId, String slug);

    /**
     * 检查slug在站点内是否存在（排除指定内容）
     * 
     * @param siteId 站点ID
     * @param slug URL别名
     * @param contentId 排除的内容ID
     * @return 是否存在
     */
    boolean existsBySiteIdAndSlugAndIdNotAndDeletedFalse(Long siteId, String slug, Long contentId);

    /**
     * 根据站点ID和状态查找内容
     * 
     * @param siteId 站点ID
     * @param status 内容状态
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    Page<Content> findBySiteIdAndStatusAndDeletedFalse(Long siteId, ContentStatus status, Pageable pageable);

    /**
     * 根据站点ID和内容类型查找内容
     * 
     * @param siteId 站点ID
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    Page<Content> findBySiteIdAndContentTypeAndDeletedFalse(Long siteId, ContentType contentType, Pageable pageable);

    /**
     * 根据站点ID查找已发布的内容
     * 
     * @param siteId 站点ID
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.site.id = :siteId AND c.deleted = false AND " +
           "c.status = 'PUBLISHED' AND (c.publishAt IS NULL OR c.publishAt <= :now)")
    Page<Content> findPublishedBySiteId(@Param("siteId") Long siteId, 
                                       @Param("now") LocalDateTime now, 
                                       Pageable pageable);

    /**
     * 根据分类查找内容
     * 
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    Page<Content> findByCategoryIdAndDeletedFalse(Long categoryId, Pageable pageable);

    /**
     * 根据分类查找已发布的内容
     * 
     * @param categoryId 分类ID
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.category.id = :categoryId AND c.deleted = false AND " +
           "c.status = 'PUBLISHED' AND (c.publishAt IS NULL OR c.publishAt <= :now)")
    Page<Content> findPublishedByCategoryId(@Param("categoryId") Long categoryId, 
                                           @Param("now") LocalDateTime now, 
                                           Pageable pageable);

    /**
     * 根据作者查找内容
     * 
     * @param authorId 作者ID
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    Page<Content> findByAuthorIdAndDeletedFalse(Long authorId, Pageable pageable);

    /**
     * 根据关键字搜索内容
     * 
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.deleted = false AND " +
           "(c.title LIKE %:keyword% OR c.summary LIKE %:keyword% OR c.content LIKE %:keyword%)")
    Page<Content> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据站点和关键字搜索内容
     * 
     * @param siteId 站点ID
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.site.id = :siteId AND c.deleted = false AND " +
           "(c.title LIKE %:keyword% OR c.summary LIKE %:keyword% OR c.content LIKE %:keyword%)")
    Page<Content> findBySiteIdAndKeyword(@Param("siteId") Long siteId, 
                                        @Param("keyword") String keyword, 
                                        Pageable pageable);

    /**
     * 查找置顶内容
     * 
     * @param siteId 站点ID
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.site.id = :siteId AND c.deleted = false AND " +
           "c.isTop = true AND c.status = 'PUBLISHED' AND (c.publishAt IS NULL OR c.publishAt <= :now)")
    Page<Content> findTopContentsBySiteId(@Param("siteId") Long siteId, 
                                         @Param("now") LocalDateTime now, 
                                         Pageable pageable);

    /**
     * 查找热门内容（按浏览量排序）
     * 
     * @param siteId 站点ID
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.site.id = :siteId AND c.deleted = false AND " +
           "c.status = 'PUBLISHED' AND (c.publishAt IS NULL OR c.publishAt <= :now) " +
           "ORDER BY c.viewCount DESC, c.publishAt DESC")
    Page<Content> findPopularContentsBySiteId(@Param("siteId") Long siteId, 
                                             @Param("now") LocalDateTime now, 
                                             Pageable pageable);

    /**
     * 查找最新内容
     * 
     * @param siteId 站点ID
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.site.id = :siteId AND c.deleted = false AND " +
           "c.status = 'PUBLISHED' AND (c.publishAt IS NULL OR c.publishAt <= :now) " +
           "ORDER BY c.publishAt DESC, c.createdAt DESC")
    Page<Content> findLatestContentsBySiteId(@Param("siteId") Long siteId, 
                                            @Param("now") LocalDateTime now, 
                                            Pageable pageable);

    /**
     * 查找相关内容（同分类）
     * 
     * @param categoryId 分类ID
     * @param excludeContentId 排除的内容ID
     * @param now 当前时间
     * @param pageable 分页参数
     * @return 内容分页结果
     */
    @Query("SELECT c FROM Content c WHERE c.category.id = :categoryId AND c.id != :excludeContentId AND " +
           "c.deleted = false AND c.status = 'PUBLISHED' AND (c.publishAt IS NULL OR c.publishAt <= :now)")
    Page<Content> findRelatedContents(@Param("categoryId") Long categoryId, 
                                     @Param("excludeContentId") Long excludeContentId,
                                     @Param("now") LocalDateTime now, 
                                     Pageable pageable);

    /**
     * 统计内容数量（按站点和状态）
     * 
     * @param siteId 站点ID
     * @param status 内容状态
     * @return 内容数量
     */
    long countBySiteIdAndStatusAndDeletedFalse(Long siteId, ContentStatus status);

    /**
     * 统计内容数量（按分类）
     * 
     * @param categoryId 分类ID
     * @return 内容数量
     */
    long countByCategoryIdAndDeletedFalse(Long categoryId);

    /**
     * 统计作者的内容数量
     * 
     * @param authorId 作者ID
     * @return 内容数量
     */
    long countByAuthorIdAndDeletedFalse(Long authorId);

    /**
     * 统计站点的内容数量
     * 
     * @param siteId 站点ID
     * @return 内容数量
     */
    long countBySiteIdAndDeletedFalse(Long siteId);

    /**
     * 增加浏览次数
     * 
     * @param contentId 内容ID
     */
    @Modifying
    @Query("UPDATE Content c SET c.viewCount = c.viewCount + 1 WHERE c.id = :contentId")
    void incrementViewCount(@Param("contentId") Long contentId);

    /**
     * 增加点赞次数
     * 
     * @param contentId 内容ID
     */
    @Modifying
    @Query("UPDATE Content c SET c.likeCount = c.likeCount + 1 WHERE c.id = :contentId")
    void incrementLikeCount(@Param("contentId") Long contentId);

    /**
     * 减少点赞次数
     * 
     * @param contentId 内容ID
     */
    @Modifying
    @Query("UPDATE Content c SET c.likeCount = GREATEST(0, c.likeCount - 1) WHERE c.id = :contentId")
    void decrementLikeCount(@Param("contentId") Long contentId);

    /**
     * 批量更新内容状态
     * 
     * @param contentIds 内容ID列表
     * @param status 新状态
     */
    @Modifying
    @Query("UPDATE Content c SET c.status = :status WHERE c.id IN :contentIds")
    void updateStatusByIds(@Param("contentIds") List<Long> contentIds, @Param("status") ContentStatus status);

    /**
     * 批量发布内容
     * 
     * @param contentIds 内容ID列表
     * @param publishAt 发布时间
     */
    @Modifying
    @Query("UPDATE Content c SET c.status = 'PUBLISHED', c.publishAt = :publishAt WHERE c.id IN :contentIds")
    void publishByIds(@Param("contentIds") List<Long> contentIds, @Param("publishAt") LocalDateTime publishAt);

    /**
     * 软删除内容
     * 
     * @param contentId 内容ID
     */
    @Modifying
    @Query("UPDATE Content c SET c.deleted = true WHERE c.id = :contentId")
    void softDeleteById(@Param("contentId") Long contentId);

    /**
     * 批量软删除内容
     * 
     * @param contentIds 内容ID列表
     */
    @Modifying
    @Query("UPDATE Content c SET c.deleted = true WHERE c.id IN :contentIds")
    void softDeleteByIds(@Param("contentIds") List<Long> contentIds);

    /**
     * 根据站点ID查询内容（分页）
     */
    Page<Content> findBySiteIdAndDeletedFalse(Long siteId, Pageable pageable);
}
