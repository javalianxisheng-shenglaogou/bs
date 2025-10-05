package com.cms.module.user.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.user.dto.RoleDTO;
import com.cms.module.user.service.RoleService;
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
 * 角色管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('role:view')")
    @Operation(summary = "获取所有角色", description = "获取所有角色列表（不分页）")
    public ApiResponse<List<RoleDTO>> getAllRoles() {
        log.info("获取所有角色列表");
        List<RoleDTO> result = roleService.getAllRoles();
        return ApiResponse.success(result);
    }

    /**
     * 分页获取角色列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    @Operation(summary = "获取角色列表", description = "分页查询角色列表")
    public ApiResponse<Page<RoleDTO>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("获取角色列表: page={}, size={}", page, size);
        Page<RoleDTO> result = roleService.getRoles(page, size);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详细信息")
    public ApiResponse<RoleDTO> getRoleById(@PathVariable Long id) {
        log.info("获取角色详情: id={}", id);
        RoleDTO role = roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    @Operation(summary = "创建角色", description = "创建新角色")
    public ApiResponse<RoleDTO> createRole(@Validated @RequestBody RoleDTO dto) {
        log.info("创建角色: code={}", dto.getCode());
        RoleDTO role = roleService.createRole(dto);
        return ApiResponse.success(role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public ApiResponse<RoleDTO> updateRole(
            @PathVariable Long id,
            @Validated @RequestBody RoleDTO dto
    ) {
        log.info("更新角色: id={}", id);
        RoleDTO role = roleService.updateRole(id, dto);
        return ApiResponse.success(role);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    @Operation(summary = "删除角色", description = "软删除角色")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        log.info("删除角色: id={}", id);
        roleService.deleteRole(id);
        return ApiResponse.success();
    }

    /**
     * 为角色分配权限
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    @Operation(summary = "分配权限", description = "为角色分配权限")
    public ApiResponse<RoleDTO> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds
    ) {
        log.info("为角色分配权限: roleId={}, permissionIds={}", id, permissionIds);
        RoleDTO role = roleService.assignPermissions(id, permissionIds);
        return ApiResponse.success(role);
    }
}

