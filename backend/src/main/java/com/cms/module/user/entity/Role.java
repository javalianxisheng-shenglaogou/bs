package com.cms.module.user.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 角色代码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * 角色描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 角色级别
     */
    @Column(nullable = false)
    private Integer level = 0;

    /**
     * 是否系统角色
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 是否默认角色
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    /**
     * 角色权限关联
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}

