package com.cms.module.user.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.user.dto.PermissionDTO;
import com.cms.module.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限管理相关接口")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取所有权限
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('permission:view')")
    @Operation(summary = "获取所有权限", description = "获取所有权限列表（不分页）")
    public ApiResponse<List<PermissionDTO>> getAllPermissions() {
        log.info("获取所有权限列表");
        List<PermissionDTO> result = permissionService.getAllPermissions();
        return ApiResponse.success(result);
    }

    /**
     * 分页获取权限列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('permission:view')")
    @Operation(summary = "获取权限列表", description = "分页查询权限列表")
    public ApiResponse<Page<PermissionDTO>> getPermissions(
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("获取权限列表: module={}, page={}, size={}", module, page, size);
        Page<PermissionDTO> result = permissionService.getPermissions(module, page, size);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:view')")
    @Operation(summary = "获取权限详情", description = "根据权限ID获取权限详细信息")
    public ApiResponse<PermissionDTO> getPermissionById(@PathVariable Long id) {
        log.info("获取权限详情: id={}", id);
        PermissionDTO permission = permissionService.getPermissionById(id);
        return ApiResponse.success(permission);
    }

    /**
     * 根据模块获取权限列表
     */
    @GetMapping("/module/{module}")
    @PreAuthorize("hasAuthority('permission:view')")
    @Operation(summary = "根据模块获取权限", description = "根据模块名称获取权限列表")
    public ApiResponse<List<PermissionDTO>> getPermissionsByModule(@PathVariable String module) {
        log.info("根据模块获取权限: module={}", module);
        List<PermissionDTO> result = permissionService.getPermissionsByModule(module);
        return ApiResponse.success(result);
    }

    /**
     * 创建权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    @Operation(summary = "创建权限", description = "创建新权限")
    public ApiResponse<PermissionDTO> createPermission(@Validated @RequestBody PermissionDTO dto) {
        log.info("创建权限: code={}", dto.getCode());
        PermissionDTO permission = permissionService.createPermission(dto);
        return ApiResponse.success(permission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    @Operation(summary = "更新权限", description = "更新权限信息")
    public ApiResponse<PermissionDTO> updatePermission(
            @PathVariable Long id,
            @Validated @RequestBody PermissionDTO dto
    ) {
        log.info("更新权限: id={}", id);
        PermissionDTO permission = permissionService.updatePermission(id, dto);
        return ApiResponse.success(permission);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    @Operation(summary = "删除权限", description = "软删除权限")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        log.info("删除权限: id={}", id);
        permissionService.deletePermission(id);
        return ApiResponse.success();
    }
}

