package com.multisite.cms.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_code", columnList = "code"),
    @Index(name = "idx_is_system", columnList = "is_system")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"userRoles"})
@ToString(callSuper = true, exclude = {"userRoles"})
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50")
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    /**
     * 角色代码
     */
    @NotBlank(message = "角色代码不能为空")
    @Size(max = 50, message = "角色代码长度不能超过50")
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    /**
     * 角色描述
     */
    @Size(max = 255, message = "角色描述长度不能超过255")
    @Column(name = "description")
    private String description;

    /**
     * 是否系统角色
     */
    @Column(name = "is_system", nullable = false)
    @Builder.Default
    private Boolean isSystem = Boolean.FALSE;

    /**
     * 用户角色关联
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * 判断是否为系统角色
     */
    @Transient
    public boolean isSystemRole() {
        return Boolean.TRUE.equals(this.isSystem);
    }

    /**
     * 判断是否为自定义角色
     */
    @Transient
    public boolean isCustomRole() {
        return !isSystemRole();
    }

    /**
     * 添加用户角色关联
     */
    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
        userRole.setRole(this);
    }

    /**
     * 移除用户角色关联
     */
    public void removeUserRole(UserRole userRole) {
        this.userRoles.remove(userRole);
        userRole.setRole(null);
    }

    /**
     * 清空用户角色关联
     */
    public void clearUserRoles() {
        this.userRoles.forEach(userRole -> userRole.setRole(null));
        this.userRoles.clear();
    }

    /**
     * 获取角色的用户数量
     */
    @Transient
    public int getUserCount() {
        return userRoles != null ? userRoles.size() : 0;
    }
}
