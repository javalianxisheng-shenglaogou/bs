package com.cms.module.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色DTO
 */
@Data
public class RoleDTO {

    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 角色代码
     */
    @NotBlank(message = "角色代码不能为空")
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色级别
     */
    private Integer level = 0;

    /**
     * 是否系统角色
     */
    private Boolean isSystem = false;

    /**
     * 是否默认角色
     */
    private Boolean isDefault = false;

    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;

    /**
     * 权限列表
     */
    private List<PermissionDTO> permissions;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

