package com.multisite.cms.service;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface UserService {

    /**
     * 分页查询用户
     *
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序字段
     * @param direction 排序方向
     * @param status 用户状态
     * @param keyword 搜索关键字
     * @return 用户分页结果
     */
    PageResult<User> getUsers(int page, int size, String sort, String direction,
                            UserStatus status, String keyword);

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Optional<User> getUserById(Long userId);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> getUserByEmail(String email);

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @param createdBy 创建者
     * @return 创建的用户
     */
    User createUser(User user, String createdBy);

    /**
     * 更新用户
     *
     * @param userId 用户ID
     * @param updateUser 更新的用户信息
     * @param updatedBy 更新者
     * @return 更新后的用户
     */
    User updateUser(Long userId, User updateUser, String updatedBy);

    /**
     * 删除用户（软删除）
     *
     * @param userId 用户ID
     * @param deletedBy 删除者
     */
    void deleteUser(Long userId, String deletedBy);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     * @param deletedBy 删除者
     */
    void deleteUsers(List<Long> userIds, String deletedBy);

    /**
     * 激活用户
     *
     * @param userId 用户ID
     * @param activatedBy 激活者
     */
    void activateUser(Long userId, String activatedBy);

    /**
     * 锁定用户
     *
     * @param userId 用户ID
     * @param lockedBy 锁定者
     */
    void lockUser(Long userId, String lockedBy);

    /**
     * 解锁用户
     *
     * @param userId 用户ID
     * @param unlockedBy 解锁者
     */
    void unlockUser(Long userId, String unlockedBy);

    /**
     * 更改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param resetBy 重置者
     */
    void resetPassword(Long userId, String newPassword, String resetBy);

    /**
     * 更新最后登录时间
     *
     * @param userId 用户ID
     * @param lastLoginAt 最后登录时间
     */
    void updateLastLoginTime(Long userId, LocalDateTime lastLoginAt);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @param excludeUserId 排除的用户ID
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username, Long excludeUserId);

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @return 是否可用
     */
    boolean isEmailAvailable(String email, Long excludeUserId);

    /**
     * 获取用户统计信息
     *
     * @return 统计信息
     */
    UserStats getUserStats();

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 新状态
     * @param updatedBy 更新者
     */
    User updateUserStatus(Long userId, UserStatus status, String updatedBy);

    /**
     * 批量更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status 新状态
     */
    void updateUsersStatus(List<Long> userIds, UserStatus status);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 用户统计信息类
     */
    class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long inactiveUsers;
        private long lockedUsers;

        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }

        public long getInactiveUsers() { return inactiveUsers; }
        public void setInactiveUsers(long inactiveUsers) { this.inactiveUsers = inactiveUsers; }

        public long getLockedUsers() { return lockedUsers; }
        public void setLockedUsers(long lockedUsers) { this.lockedUsers = lockedUsers; }
    }
}