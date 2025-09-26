package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import com.multisite.cms.dto.auth.ChangePasswordRequest;
import com.multisite.cms.dto.auth.LoginRequest;
import com.multisite.cms.dto.auth.RegisterRequest;
import com.multisite.cms.security.UserPrincipal;
import com.multisite.cms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过用户名/邮箱和密码进行登录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "登录成功",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", 
                    description = "用户名或密码错误"
            )
    })
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt: {}", request.getUsernameOrEmail());
        
        try {
            Map<String, Object> result = authService.login(
                    request.getUsernameOrEmail(), 
                    request.getPassword()
            );
            
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getUsernameOrEmail(), e);
            return ApiResponse.unauthorized(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "注册成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误或用户已存在"
            )
    })
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration attempt: {}", request.getUsername());
        
        try {
            // 验证密码一致性
            if (!request.isPasswordMatch()) {
                return ApiResponse.badRequest("两次输入的密码不一致");
            }
            
            Map<String, Object> result = authService.register(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getNickname()
            );
            
            return ApiResponse.success("注册成功", result);
        } catch (Exception e) {
            log.error("Registration failed for user: {}", request.getUsername(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "刷新成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", 
                    description = "刷新令牌无效"
            )
    })
    public ApiResponse<Map<String, Object>> refreshToken(
            @Parameter(description = "刷新令牌", required = true)
            @RequestParam String refreshToken) {
        log.debug("Token refresh attempt");
        
        try {
            Map<String, Object> result = authService.refreshToken(refreshToken);
            return ApiResponse.success("令牌刷新成功", result);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ApiResponse.unauthorized(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "密码修改成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误或原密码错误"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", 
                    description = "未授权访问"
            )
    })
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        log.info("Password change attempt for user: {}", currentUser.getId());
        
        try {
            // 验证新密码一致性
            if (!request.isNewPasswordMatch()) {
                return ApiResponse.badRequest("两次输入的新密码不一致");
            }
            
            authService.changePassword(
                    currentUser.getId(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            
            return ApiResponse.success("密码修改成功");
        } catch (Exception e) {
            log.error("Password change failed for user: {}", currentUser.getId(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "通过邮箱重置密码")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "密码重置成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "邮箱不存在"
            )
    })
    public ApiResponse<Void> resetPassword(
            @Parameter(description = "邮箱", required = true)
            @RequestParam String email,
            @Parameter(description = "新密码", required = true)
            @RequestParam String newPassword) {
        log.info("Password reset attempt for email: {}", email);
        
        try {
            authService.resetPassword(email, newPassword);
            return ApiResponse.success("密码重置成功");
        } catch (Exception e) {
            log.error("Password reset failed for email: {}", email, e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 验证令牌
     */
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证JWT令牌的有效性")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "令牌有效"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", 
                    description = "令牌无效"
            )
    })
    public ApiResponse<Map<String, Object>> validateToken(
            @Parameter(description = "JWT令牌", required = true)
            @RequestParam String token) {
        log.debug("Token validation attempt");
        
        try {
            Map<String, Object> result = authService.validateToken(token);
            return ApiResponse.success("令牌有效", result);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return ApiResponse.unauthorized(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出系统")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "登出成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", 
                    description = "未授权访问"
            )
    })
    public ApiResponse<Void> logout(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            log.warn("Logout attempt with null user principal");
            return ApiResponse.success("登出成功");
        }

        log.info("User logout: {}", currentUser.getId());

        try {
            authService.logout(currentUser.getId());
            return ApiResponse.success("登出成功");
        } catch (Exception e) {
            log.error("Logout failed for user: {}", currentUser.getId(), e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", 
                    description = "未授权访问"
            )
    })
    public ApiResponse<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            log.warn("Get current user info attempt with null user principal");
            return ApiResponse.unauthorized("用户未登录");
        }

        log.debug("Get current user info: {}", currentUser.getId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", currentUser.getId());
        userInfo.put("username", currentUser.getUsername());
        userInfo.put("email", currentUser.getEmail());
        userInfo.put("nickname", currentUser.getNickname() != null ? currentUser.getNickname() : "");
        userInfo.put("displayName", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getUsername());
        userInfo.put("avatarUrl", currentUser.getAvatarUrl() != null ? currentUser.getAvatarUrl() : "");
        userInfo.put("roles", currentUser.getRoleCodes() != null ? currentUser.getRoleCodes() : Collections.emptyList());
        userInfo.put("managedSites", currentUser.getManagedSiteCodes() != null ? currentUser.getManagedSiteCodes() : Collections.emptyList());
        userInfo.put("isSuperAdmin", currentUser.isSuperAdmin());

        return ApiResponse.success("获取成功", userInfo);
    }
}
