package com.cms.module.user.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 邮箱
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 手机号
     */
    @Column(unique = true, length = 20)
    private String mobile;

    /**
     * 密码哈希
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 昵称
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 性别
     */
    @Column(columnDefinition = "ENUM('MALE', 'FEMALE', 'OTHER')")
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 个人简介
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * 用户状态
     */
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'LOCKED', 'PENDING') DEFAULT 'PENDING'")
    private String status = "PENDING";

    /**
     * 邮箱是否验证
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /**
     * 手机是否验证
     */
    @Column(name = "mobile_verified", nullable = false)
    private Boolean mobileVerified = false;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @Column(name = "login_count", nullable = false)
    private Integer loginCount = 0;

    /**
     * 失败登录次数
     */
    @Column(name = "failed_login_count", nullable = false)
    private Integer failedLoginCount = 0;

    /**
     * 锁定截止时间
     */
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    /**
     * 用户角色关联
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

