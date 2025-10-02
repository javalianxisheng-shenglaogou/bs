package com.cms.module.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Data
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 权限名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 权限代码
     */
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    /**
     * 权限描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 所属模块
     */
    @Column(nullable = false, length = 50)
    private String module;

    /**
     * 资源标识
     */
    @Column(nullable = false, length = 100)
    private String resource;

    /**
     * 操作类型
     */
    @Column(nullable = false, length = 50)
    private String action;

    /**
     * 是否系统权限
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 排序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 删除标记
     */
    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

