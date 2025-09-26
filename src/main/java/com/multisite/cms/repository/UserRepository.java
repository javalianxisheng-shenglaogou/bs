package com.multisite.cms.repository;

import com.multisite.cms.entity.User;
import com.multisite.cms.enums.UserStatus;
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
 * 用户Repository接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户Optional
     */
    Optional<User> findByUsernameAndDeletedFalse(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户Optional
     */
    Optional<User> findByEmailAndDeletedFalse(String email);

    /**
     * 根据用户ID查找用户（包含角色信息）
     *
     * @param id 用户ID
     * @return 用户Optional
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role LEFT JOIN FETCH ur.site WHERE u.id = :id AND u.deleted = false")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    /**
     * 根据用户名或邮箱查找用户
     * 
     * @param username 用户名
     * @param email 邮箱
     * @return 用户Optional
     */
    Optional<User> findByUsernameOrEmailAndDeletedFalse(String username, String email);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsernameAndDeletedFalse(String username);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /**
     * 检查用户名是否存在（排除指定用户）
     * 
     * @param username 用户名
     * @param userId 排除的用户ID
     * @return 是否存在
     */
    boolean existsByUsernameAndIdNotAndDeletedFalse(String username, Long userId);

    /**
     * 检查邮箱是否存在（排除指定用户）
     * 
     * @param email 邮箱
     * @param userId 排除的用户ID
     * @return 是否存在
     */
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long userId);

    /**
     * 根据状态查找用户
     * 
     * @param status 用户状态
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    Page<User> findByStatusAndDeletedFalse(UserStatus status, Pageable pageable);

    /**
     * 根据关键字搜索用户
     * 
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false AND " +
           "(u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%)")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据状态和关键字搜索用户
     * 
     * @param status 用户状态
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.status = :status AND " +
           "(u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%)")
    Page<User> findByStatusAndKeyword(@Param("status") UserStatus status, 
                                     @Param("keyword") String keyword, 
                                     Pageable pageable);

    /**
     * 查找最近登录的用户
     * 
     * @param since 时间起点
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    Page<User> findByLastLoginAtAfterAndDeletedFalseOrderByLastLoginAtDesc(LocalDateTime since, Pageable pageable);

    /**
     * 查找从未登录的用户
     * 
     * @param pageable 分页参数
     * @return 用户分页结果
     */
    Page<User> findByLastLoginAtIsNullAndDeletedFalse(Pageable pageable);

    /**
     * 统计用户数量（按状态）
     * 
     * @param status 用户状态
     * @return 用户数量
     */
    long countByStatusAndDeletedFalse(UserStatus status);

    /**
     * 统计总用户数量（未删除）
     * 
     * @return 用户数量
     */
    long countByDeletedFalse();

    /**
     * 更新用户最后登录时间
     * 
     * @param userId 用户ID
     * @param lastLoginAt 最后登录时间
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);

    /**
     * 批量更新用户状态
     * 
     * @param userIds 用户ID列表
     * @param status 新状态
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")
    void updateStatusByIds(@Param("userIds") List<Long> userIds, @Param("status") UserStatus status);

    /**
     * 软删除用户
     * 
     * @param userId 用户ID
     */
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id = :userId")
    void softDeleteById(@Param("userId") Long userId);

    /**
     * 批量软删除用户
     * 
     * @param userIds 用户ID列表
     */
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id IN :userIds")
    void softDeleteByIds(@Param("userIds") List<Long> userIds);

    /**
     * 查找具有特定角色的用户
     * 
     * @param roleCode 角色代码
     * @return 用户列表
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role r " +
           "WHERE u.deleted = false AND ur.deleted = false AND r.code = :roleCode")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查找在特定站点具有特定角色的用户
     * 
     * @param roleCode 角色代码
     * @param siteId 站点ID
     * @return 用户列表
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role r " +
           "WHERE u.deleted = false AND ur.deleted = false AND r.code = :roleCode AND ur.site.id = :siteId")
    List<User> findByRoleCodeAndSiteId(@Param("roleCode") String roleCode, @Param("siteId") Long siteId);

    /**
     * 查找用户的有效角色数量
     * 
     * @param userId 用户ID
     * @return 角色数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.user.id = :userId AND ur.deleted = false AND " +
           "(ur.expiresAt IS NULL OR ur.expiresAt > CURRENT_TIMESTAMP)")
    long countValidRolesByUserId(@Param("userId") Long userId);
}
