package com.cms.module.auth.service;

import com.cms.common.exception.BusinessException;
import com.cms.common.exception.ErrorCode;
import com.cms.module.auth.dto.LoginRequest;
import com.cms.module.auth.dto.LoginResponse;
import com.cms.module.auth.dto.RegisterRequest;
import com.cms.module.auth.dto.UserInfoVO;
import com.cms.module.user.entity.Role;
import com.cms.module.user.entity.User;
import com.cms.module.user.repository.RoleRepository;
import com.cms.module.user.repository.UserRepository;
import com.cms.security.service.CustomUserDetails;
import com.cms.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        try {
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 获取用户详情
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 生成JWT token
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUserId());

            // 更新用户登录信息
            updateLoginInfo(userDetails.getUserId());

            // 构建响应
            return new LoginResponse(
                    token,
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getNickname(),
                    userDetails.getEmail(),
                    null, // avatarUrl 暂时为null
                    userDetails.getRoleCodes(),
                    userDetails.getPermissionCodes()
            );
        } catch (Exception e) {
            log.error("登录失败: {}", request.getUsername(), e);
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
    }

    /**
     * 获取当前用户信息
     */
    public UserInfoVO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 检查principal类型，防止匿名用户导致ClassCastException
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        // 从数据库获取最新的用户信息(包括头像)
        com.cms.module.user.entity.User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(userDetails.getUserId());
        userInfo.setUsername(userDetails.getUsername());
        userInfo.setNickname(userDetails.getNickname());
        userInfo.setEmail(userDetails.getEmail());
        userInfo.setMobile(user.getMobile());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setStatus(userDetails.getStatus());
        userInfo.setRoles(userDetails.getRoleCodes());
        userInfo.setPermissions(userDetails.getPermissionCodes());

        return userInfo;
    }

    /**
     * 用户注册
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());

        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 检查手机号是否已存在
        if (request.getMobile() != null && !request.getMobile().isEmpty()
                && userRepository.existsByMobile(request.getMobile())) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus("ACTIVE"); // 新注册用户默认激活

        // 分配默认角色
        Role defaultRole = roleRepository.findByCode("USER")
                .orElseGet(() -> roleRepository.findByCode("EDITOR")
                        .orElse(null));

        if (defaultRole != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(defaultRole);
            user.setRoles(roles);
        }

        // 保存用户
        user = userRepository.save(user);
        log.info("用户注册成功: {}", user.getUsername());

        // 自动登录
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getUserId());

            return new LoginResponse(
                    token,
                    userDetails.getUserId(),
                    userDetails.getUsername(),
                    userDetails.getNickname(),
                    userDetails.getEmail(),
                    null,
                    userDetails.getRoleCodes(),
                    userDetails.getPermissionCodes()
            );
        } catch (Exception e) {
            log.error("注册后自动登录失败: {}", request.getUsername(), e);
            throw new BusinessException("注册成功，但自动登录失败，请手动登录");
        }
    }

    /**
     * 更新用户登录信息
     */
    private void updateLoginInfo(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            user.setLoginCount(user.getLoginCount() + 1);
            user.setFailedLoginCount(0);
            userRepository.save(user);
        });
    }
}

