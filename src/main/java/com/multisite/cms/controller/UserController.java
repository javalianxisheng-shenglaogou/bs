package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import com.multisite.cms.common.PageResult;
import com.multisite.cms.dto.user.UserCreateRequest;
import com.multisite.cms.dto.user.UserResponse;
import com.multisite.cms.dto.user.UserUpdateRequest;
import com.multisite.cms.dto.user.ProfileUpdateRequest;
import com.multisite.cms.dto.user.ChangePasswordRequest;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.UserStatus;
import com.multisite.cms.security.UserPrincipal;
import com.multisite.cms.service.UserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping
    @Operation(summary = "分页查询用户", description = "分页查询用户列表，支持状态过滤和关键字搜索")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "查询成功",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<PageResult<UserResponse>> getUsers(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,
            @Parameter(description = "用户状态")
            @RequestParam(required = false) UserStatus status,
            @Parameter(description = "搜索关键字")
            @RequestParam(required = false) String keyword) {
        
        log.debug("Query users: page={}, size={}, sort={}, direction={}, status={}, keyword={}", 
                page, size, sort, direction, status, keyword);
        
        try {
            PageResult<User> userPage = userService.getUsers(page, size, sort, direction, status, keyword);
            
            // 转换为响应DTO
            List<UserResponse> userResponses = userPage.getContent().stream()
                    .map(UserResponse::from)
                    .collect(Collectors.toList());
            
            PageResult<UserResponse> result = PageResult.of(
                    userResponses, 
                    userPage.getPage(), 
                    userPage.getSize(), 
                    userPage.getTotalElements()
            );
            
            return ApiResponse.success("查询成功", result);
        } catch (Exception e) {
            log.error("Query users failed", e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{userId}")
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取用户详细信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN') or #userId == authentication.principal.id")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "用户不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        
        log.debug("Get user by ID: {}", userId);
        
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            UserResponse response = UserResponse.from(user);
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            log.error("Get user failed: {}", userId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "创建成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误或用户已存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Create user: {}", request.getUsername());
        
        try {
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash(request.getPassword()) // 将在service中加密
                    .nickname(request.getNickname())
                    .phone(request.getPhone())
                    .avatarUrl(request.getAvatarUrl())
                    .status(request.getStatus())
                    .build();
            
            User createdUser = userService.createUser(user, currentUser.getUsername());
            UserResponse response = UserResponse.from(createdUser);
            
            return ApiResponse.success("用户创建成功", response);
        } catch (Exception e) {
            log.error("Create user failed: {}", request.getUsername(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN') or #userId == authentication.principal.id")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "更新成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "请求参数错误"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "用户不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<UserResponse> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Update user: {}", userId);
        
        try {
            User updateUser = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .nickname(request.getNickname())
                    .phone(request.getPhone())
                    .avatarUrl(request.getAvatarUrl())
                    .status(request.getStatus())
                    .passwordHash(request.getPassword()) // 如果提供了新密码
                    .build();
            
            User updatedUser = userService.updateUser(userId, updateUser, currentUser.getUsername());
            UserResponse response = UserResponse.from(updatedUser);
            
            return ApiResponse.success("用户更新成功", response);
        } catch (Exception e) {
            log.error("Update user failed: {}", userId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "删除用户（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "删除成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "用户不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Delete user: {}", userId);
        
        try {
            userService.deleteUser(userId, currentUser.getUsername());
            return ApiResponse.success("用户删除成功");
        } catch (Exception e) {
            log.error("Delete user failed: {}", userId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户", description = "批量删除用户（软删除）")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "删除成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> deleteUsers(
            @Parameter(description = "用户ID列表", required = true)
            @RequestBody List<Long> userIds,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Batch delete users: {}", userIds);
        
        try {
            userService.deleteUsers(userIds, currentUser.getUsername());
            return ApiResponse.success("批量删除成功");
        } catch (Exception e) {
            log.error("Batch delete users failed: {}", userIds, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PatchMapping("/{userId}/status")
    @Operation(summary = "更新用户状态", description = "更新用户状态")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "更新成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404", 
                    description = "用户不存在"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "用户状态", required = true)
            @RequestParam UserStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        log.info("Update user status: {} -> {}", userId, status);
        
        try {
            userService.updateUserStatus(userId, status, currentUser.getUsername());
            return ApiResponse.success("状态更新成功");
        } catch (Exception e) {
            log.error("Update user status failed: {}", userId, e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.notFound(e.getMessage());
            }
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 批量更新用户状态
     */
    @PatchMapping("/batch/status")
    @Operation(summary = "批量更新用户状态", description = "批量更新用户状态")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "更新成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<Void> updateUsersStatus(
            @Parameter(description = "用户ID列表", required = true)
            @RequestParam List<Long> userIds,
            @Parameter(description = "用户状态", required = true)
            @RequestParam UserStatus status) {
        
        log.info("Batch update user status: {} -> {}", userIds, status);
        
        try {
            userService.updateUsersStatus(userIds, status);
            return ApiResponse.success("批量状态更新成功");
        } catch (Exception e) {
            log.error("Batch update user status failed: {}", userIds, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否可用", description = "检查用户名是否已被使用")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "检查完成"
            )
    })
    public ApiResponse<Boolean> checkUsernameAvailable(
            @Parameter(description = "用户名", required = true)
            @RequestParam String username,
            @Parameter(description = "排除的用户ID")
            @RequestParam(required = false) Long excludeUserId) {
        
        log.debug("Check username available: {}", username);
        
        try {
            boolean available = userService.isUsernameAvailable(username, excludeUserId);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            log.error("Check username failed: {}", username, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    @Operation(summary = "检查邮箱是否可用", description = "检查邮箱是否已被使用")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "检查完成"
            )
    })
    public ApiResponse<Boolean> checkEmailAvailable(
            @Parameter(description = "邮箱", required = true)
            @RequestParam String email,
            @Parameter(description = "排除的用户ID")
            @RequestParam(required = false) Long excludeUserId) {
        
        log.debug("Check email available: {}", email);
        
        try {
            boolean available = userService.isEmailAvailable(email, excludeUserId);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            log.error("Check email failed: {}", email, e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计信息", description = "获取用户数量统计信息")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "获取成功"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", 
                    description = "权限不足"
            )
    })
    public ApiResponse<UserService.UserStats> getUserStats() {
        log.debug("Get user stats");
        
        try {
            UserService.UserStats stats = userService.getUserStats();
            return ApiResponse.success("获取成功", stats);
        } catch (Exception e) {
            log.error("Get user stats failed", e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户资料", description = "获取当前登录用户的详细资料")
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
    public ApiResponse<UserResponse> getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        log.debug("Get current user profile: {}", currentUser.getId());

        try {
            User user = userService.getUserById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            UserResponse response = UserResponse.from(user);
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            log.error("Get current user profile failed: {}", currentUser.getId(), e);
            return ApiResponse.internalError(e.getMessage());
        }
    }

    /**
     * 更新当前用户资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人资料")
    public ApiResponse<UserResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Update profile for user: {}", currentUser.getId());

        try {
            User user = userService.getUserById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 更新用户信息
            if (StringUtils.hasText(request.getEmail())) {
                user.setEmail(request.getEmail());
            }
            if (StringUtils.hasText(request.getNickname())) {
                user.setNickname(request.getNickname());
            }
            if (StringUtils.hasText(request.getPhone())) {
                user.setPhone(request.getPhone());
            }
            if (StringUtils.hasText(request.getAvatarUrl())) {
                user.setAvatarUrl(request.getAvatarUrl());
            }

            User updatedUser = userService.updateUser(user.getId(), user, currentUser.getUsername());
            UserResponse response = UserResponse.from(updatedUser);

            return ApiResponse.success("个人资料更新成功", response);
        } catch (Exception e) {
            log.error("Update profile failed for user: {}", currentUser.getId(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Change password for user: {}", currentUser.getId());

        try {
            userService.changePassword(currentUser.getId(), request.getCurrentPassword(), request.getNewPassword());
            return ApiResponse.success("密码修改成功");
        } catch (Exception e) {
            log.error("Change password failed for user: {}", currentUser.getId(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }
}
