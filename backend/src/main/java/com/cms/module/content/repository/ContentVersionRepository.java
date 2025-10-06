package com.cms.module.content.repository;

import com.cms.module.content.entity.ContentVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 内容版本Repository
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Repository
public interface ContentVersionRepository extends JpaRepository<ContentVersion, Long> {

    /**
     * 根据内容ID查询所有版本（按版本号倒序）
     *
     * @param contentId 内容ID
     * @param pageable  分页参数
     * @return 版本列表
     */
    Page<ContentVersion> findByContentIdOrderByVersionNumberDesc(Long contentId, Pageable pageable);

    /**
     * 根据内容ID和版本号查询版本
     *
     * @param contentId     内容ID
     * @param versionNumber 版本号
     * @return 版本信息
     */
    Optional<ContentVersion> findByContentIdAndVersionNumber(Long contentId, Integer versionNumber);

    /**
     * 获取内容的最新版本号
     *
     * @param contentId 内容ID
     * @return 最新版本号
     */
    @Query("SELECT MAX(v.versionNumber) FROM ContentVersion v WHERE v.contentId = :contentId")
    Integer findMaxVersionNumberByContentId(@Param("contentId") Long contentId);

    /**
     * 获取内容的版本数量
     *
     * @param contentId 内容ID
     * @return 版本数量
     */
    Long countByContentId(Long contentId);

    /**
     * 查询标记为重要的版本
     *
     * @param contentId 内容ID
     * @return 重要版本列表
     */
    List<ContentVersion> findByContentIdAndIsTaggedTrueOrderByVersionNumberDesc(Long contentId);

    /**
     * 根据修改类型查询版本
     *
     * @param contentId  内容ID
     * @param changeType 修改类型
     * @param pageable   分页参数
     * @return 版本列表
     */
    Page<ContentVersion> findByContentIdAndChangeTypeOrderByVersionNumberDesc(
            Long contentId, String changeType, Pageable pageable);

    /**
     * 根据创建人查询版本
     *
     * @param contentId 内容ID
     * @param createdBy 创建人ID
     * @param pageable  分页参数
     * @return 版本列表
     */
    Page<ContentVersion> findByContentIdAndCreatedByOrderByVersionNumberDesc(
            Long contentId, Long createdBy, Pageable pageable);

    /**
     * 删除内容的所有版本（级联删除时使用）
     *
     * @param contentId 内容ID
     */
    void deleteByContentId(Long contentId);
}

