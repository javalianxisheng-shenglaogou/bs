package com.cms.module.content.repository;

import com.cms.module.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 内容仓库
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Repository
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {

    /**
     * 根据ID和未删除标记查找
     */
    Optional<Content> findByIdAndDeletedFalse(Long id);

    /**
     * 根据站点ID和slug查找
     */
    Optional<Content> findBySiteIdAndSlugAndDeletedFalse(Long siteId, String slug);

    /**
     * 检查站点ID和slug是否存在
     */
    boolean existsBySiteIdAndSlugAndDeletedFalse(Long siteId, String slug);

    /**
     * 检查站点ID和slug是否被其他内容使用
     */
    boolean existsBySiteIdAndSlugAndIdNotAndDeletedFalse(Long siteId, String slug, Long id);

    /**
     * 根据站点ID查找所有内容
     */
    List<Content> findBySiteIdAndDeletedFalse(Long siteId);

    /**
     * 根据站点ID分页查找
     */
    Page<Content> findBySiteIdAndDeletedFalse(Long siteId, Pageable pageable);

    /**
     * 根据分类ID查找所有内容
     */
    List<Content> findByCategoryIdAndDeletedFalse(Long categoryId);

    /**
     * 根据作者ID查找所有内容
     */
    List<Content> findByAuthorIdAndDeletedFalse(Long authorId);

    /**
     * 根据状态查找所有内容
     */
    List<Content> findByStatusAndDeletedFalse(String status);
}

