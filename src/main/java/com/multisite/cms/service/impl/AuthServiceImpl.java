package com.multisite.cms.service.impl;

import com.multisite.cms.entity.User;
import com.multisite.cms.repository.UserRepository;
import com.multisite.cms.security.JwtTokenProvider;
import com.multisite.cms.security.UserPrincipal;
import com.multisite.cms.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录
     *
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     * @return 登录结果（包含令牌信息）
     */
    @Override
    @Transactional
    public Map<String, Object> login(String usernameOrEmail, String password) {
        log.info("User attempting to login: {}", usernameOrEmail);
        
        try {
            // 进行身份认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // 生成JWT令牌
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
            
            // 更新最后登录时间
            updateLastLoginTime(userPrincipal.getId());
            
            // 构建响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenProvider.getTokenRemainingTime(accessToken));
            result.put("user", buildUserInfo(userPrincipal));
            
            log.info("User login successful: {} (ID: {})", userPrincipal.getUsername(), userPrincipal.getId());
            return result;
            
        } catch (AuthenticationException e) {
            log.warn("User login failed: {} - {}", usernameOrEmail, e.getMessage());
            throw new RuntimeException("用户名或密码错误");
        }
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> refreshToken(String refreshToken) {
        log.debug("Attempting to refresh token");
        
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效");
        }
        
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("令牌类型错误");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted() && u.isActive())
                .orElseThrow(() -> new RuntimeException("用户不存在或已被禁用"));
        
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        
        // 创建认证对象用于生成新令牌
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
        
        // 生成新的访问令牌
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", refreshToken); // 刷新令牌保持不变
        result.put("tokenType", "Bearer");
        result.put("expiresIn", jwtTokenProvider.getTokenRemainingTime(newAccessToken));
        result.put("user", buildUserInfo(userPrincipal));
        
        log.debug("Token refresh successful for user: {} (ID: {})", user.getUsername(), user.getId());
        return result;
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param nickname 昵称
     * @return 注册结果
     */
    @Override
    @Transactional
    public Map<String, Object> register(String username, String email, String password, String nickname) {
        log.info("User attempting to register: {} ({})", username, email);
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsernameAndDeletedFalse(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmailAndDeletedFalse(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .nickname(nickname)
                .createdBy("system")
                .build();
        
        user = userRepository.save(user);
        
        // 自动登录
        Map<String, Object> loginResult = login(username, password);
        
        log.info("User registration successful: {} (ID: {})", username, user.getId());
        return loginResult;
    }

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("User attempting to change password: {}", userId);
        
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedBy(user.getUsername());
        userRepository.save(user);
        
        log.info("Password change successful for user: {} (ID: {})", user.getUsername(), userId);
    }

    /**
     * 重置密码
     *
     * @param email 邮箱
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        log.info("User attempting to reset password: {}", email);
        
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("邮箱不存在"));
        
        // 更新密码
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedBy("system");
        userRepository.save(user);
        
        log.info("Password reset successful for user: {} (ID: {})", user.getUsername(), user.getId());
    }

    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @return 用户信息
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("令牌无效");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted() && u.isActive())
                .orElseThrow(() -> new RuntimeException("用户不存在或已被禁用"));
        
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("user", buildUserInfo(userPrincipal));
        result.put("expiresIn", jwtTokenProvider.getTokenRemainingTime(token));
        
        return result;
    }

    /**
     * 用户登出
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void logout(Long userId) {
        log.info("User logout: {}", userId);
        // 这里可以实现令牌黑名单机制
        // 目前JWT是无状态的，客户端删除令牌即可
    }

    /**
     * 更新最后登录时间
     * 
     * @param userId 用户ID
     */
    private void updateLastLoginTime(Long userId) {
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
    }

    /**
     * 构建用户信息
     * 
     * @param userPrincipal 用户主体
     * @return 用户信息Map
     */
    private Map<String, Object> buildUserInfo(UserPrincipal userPrincipal) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userPrincipal.getId());
        userInfo.put("username", userPrincipal.getUsername());
        userInfo.put("email", userPrincipal.getEmail());
        userInfo.put("nickname", userPrincipal.getNickname());
        userInfo.put("displayName", userPrincipal.getDisplayName());
        userInfo.put("avatarUrl", userPrincipal.getAvatarUrl());
        userInfo.put("roles", userPrincipal.getRoleCodes());
        userInfo.put("managedSites", userPrincipal.getManagedSiteCodes());
        userInfo.put("isSuperAdmin", userPrincipal.isSuperAdmin());
        return userInfo;
    }
}
