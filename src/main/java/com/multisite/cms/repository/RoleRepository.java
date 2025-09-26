package com.multisite.cms.repository;

import com.multisite.cms.entity.Role;
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
 * 角色Repository接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * 根据角色代码查找角色
     * 
     * @param code 角色代码
     * @return 角色Optional
     */
    Optional<Role> findByCodeAndDeletedFalse(String code);

    /**
     * 根据角色名称查找角色
     * 
     * @param name 角色名称
     * @return 角色Optional
     */
    Optional<Role> findByNameAndDeletedFalse(String name);

    /**
     * 检查角色代码是否存在
     * 
     * @param code 角色代码
     * @return 是否存在
     */
    boolean existsByCodeAndDeletedFalse(String code);

    /**
     * 检查角色名称是否存在
     * 
     * @param name 角色名称
     * @return 是否存在
     */
    boolean existsByNameAndDeletedFalse(String name);

    /**
     * 检查角色代码是否存在（排除指定角色）
     * 
     * @param code 角色代码
     * @param roleId 排除的角色ID
     * @return 是否存在
     */
    boolean existsByCodeAndIdNotAndDeletedFalse(String code, Long roleId);

    /**
     * 检查角色名称是否存在（排除指定角色）
     * 
     * @param name 角色名称
     * @param roleId 排除的角色ID
     * @return 是否存在
     */
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long roleId);

    /**
     * 查找所有系统角色
     * 
     * @return 系统角色列表
     */
    List<Role> findByIsSystemTrueAndDeletedFalseOrderByCreatedAt();

    /**
     * 查找所有自定义角色
     * 
     * @return 自定义角色列表
     */
    List<Role> findByIsSystemFalseAndDeletedFalseOrderByCreatedAt();

    /**
     * 根据系统角色标识分页查询
     * 
     * @param isSystem 是否系统角色
     * @param pageable 分页参数
     * @return 角色分页结果
     */
    Page<Role> findByIsSystemAndDeletedFalse(Boolean isSystem, Pageable pageable);

    /**
     * 根据关键字搜索角色
     * 
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 角色分页结果
     */
    @Query("SELECT r FROM Role r WHERE r.deleted = false AND " +
           "(r.name LIKE %:keyword% OR r.code LIKE %:keyword% OR r.description LIKE %:keyword%)")
    Page<Role> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据系统角色标识和关键字搜索角色
     * 
     * @param isSystem 是否系统角色
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 角色分页结果
     */
    @Query("SELECT r FROM Role r WHERE r.deleted = false AND r.isSystem = :isSystem AND " +
           "(r.name LIKE %:keyword% OR r.code LIKE %:keyword% OR r.description LIKE %:keyword%)")
    Page<Role> findByIsSystemAndKeyword(@Param("isSystem") Boolean isSystem, 
                                       @Param("keyword") String keyword, 
                                       Pageable pageable);

    /**
     * 查找用户拥有的角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.userRoles ur " +
           "WHERE r.deleted = false AND ur.deleted = false AND ur.user.id = :userId AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 查找用户在特定站点的角色
     * 
     * @param userId 用户ID
     * @param siteId 站点ID
     * @return 角色列表
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.userRoles ur " +
           "WHERE r.deleted = false AND ur.deleted = false AND ur.user.id = :userId AND ur.site.id = :siteId AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
    List<Role> findByUserIdAndSiteId(@Param("userId") Long userId, @Param("siteId") Long siteId);

    /**
     * 查找用户的全局角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.userRoles ur " +
           "WHERE r.deleted = false AND ur.deleted = false AND ur.user.id = :userId AND ur.site IS NULL AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
    List<Role> findGlobalRolesByUserId(@Param("userId") Long userId);

    /**
     * 统计角色的用户数量
     * 
     * @param roleId 角色ID
     * @return 用户数量
     */
    @Query("SELECT COUNT(DISTINCT ur.user) FROM UserRole ur " +
           "WHERE ur.role.id = :roleId AND ur.deleted = false AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
    long countUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计系统角色数量
     * 
     * @return 系统角色数量
     */
    long countByIsSystemTrueAndDeletedFalse();

    /**
     * 统计自定义角色数量
     * 
     * @return 自定义角色数量
     */
    long countByIsSystemFalseAndDeletedFalse();

    /**
     * 统计总角色数量（未删除）
     * 
     * @return 角色数量
     */
    long countByDeletedFalse();

    /**
     * 查找可分配的角色（非系统角色或指定的系统角色）
     * 
     * @param allowedSystemRoles 允许的系统角色代码列表
     * @return 角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.deleted = false AND " +
           "(r.isSystem = false OR r.code IN :allowedSystemRoles) " +
           "ORDER BY r.isSystem DESC, r.name ASC")
    List<Role> findAssignableRoles(@Param("allowedSystemRoles") List<String> allowedSystemRoles);

    /**
     * 查找所有有效角色（按名称排序）
     * 
     * @return 角色列表
     */
    List<Role> findByDeletedFalseOrderByName();
}
