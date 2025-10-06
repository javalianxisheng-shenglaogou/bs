package com.cms.module.content.repository;

import com.cms.module.content.entity.ContentVersionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 内容版本操作日志Repository
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Repository
public interface ContentVersionLogRepository extends JpaRepository<ContentVersionLog, Long> {

    /**
     * 根据内容ID查询操作日志
     *
     * @param contentId 内容ID
     * @param pageable  分页参数
     * @return 日志列表
     */
    Page<ContentVersionLog> findByContentIdOrderByCreatedAtDesc(Long contentId, Pageable pageable);

    /**
     * 根据版本ID查询操作日志
     *
     * @param versionId 版本ID
     * @return 日志列表
     */
    List<ContentVersionLog> findByVersionIdOrderByCreatedAtDesc(Long versionId);

    /**
     * 根据操作人查询日志
     *
     * @param operatorId 操作人ID
     * @param pageable   分页参数
     * @return 日志列表
     */
    Page<ContentVersionLog> findByOperatorIdOrderByCreatedAtDesc(Long operatorId, Pageable pageable);

    /**
     * 根据操作类型查询日志
     *
     * @param contentId     内容ID
     * @param operationType 操作类型
     * @param pageable      分页参数
     * @return 日志列表
     */
    Page<ContentVersionLog> findByContentIdAndOperationTypeOrderByCreatedAtDesc(
            Long contentId, String operationType, Pageable pageable);
}

