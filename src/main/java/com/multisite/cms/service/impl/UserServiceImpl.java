package com.multisite.cms.service.impl;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.UserStatus;
import com.multisite.cms.repository.UserRepository;
import com.multisite.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    @Transactional(readOnly = true)
    public PageResult<User> getUsers(int page, int size, String sort, String direction, 
                                   UserStatus status, String keyword) {
        log.debug("Querying users: page={}, size={}, sort={}, direction={}, status={}, keyword={}", 
                page, size, sort, direction, status, keyword);
        
        // 创建排序对象
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(sortDirection, sort != null ? sort : "createdAt");
        
        // 创建分页对象（Spring Data页码从0开始）
        Pageable pageable = PageRequest.of(page - 1, size, sortObj);
        
        Page<User> userPage;
        
        if (status != null && StringUtils.hasText(keyword)) {
            // 按状态和关键字搜索
            userPage = userRepository.findByStatusAndKeyword(status, keyword, pageable);
        } else if (status != null) {
            // 按状态查询
            userPage = userRepository.findByStatusAndDeletedFalse(status, pageable);
        } else if (StringUtils.hasText(keyword)) {
            // 按关键字搜索
            userPage = userRepository.findByKeyword(keyword, pageable);
        } else {
            // 查询所有
            userPage = userRepository.findAll(pageable);
        }
        
        return PageResult.of(userPage);
    }

    /**
     * 根据ID获取用户
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        log.debug("Getting user by ID: {}", userId);
        return userRepository.findById(userId)
                .filter(user -> !user.isDeleted());
    }

    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        return userRepository.findByUsernameAndDeletedFalse(username);
    }

    /**
     * 根据邮箱获取用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @param createdBy 创建者
     * @return 创建的用户
     */
    @Transactional
    public User createUser(User user, String createdBy) {
        log.info("Creating user: {}", user.getUsername());
        
        // 验证用户名唯一性
        if (userRepository.existsByUsernameAndDeletedFalse(user.getUsername())) {
            throw new RuntimeException("用户名已存在: " + user.getUsername());
        }
        
        // 验证邮箱唯一性
        if (userRepository.existsByEmailAndDeletedFalse(user.getEmail())) {
            throw new RuntimeException("邮箱已存在: " + user.getEmail());
        }
        
        // 加密密码
        if (StringUtils.hasText(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        // 设置创建信息
        user.setCreatedBy(createdBy);
        user.setUpdatedBy(createdBy);
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        
        return savedUser;
    }

    /**
     * 更新用户
     * 
     * @param userId 用户ID
     * @param updateUser 更新的用户信息
     * @param updatedBy 更新者
     * @return 更新后的用户
     */
    @Transactional
    public User updateUser(Long userId, User updateUser, String updatedBy) {
        log.info("Updating user: {}", userId);
        
        User existingUser = userRepository.findById(userId)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        // 验证用户名唯一性（排除当前用户）
        if (StringUtils.hasText(updateUser.getUsername()) && 
            !updateUser.getUsername().equals(existingUser.getUsername()) &&
            userRepository.existsByUsernameAndIdNotAndDeletedFalse(updateUser.getUsername(), userId)) {
            throw new RuntimeException("用户名已存在: " + updateUser.getUsername());
        }
        
        // 验证邮箱唯一性（排除当前用户）
        if (StringUtils.hasText(updateUser.getEmail()) && 
            !updateUser.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmailAndIdNotAndDeletedFalse(updateUser.getEmail(), userId)) {
            throw new RuntimeException("邮箱已存在: " + updateUser.getEmail());
        }
        
        // 更新字段
        if (StringUtils.hasText(updateUser.getUsername())) {
            existingUser.setUsername(updateUser.getUsername());
        }
        if (StringUtils.hasText(updateUser.getEmail())) {
            existingUser.setEmail(updateUser.getEmail());
        }
        if (StringUtils.hasText(updateUser.getNickname())) {
            existingUser.setNickname(updateUser.getNickname());
        }
        if (StringUtils.hasText(updateUser.getPhone())) {
            existingUser.setPhone(updateUser.getPhone());
        }
        if (StringUtils.hasText(updateUser.getAvatarUrl())) {
            existingUser.setAvatarUrl(updateUser.getAvatarUrl());
        }
        if (updateUser.getStatus() != null) {
            existingUser.setStatus(updateUser.getStatus());
        }
        
        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(updateUser.getPasswordHash())) {
            existingUser.setPasswordHash(passwordEncoder.encode(updateUser.getPasswordHash()));
        }
        
        existingUser.setUpdatedBy(updatedBy);
        
        User savedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        
        return savedUser;
    }

    /**
     * 删除用户（软删除）
     * 
     * @param userId 用户ID
     * @param deletedBy 删除者
     */
    @Transactional
    public void deleteUser(Long userId, String deletedBy) {
        log.info("Deleting user: {}", userId);
        
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        user.markAsDeleted();
        user.setUpdatedBy(deletedBy);
        userRepository.save(user);
        
        log.info("User deleted successfully: {} (ID: {})", user.getUsername(), userId);
    }

    /**
     * 批量删除用户
     * 
     * @param userIds 用户ID列表
     * @param deletedBy 删除者
     */
    @Transactional
    public void deleteUsers(List<Long> userIds, String deletedBy) {
        log.info("Batch deleting users: {}", userIds);
        
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> {
            if (!user.isDeleted()) {
                user.markAsDeleted();
                user.setUpdatedBy(deletedBy);
            }
        });
        
        userRepository.saveAll(users);
        log.info("Batch delete completed for {} users", users.size());
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 新状态
     * @param updatedBy 更新者
     */
    @Override
    @Transactional
    public User updateUserStatus(Long userId, UserStatus status, String updatedBy) {
        log.info("Updating user status: {} -> {}", userId, status);

        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setStatus(status);
        user.setUpdatedBy(updatedBy);
        User savedUser = userRepository.save(user);

        log.info("User status updated: {} (ID: {}) -> {}", user.getUsername(), userId, status);
        return savedUser;
    }

    /**
     * 批量更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status 新状态
     */
    @Override
    @Transactional
    public void updateUsersStatus(List<Long> userIds, UserStatus status) {
        log.info("Batch updating user status: {} -> {}", userIds, status);
        userRepository.updateStatusByIds(userIds, status);
        log.info("Batch status update completed for {} users", userIds.size());
    }

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @param excludeUserId 排除的用户ID
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username, Long excludeUserId) {
        if (excludeUserId != null) {
            return !userRepository.existsByUsernameAndIdNotAndDeletedFalse(username, excludeUserId);
        } else {
            return !userRepository.existsByUsernameAndDeletedFalse(username);
        }
    }

    /**
     * 检查邮箱是否可用
     * 
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        if (excludeUserId != null) {
            return !userRepository.existsByEmailAndIdNotAndDeletedFalse(email, excludeUserId);
        } else {
            return !userRepository.existsByEmailAndDeletedFalse(email);
        }
    }

    /**
     * 获取用户统计信息
     *
     * @return 统计信息
     */
    @Override
    @Transactional(readOnly = true)
    public UserService.UserStats getUserStats() {
        long totalUsers = userRepository.countByDeletedFalse();
        long activeUsers = userRepository.countByStatusAndDeletedFalse(UserStatus.ACTIVE);
        long inactiveUsers = userRepository.countByStatusAndDeletedFalse(UserStatus.INACTIVE);
        long lockedUsers = userRepository.countByStatusAndDeletedFalse(UserStatus.LOCKED);
        
        UserService.UserStats stats = new UserService.UserStats();
        stats.setTotalUsers(totalUsers);
        stats.setActiveUsers(activeUsers);
        stats.setInactiveUsers(inactiveUsers);
        stats.setLockedUsers(lockedUsers);

        return stats;
    }

    @Override
    @Transactional
    public void updateLastLoginTime(Long userId, LocalDateTime lastLoginTime) {
        log.debug("Updating last login time for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setLastLoginAt(lastLoginTime);
        userRepository.save(user);

        log.info("Updated last login time for user: {}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword, String updatedBy) {
        log.debug("Resetting password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        // 这里应该使用密码编码器，暂时直接设置
        user.setPasswordHash(newPassword);
        user.setUpdatedBy(updatedBy);
        userRepository.save(user);

        log.info("Password reset for user: {}", userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.debug("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }

        // 使用密码编码器加密新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedBy(user.getUsername());
        userRepository.save(user);

        log.info("Password changed for user: {}", userId);
    }

    @Override
    @Transactional
    public void unlockUser(Long userId, String updatedBy) {
        log.debug("Unlocking user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedBy(updatedBy);
        userRepository.save(user);

        log.info("User unlocked: {}", userId);
    }

    @Override
    @Transactional
    public void lockUser(Long userId, String updatedBy) {
        log.debug("Locking user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setStatus(UserStatus.LOCKED);
        user.setUpdatedBy(updatedBy);
        userRepository.save(user);

        log.info("User locked: {}", userId);
    }

    @Override
    @Transactional
    public void activateUser(Long userId, String updatedBy) {
        log.debug("Activating user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedBy(updatedBy);
        userRepository.save(user);

        log.info("User activated: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeletedFalse(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedFalse(email);
    }
}
