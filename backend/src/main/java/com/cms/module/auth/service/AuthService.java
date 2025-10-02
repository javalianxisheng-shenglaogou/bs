package com.cms.module.auth.service;

import com.cms.common.exception.BusinessException;
import com.cms.common.exception.ErrorCode;
import com.cms.module.auth.dto.LoginRequest;
import com.cms.module.auth.dto.LoginResponse;
import com.cms.module.auth.dto.UserInfoVO;
import com.cms.module.user.repository.UserRepository;
import com.cms.security.service.CustomUserDetails;
import com.cms.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(userDetails.getUserId());
        userInfo.setUsername(userDetails.getUsername());
        userInfo.setNickname(userDetails.getNickname());
        userInfo.setEmail(userDetails.getEmail());
        userInfo.setStatus(userDetails.getStatus());
        userInfo.setRoles(userDetails.getRoleCodes());
        userInfo.setPermissions(userDetails.getPermissionCodes());

        return userInfo;
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

