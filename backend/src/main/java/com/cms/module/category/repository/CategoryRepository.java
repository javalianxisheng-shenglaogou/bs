package com.cms.module.category.repository;

import com.cms.module.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类Repository
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    /**
     * 根据站点ID查询所有分类
     *
     * @param siteId 站点ID
     * @return 分类列表
     */
    List<Category> findBySiteIdAndDeletedFalseOrderBySortOrderAsc(Long siteId);

    /**
     * 根据站点ID和父分类ID查询子分类
     *
     * @param siteId   站点ID
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> findBySiteIdAndParentIdAndDeletedFalseOrderBySortOrderAsc(Long siteId, Long parentId);

    /**
     * 根据站点ID和分类编码查询分类
     *
     * @param siteId 站点ID
     * @param code   分类编码
     * @return 分类
     */
    Category findBySiteIdAndCodeAndDeletedFalse(Long siteId, String code);

    /**
     * 根据站点ID和可见性查询分类
     *
     * @param siteId    站点ID
     * @param isVisible 是否可见
     * @return 分类列表
     */
    List<Category> findBySiteIdAndIsVisibleAndDeletedFalseOrderBySortOrderAsc(Long siteId, Boolean isVisible);

    /**
     * 查询某个分类的所有子分类(递归)
     *
     * @param siteId   站点ID
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Query("SELECT c FROM Category c WHERE c.siteId = :siteId AND c.path LIKE CONCAT(:path, '%') AND c.deleted = false ORDER BY c.sortOrder ASC")
    List<Category> findAllChildren(@Param("siteId") Long siteId, @Param("path") String path);

    /**
     * 检查分类编码是否存在
     *
     * @param siteId 站点ID
     * @param code   分类编码
     * @param id     分类ID(排除自己)
     * @return 是否存在
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.siteId = :siteId AND c.code = :code AND c.id != :id AND c.deleted = false")
    boolean existsByCodeExcludingId(@Param("siteId") Long siteId, @Param("code") String code, @Param("id") Long id);

    /**
     * 检查分类编码是否存在(新增时)
     *
     * @param siteId 站点ID
     * @param code   分类编码
     * @return 是否存在
     */
    boolean existsBySiteIdAndCodeAndDeletedFalse(Long siteId, String code);
}

