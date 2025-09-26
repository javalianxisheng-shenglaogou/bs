package com.multisite.cms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "user_roles", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_role_site", columnNames = {"user_id", "role_id", "site_id"})
    },
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_role_id", columnList = "role_id"),
        @Index(name = "idx_site_id", columnList = "site_id"),
        @Index(name = "idx_user_roles_site", columnList = "user_id, site_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"user", "role", "site", "grantedByUser"})
@ToString(callSuper = true, exclude = {"user", "role", "site", "grantedByUser"})
public class UserRole extends BaseEntity {

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_roles_user"))
    @NotNull(message = "用户不能为空")
    private User user;

    /**
     * 角色
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_roles_role"))
    @NotNull(message = "角色不能为空")
    private Role role;

    /**
     * 站点（站点级角色，为空表示全局角色）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", foreignKey = @ForeignKey(name = "fk_user_roles_site"))
    private Site site;

    /**
     * 授权人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", nullable = false, foreignKey = @ForeignKey(name = "fk_user_roles_granted_by"))
    @NotNull(message = "授权人不能为空")
    private User grantedByUser;

    /**
     * 授权时间
     */
    @Column(name = "granted_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Builder.Default
    private LocalDateTime grantedAt = LocalDateTime.now();

    /**
     * 过期时间（为空表示永不过期）
     */
    @Column(name = "expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expiresAt;

    /**
     * 判断是否为全局角色
     */
    @Transient
    public boolean isGlobalRole() {
        return this.site == null;
    }

    /**
     * 判断是否为站点级角色
     */
    @Transient
    public boolean isSiteRole() {
        return this.site != null;
    }

    /**
     * 判断角色是否已过期
     */
    @Transient
    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * 判断角色是否有效（未过期且未删除）
     */
    @Transient
    public boolean isValid() {
        return !isExpired() && !isDeleted();
    }

    /**
     * 设置过期时间（从现在开始的天数）
     */
    public void setExpiresInDays(int days) {
        if (days > 0) {
            this.expiresAt = LocalDateTime.now().plusDays(days);
        } else {
            this.expiresAt = null;
        }
    }

    /**
     * 设置过期时间（从现在开始的小时数）
     */
    public void setExpiresInHours(int hours) {
        if (hours > 0) {
            this.expiresAt = LocalDateTime.now().plusHours(hours);
        } else {
            this.expiresAt = null;
        }
    }

    /**
     * 延长过期时间
     */
    public void extendExpiration(int days) {
        if (this.expiresAt != null) {
            this.expiresAt = this.expiresAt.plusDays(days);
        } else {
            setExpiresInDays(days);
        }
    }

    /**
     * 取消过期时间（设置为永不过期）
     */
    public void removeExpiration() {
        this.expiresAt = null;
    }

    /**
     * 获取角色描述信息
     */
    @Transient
    public String getRoleDescription() {
        StringBuilder sb = new StringBuilder();
        if (role != null) {
            sb.append(role.getName());
        }
        if (site != null) {
            sb.append(" (").append(site.getName()).append(")");
        } else {
            sb.append(" (全局)");
        }
        return sb.toString();
    }

    /**
     * 预持久化操作
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (this.grantedAt == null) {
            this.grantedAt = LocalDateTime.now();
        }
    }
}
