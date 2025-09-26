package com.multisite.cms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.multisite.cms.enums.UserStatus;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_status_created", columnList = "status, created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"passwordHash", "userRoles"})
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    /**
     * 密码哈希
     */
    @JsonIgnore
    @NotBlank(message = "密码不能为空")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50")
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号长度不能超过20")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 用户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginAt;

    /**
     * 用户角色关联
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * 获取显示名称（优先使用昵称，否则使用用户名）
     */
    @Transient
    public String getDisplayName() {
        return nickname != null && !nickname.trim().isEmpty() ? nickname : username;
    }

    /**
     * 判断用户是否激活
     */
    @Transient
    public boolean isActive() {
        return UserStatus.ACTIVE.equals(this.status) && !isDeleted();
    }

    /**
     * 判断用户是否被锁定
     */
    @Transient
    public boolean isLocked() {
        return UserStatus.LOCKED.equals(this.status);
    }

    /**
     * 判断用户是否未激活
     */
    @Transient
    public boolean isInactive() {
        return UserStatus.INACTIVE.equals(this.status);
    }

    /**
     * 激活用户
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 停用用户
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * 锁定用户
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    /**
     * 解锁用户
     */
    public void unlock() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 添加用户角色
     */
    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
        userRole.setUser(this);
    }

    /**
     * 移除用户角色
     */
    public void removeUserRole(UserRole userRole) {
        this.userRoles.remove(userRole);
        userRole.setUser(null);
    }

    /**
     * 清空用户角色
     */
    public void clearUserRoles() {
        this.userRoles.forEach(userRole -> userRole.setUser(null));
        this.userRoles.clear();
    }
}
