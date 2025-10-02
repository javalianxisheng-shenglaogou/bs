package com.cms.module.user.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.user.dto.UserCreateRequest;
import com.cms.module.user.dto.UserDTO;
import com.cms.module.user.dto.UserUpdateRequest;
import com.cms.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:view')")
    @Operation(summary = "获取用户列表", description = "分页查询用户列表，支持关键词搜索和状态筛选")
    public ApiResponse<Page<UserDTO>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder
    ) {
        log.info("获取用户列表: keyword={}, status={}, page={}, size={}", keyword, status, page, size);
        Page<UserDTO> result = userService.getUserList(keyword, status, page, size, sortBy, sortOrder);
        return ApiResponse.success(result);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        log.info("获取用户详情: id={}", id);
        UserDTO user = userService.getUserById(id);
        return ApiResponse.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    @Operation(summary = "创建用户", description = "创建新用户")
    public ApiResponse<UserDTO> createUser(@Validated @RequestBody UserCreateRequest request) {
        log.info("创建用户: username={}", request.getUsername());
        UserDTO user = userService.createUser(request);
        return ApiResponse.success(user);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public ApiResponse<UserDTO> updateUser(
            @PathVariable Long id,
            @Validated @RequestBody UserUpdateRequest request
    ) {
        log.info("更新用户: id={}", id);
        UserDTO user = userService.updateUser(id, request);
        return ApiResponse.success(user);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    @Operation(summary = "删除用户", description = "软删除用户")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: id={}", id);
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    /**
     * 更新用户头像
     */
    @PutMapping("/{id}/avatar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新用户头像", description = "更新用户头像URL")
    public ApiResponse<UserDTO> updateAvatar(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> request
    ) {
        log.info("更新用户头像: id={}", id);
        String avatarUrl = request.get("avatarUrl");
        UserDTO user = userService.updateAvatar(id, avatarUrl);
        return ApiResponse.success(user);
    }
}

