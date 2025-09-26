package com.multisite.cms.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户响应DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户响应")
public class UserResponse {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", example = "管理员")
    private String displayName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    /**
     * 用户状态
     */
    @Schema(description = "用户状态", example = "ACTIVE")
    private UserStatus status;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginAt;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "system")
    private String createdBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "admin")
    private String updatedBy;

    /**
     * 角色列表
     */
    @Schema(description = "角色列表")
    private List<String> roles;

    /**
     * 管理的站点列表
     */
    @Schema(description = "管理的站点列表")
    private List<String> managedSites;

    /**
     * 是否为超级管理员
     */
    @Schema(description = "是否为超级管理员", example = "false")
    private Boolean isSuperAdmin;

    /**
     * 从User实体创建UserResponse
     * 
     * @param user User实体
     * @return UserResponse
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .displayName(user.getDisplayName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    /**
     * 从User实体创建UserResponse（包含角色信息）
     * 
     * @param user User实体
     * @param roles 角色列表
     * @param managedSites 管理的站点列表
     * @param isSuperAdmin 是否为超级管理员
     * @return UserResponse
     */
    public static UserResponse from(User user, List<String> roles, List<String> managedSites, Boolean isSuperAdmin) {
        UserResponse response = from(user);
        response.setRoles(roles);
        response.setManagedSites(managedSites);
        response.setIsSuperAdmin(isSuperAdmin);
        return response;
    }
}
