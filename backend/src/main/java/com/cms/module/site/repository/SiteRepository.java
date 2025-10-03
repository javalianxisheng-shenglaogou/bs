package com.cms.module.site.repository;

import com.cms.module.site.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 站点仓储接口
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {

    /**
     * 根据代码查找站点
     */
    Optional<Site> findByCodeAndDeletedFalse(String code);

    /**
     * 根据域名查找站点
     */
    Optional<Site> findByDomainAndDeletedFalse(String domain);

    /**
     * 查找默认站点
     */
    Optional<Site> findByIsDefaultTrueAndDeletedFalse();

    /**
     * 根据状态查找站点列表
     */
    List<Site> findByStatusAndDeletedFalse(String status);

    /**
     * 查找所有未删除的站点
     */
    List<Site> findByDeletedFalse();

    /**
     * 分页查询未删除的站点
     */
    Page<Site> findByDeletedFalse(Pageable pageable);

    /**
     * 检查代码是否存在
     */
    boolean existsByCodeAndDeletedFalse(String code);

    /**
     * 检查域名是否存在
     */
    boolean existsByDomainAndDeletedFalse(String domain);

    /**
     * 检查代码是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Site s WHERE s.code = :code AND s.id != :id AND s.deleted = false")
    boolean existsByCodeAndIdNotAndDeletedFalse(@Param("code") String code, @Param("id") Long id);

    /**
     * 检查域名是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Site s WHERE s.domain = :domain AND s.id != :id AND s.deleted = false")
    boolean existsByDomainAndIdNotAndDeletedFalse(@Param("domain") String domain, @Param("id") Long id);
}

