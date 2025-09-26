package com.multisite.cms.service;

import com.multisite.cms.entity.User;
import com.multisite.cms.repository.UserRepository;
import com.multisite.cms.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户详情服务实现类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     * 
     * @param username 用户名（可以是用户名或邮箱）
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .or(() -> userRepository.findByEmailAndDeletedFalse(username))
                .orElseThrow(() -> {
                    log.warn("User not found with username or email: {}", username);
                    return new UsernameNotFoundException("用户不存在: " + username);
                });

        log.debug("User found: {} (ID: {})", user.getUsername(), user.getId());
        return UserPrincipal.create(user);
    }

    /**
     * 根据用户ID加载用户详情
     * 
     * @param userId 用户ID
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("用户不存在: " + userId);
                });

        log.debug("User found: {} (ID: {}) with {} roles", user.getUsername(), user.getId(), user.getUserRoles().size());
        return UserPrincipal.create(user);
    }

    /**
     * 根据用户ID和站点ID加载用户详情（包含站点级权限）
     * 
     * @param userId 用户ID
     * @param siteId 站点ID
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByIdAndSiteId(Long userId, Long siteId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {} for site: {}", userId, siteId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("用户不存在: " + userId);
                });

        log.debug("User found: {} (ID: {}) for site: {} with {} roles", user.getUsername(), user.getId(), siteId, user.getUserRoles().size());
        return UserPrincipal.create(user, siteId);
    }
}
