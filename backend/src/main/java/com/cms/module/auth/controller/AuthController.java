package com.cms.module.auth.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.auth.dto.LoginRequest;
import com.cms.module.auth.dto.LoginResponse;
import com.cms.module.auth.dto.RegisterRequest;
import com.cms.module.auth.dto.UserInfoVO;
import com.cms.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "用户名密码登录，返回JWT token")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "新用户注册，注册成功后自动登录")
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Validated @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "根据token获取当前登录用户的详细信息")
    @GetMapping("/me")
    public ApiResponse<UserInfoVO> getCurrentUser() {
        UserInfoVO userInfo = authService.getCurrentUser();
        return ApiResponse.success(userInfo);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "退出登录（前端清除token即可）")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT是无状态的，登出只需要前端清除token即可
        // 这里可以记录登出日志
        return ApiResponse.success();
    }
}

