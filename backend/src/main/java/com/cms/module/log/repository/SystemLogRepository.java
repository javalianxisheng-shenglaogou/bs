package com.cms.module.log.repository;

import com.cms.module.log.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志Repository
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long>, JpaSpecificationExecutor<SystemLog> {

    /**
     * 根据用户ID查询日志
     */
    List<SystemLog> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(Long userId);

    /**
     * 根据模块查询日志
     */
    List<SystemLog> findByModuleAndDeletedFalseOrderByCreatedAtDesc(String module);

    /**
     * 根据日志级别查询
     */
    List<SystemLog> findByLevelAndDeletedFalseOrderByCreatedAtDesc(String level);

    /**
     * 查询指定时间范围内的日志
     */
    @Query("SELECT l FROM SystemLog l WHERE l.createdAt BETWEEN :startTime AND :endTime AND l.deleted = false ORDER BY l.createdAt DESC")
    List<SystemLog> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各模块的日志数量
     */
    @Query("SELECT l.module, COUNT(l) FROM SystemLog l WHERE l.deleted = false GROUP BY l.module")
    List<Object[]> countByModule();

    /**
     * 统计各级别的日志数量
     */
    @Query("SELECT l.level, COUNT(l) FROM SystemLog l WHERE l.deleted = false GROUP BY l.level")
    List<Object[]> countByLevel();
}

