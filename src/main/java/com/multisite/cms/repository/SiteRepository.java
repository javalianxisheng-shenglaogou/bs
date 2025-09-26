package com.multisite.cms.repository;

import com.multisite.cms.entity.Site;
import com.multisite.cms.enums.SiteStatus;
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
 * 站点Repository接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Long>, JpaSpecificationExecutor<Site> {

    /**
     * 根据站点代码查找站点
     * 
     * @param code 站点代码
     * @return 站点Optional
     */
    Optional<Site> findByCodeAndDeletedFalse(String code);

    /**
     * 根据域名查找站点
     * 
     * @param domain 域名
     * @return 站点Optional
     */
    Optional<Site> findByDomainAndDeletedFalse(String domain);

    /**
     * 检查站点代码是否存在
     * 
     * @param code 站点代码
     * @return 是否存在
     */
    boolean existsByCodeAndDeletedFalse(String code);

    /**
     * 检查域名是否存在
     * 
     * @param domain 域名
     * @return 是否存在
     */
    boolean existsByDomainAndDeletedFalse(String domain);

    /**
     * 检查站点代码是否存在（排除指定站点）
     * 
     * @param code 站点代码
     * @param siteId 排除的站点ID
     * @return 是否存在
     */
    boolean existsByCodeAndIdNotAndDeletedFalse(String code, Long siteId);

    /**
     * 检查域名是否存在（排除指定站点）
     * 
     * @param domain 域名
     * @param siteId 排除的站点ID
     * @return 是否存在
     */
    boolean existsByDomainAndIdNotAndDeletedFalse(String domain, Long siteId);

    /**
     * 根据状态查找站点
     * 
     * @param status 站点状态
     * @param pageable 分页参数
     * @return 站点分页结果
     */
    Page<Site> findByStatusAndDeletedFalse(SiteStatus status, Pageable pageable);

    /**
     * 查找所有激活的站点
     * 
     * @return 站点列表
     */
    List<Site> findByStatusAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(SiteStatus status);

    /**
     * 根据所有者查找站点
     * 
     * @param ownerId 所有者ID
     * @param pageable 分页参数
     * @return 站点分页结果
     */
    Page<Site> findByOwnerIdAndDeletedFalse(Long ownerId, Pageable pageable);

    /**
     * 根据父站点查找子站点
     * 
     * @param parentSiteId 父站点ID
     * @return 子站点列表
     */
    List<Site> findByParentSiteIdAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(Long parentSiteId);

    /**
     * 查找根站点（无父站点）
     * 
     * @return 根站点列表
     */
    List<Site> findByParentSiteIsNullAndDeletedFalseOrderBySortOrderAscCreatedAtAsc();

    /**
     * 根据关键字搜索站点
     * 
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 站点分页结果
     */
    @Query("SELECT s FROM Site s WHERE s.deleted = false AND " +
           "(s.name LIKE %:keyword% OR s.code LIKE %:keyword% OR s.domain LIKE %:keyword% OR s.description LIKE %:keyword%)")
    Page<Site> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据状态和关键字搜索站点
     * 
     * @param status 站点状态
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 站点分页结果
     */
    @Query("SELECT s FROM Site s WHERE s.deleted = false AND s.status = :status AND " +
           "(s.name LIKE %:keyword% OR s.code LIKE %:keyword% OR s.domain LIKE %:keyword% OR s.description LIKE %:keyword%)")
    Page<Site> findByStatusAndKeyword(@Param("status") SiteStatus status, 
                                     @Param("keyword") String keyword, 
                                     Pageable pageable);

    /**
     * 查找用户管理的站点
     * 
     * @param userId 用户ID
     * @return 站点列表
     */
    @Query("SELECT DISTINCT s FROM Site s LEFT JOIN s.userRoles ur " +
           "WHERE s.deleted = false AND " +
           "(s.owner.id = :userId OR " +
           "(ur.user.id = :userId AND ur.deleted = false AND ur.role.code IN ('SUPER_ADMIN', 'SITE_ADMIN') AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)))")
    List<Site> findManagedSitesByUserId(@Param("userId") Long userId);

    /**
     * 查找用户有权限访问的站点
     * 
     * @param userId 用户ID
     * @return 站点列表
     */
    @Query("SELECT DISTINCT s FROM Site s LEFT JOIN s.userRoles ur " +
           "WHERE s.deleted = false AND s.status = 'ACTIVE' AND " +
           "(s.owner.id = :userId OR " +
           "(ur.user.id = :userId AND ur.deleted = false AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)))")
    List<Site> findAccessibleSitesByUserId(@Param("userId") Long userId);

    /**
     * 统计站点数量（按状态）
     * 
     * @param status 站点状态
     * @return 站点数量
     */
    long countByStatusAndDeletedFalse(SiteStatus status);

    /**
     * 统计用户拥有的站点数量
     * 
     * @param ownerId 所有者ID
     * @return 站点数量
     */
    long countByOwnerIdAndDeletedFalse(Long ownerId);

    /**
     * 统计子站点数量
     * 
     * @param parentSiteId 父站点ID
     * @return 子站点数量
     */
    long countByParentSiteIdAndDeletedFalse(Long parentSiteId);

    /**
     * 统计总站点数量（未删除）
     * 
     * @return 站点数量
     */
    long countByDeletedFalse();

    /**
     * 批量更新站点状态
     * 
     * @param siteIds 站点ID列表
     * @param status 新状态
     */
    @Modifying
    @Query("UPDATE Site s SET s.status = :status WHERE s.id IN :siteIds")
    void updateStatusByIds(@Param("siteIds") List<Long> siteIds, @Param("status") SiteStatus status);

    /**
     * 软删除站点
     * 
     * @param siteId 站点ID
     */
    @Modifying
    @Query("UPDATE Site s SET s.deleted = true WHERE s.id = :siteId")
    void softDeleteById(@Param("siteId") Long siteId);

    /**
     * 批量软删除站点
     * 
     * @param siteIds 站点ID列表
     */
    @Modifying
    @Query("UPDATE Site s SET s.deleted = true WHERE s.id IN :siteIds")
    void softDeleteByIds(@Param("siteIds") List<Long> siteIds);

    /**
     * 更新站点排序
     * 
     * @param siteId 站点ID
     * @param sortOrder 排序值
     */
    @Modifying
    @Query("UPDATE Site s SET s.sortOrder = :sortOrder WHERE s.id = :siteId")
    void updateSortOrder(@Param("siteId") Long siteId, @Param("sortOrder") Integer sortOrder);

    /**
     * 查找站点的所有子站点（递归）
     * 
     * @param parentSiteId 父站点ID
     * @return 子站点ID列表
     */
    @Query(value = "WITH RECURSIVE site_tree AS (" +
                   "  SELECT id, parent_site_id, 1 as level FROM sites WHERE parent_site_id = :parentSiteId AND deleted = false" +
                   "  UNION ALL" +
                   "  SELECT s.id, s.parent_site_id, st.level + 1 FROM sites s" +
                   "  INNER JOIN site_tree st ON s.parent_site_id = st.id" +
                   "  WHERE s.deleted = false AND st.level < 10" + // 防止无限递归
                   ") SELECT id FROM site_tree", 
           nativeQuery = true)
    List<Long> findAllChildSiteIds(@Param("parentSiteId") Long parentSiteId);

    /**
     * 查找站点的所有父站点（递归）
     * 
     * @param siteId 站点ID
     * @return 父站点ID列表
     */
    @Query(value = "WITH RECURSIVE site_tree AS (" +
                   "  SELECT id, parent_site_id, 1 as level FROM sites WHERE id = :siteId AND deleted = false" +
                   "  UNION ALL" +
                   "  SELECT s.id, s.parent_site_id, st.level + 1 FROM sites s" +
                   "  INNER JOIN site_tree st ON s.id = st.parent_site_id" +
                   "  WHERE s.deleted = false AND st.level < 10" + // 防止无限递归
                   ") SELECT id FROM site_tree WHERE id != :siteId", 
           nativeQuery = true)
    List<Long> findAllParentSiteIds(@Param("siteId") Long siteId);

    /**
     * 查找所有未删除的站点（分页）
     */
    Page<Site> findByDeletedFalse(Pageable pageable);

    /**
     * 根据父站点ID查找子站点
     */
    List<Site> findByParentSiteIdAndDeletedFalseOrderByCreatedAtAsc(Long parentSiteId);

    /**
     * 查找根站点（没有父站点的站点）
     */
    List<Site> findByParentSiteIsNullAndDeletedFalseOrderByCreatedAtAsc();
}
