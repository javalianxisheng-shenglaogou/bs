package com.cms.module.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 权限DTO
 */
@Data
public class PermissionDTO {

    private Long id;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String name;

    /**
     * 权限代码
     */
    @NotBlank(message = "权限代码不能为空")
    private String code;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 所属模块
     */
    @NotBlank(message = "所属模块不能为空")
    private String module;

    /**
     * 资源标识
     */
    @NotBlank(message = "资源标识不能为空")
    private String resource;

    /**
     * 操作类型
     */
    @NotBlank(message = "操作类型不能为空")
    private String action;

    /**
     * 是否系统权限
     */
    private Boolean isSystem = false;

    /**
     * 排序
     */
    private Integer sortOrder = 0;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

