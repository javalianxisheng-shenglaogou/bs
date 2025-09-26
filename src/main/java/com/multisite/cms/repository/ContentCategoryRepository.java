package com.multisite.cms.repository;

import com.multisite.cms.entity.ContentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 内容分类Repository接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long>, JpaSpecificationExecutor<ContentCategory> {

    /**
     * 根据站点ID和代码查找分类
     * 
     * @param siteId 站点ID
     * @param code 分类代码
     * @return 分类Optional
     */
    Optional<ContentCategory> findBySiteIdAndCodeAndDeletedFalse(Long siteId, String code);

    /**
     * 根据站点ID和路径查找分类
     * 
     * @param siteId 站点ID
     * @param path 分类路径
     * @return 分类Optional
     */
    Optional<ContentCategory> findBySiteIdAndPathAndDeletedFalse(Long siteId, String path);

    /**
     * 检查分类代码在站点内是否存在
     *
     * @param siteId 站点ID
     * @param code 分类代码
     * @return 是否存在
     */
    boolean existsBySiteIdAndCodeAndDeletedFalse(Long siteId, String code);

    /**
     * 检查分类名称在站点内是否存在
     *
     * @param siteId 站点ID
     * @param name 分类名称
     * @return 是否存在
     */
    boolean existsBySiteIdAndNameAndDeletedFalse(Long siteId, String name);

    /**
     * 检查分类代码在站点内是否存在（排除指定分类）
     * 
     * @param siteId 站点ID
     * @param code 分类代码
     * @param categoryId 排除的分类ID
     * @return 是否存在
     */
    boolean existsBySiteIdAndCodeAndIdNotAndDeletedFalse(Long siteId, String code, Long categoryId);

    /**
     * 根据站点ID查找分类
     * 
     * @param siteId 站点ID
     * @param pageable 分页参数
     * @return 分类分页结果
     */
    Page<ContentCategory> findBySiteIdAndDeletedFalse(Long siteId, Pageable pageable);

    /**
     * 根据站点ID和父分类查找子分类
     * 
     * @param siteId 站点ID
     * @param parentCategoryId 父分类ID
     * @return 子分类列表
     */
    List<ContentCategory> findBySiteIdAndParentIdAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(Long siteId, Long parentId);

    /**
     * 根据站点ID查找根分类（无父分类）
     *
     * @param siteId 站点ID
     * @return 根分类列表
     */
    List<ContentCategory> findBySiteIdAndParentIdIsNullAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(Long siteId);

    /**
     * 根据站点ID和关键字搜索分类
     * 
     * @param siteId 站点ID
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 分类分页结果
     */
    @Query("SELECT c FROM ContentCategory c WHERE c.site.id = :siteId AND c.deleted = false AND " +
           "(c.name LIKE %:keyword% OR c.code LIKE %:keyword% OR c.description LIKE %:keyword%)")
    Page<ContentCategory> findBySiteIdAndKeyword(@Param("siteId") Long siteId, 
                                                @Param("keyword") String keyword, 
                                                Pageable pageable);

    /**
     * 统计站点的分类数量
     * 
     * @param siteId 站点ID
     * @return 分类数量
     */
    long countBySiteIdAndDeletedFalse(Long siteId);

    /**
     * 统计子分类数量
     *
     * @param parentId 父分类ID
     * @return 子分类数量
     */
    long countByParentIdAndDeletedFalse(Long parentId);

    /**
     * 软删除分类
     * 
     * @param categoryId 分类ID
     */
    @Modifying
    @Query("UPDATE ContentCategory c SET c.deleted = true WHERE c.id = :categoryId")
    void softDeleteById(@Param("categoryId") Long categoryId);

    /**
     * 批量软删除分类
     * 
     * @param categoryIds 分类ID列表
     */
    @Modifying
    @Query("UPDATE ContentCategory c SET c.deleted = true WHERE c.id IN :categoryIds")
    void softDeleteByIds(@Param("categoryIds") List<Long> categoryIds);

    /**
     * 更新分类排序
     * 
     * @param categoryId 分类ID
     * @param sortOrder 排序值
     */
    @Modifying
    @Query("UPDATE ContentCategory c SET c.sortOrder = :sortOrder WHERE c.id = :categoryId")
    void updateSortOrder(@Param("categoryId") Long categoryId, @Param("sortOrder") Integer sortOrder);

    /**
     * 查找分类的所有子分类（递归）
     * 
     * @param parentCategoryId 父分类ID
     * @return 子分类ID列表
     */
    @Query(value = "WITH RECURSIVE category_tree AS (" +
                   "  SELECT id, parent_category_id, 1 as level FROM content_categories WHERE parent_category_id = :parentCategoryId AND deleted = false" +
                   "  UNION ALL" +
                   "  SELECT c.id, c.parent_category_id, ct.level + 1 FROM content_categories c" +
                   "  INNER JOIN category_tree ct ON c.parent_category_id = ct.id" +
                   "  WHERE c.deleted = false AND ct.level < 10" + // 防止无限递归
                   ") SELECT id FROM category_tree", 
           nativeQuery = true)
    List<Long> findAllChildCategoryIds(@Param("parentCategoryId") Long parentCategoryId);

    /**
     * 查找分类的所有父分类（递归）
     * 
     * @param categoryId 分类ID
     * @return 父分类ID列表
     */
    @Query(value = "WITH RECURSIVE category_tree AS (" +
                   "  SELECT id, parent_category_id, 1 as level FROM content_categories WHERE id = :categoryId AND deleted = false" +
                   "  UNION ALL" +
                   "  SELECT c.id, c.parent_category_id, ct.level + 1 FROM content_categories c" +
                   "  INNER JOIN category_tree ct ON c.id = ct.parent_category_id" +
                   "  WHERE c.deleted = false AND ct.level < 10" + // 防止无限递归
                   ") SELECT id FROM category_tree WHERE id != :categoryId", 
           nativeQuery = true)
    List<Long> findAllParentCategoryIds(@Param("categoryId") Long categoryId);

    /**
     * 根据路径前缀查找分类
     * 
     * @param siteId 站点ID
     * @param pathPrefix 路径前缀
     * @return 分类列表
     */
    @Query("SELECT c FROM ContentCategory c WHERE c.site.id = :siteId AND c.deleted = false AND c.path LIKE :pathPrefix%")
    List<ContentCategory> findBySiteIdAndPathStartingWith(@Param("siteId") Long siteId, @Param("pathPrefix") String pathPrefix);

    /**
     * 更新分类路径
     * 
     * @param categoryId 分类ID
     * @param path 新路径
     */
    @Modifying
    @Query("UPDATE ContentCategory c SET c.path = :path WHERE c.id = :categoryId")
    void updatePath(@Param("categoryId") Long categoryId, @Param("path") String path);

    /**
     * 批量更新分类路径
     * 
     * @param oldPathPrefix 旧路径前缀
     * @param newPathPrefix 新路径前缀
     * @param siteId 站点ID
     */
    @Modifying
    @Query("UPDATE ContentCategory c SET c.path = CONCAT(:newPathPrefix, SUBSTRING(c.path, LENGTH(:oldPathPrefix) + 1)) " +
           "WHERE c.site.id = :siteId AND c.path LIKE :oldPathPrefix%")
    void updatePathsByPrefix(@Param("oldPathPrefix") String oldPathPrefix, 
                           @Param("newPathPrefix") String newPathPrefix, 
                           @Param("siteId") Long siteId);
}
