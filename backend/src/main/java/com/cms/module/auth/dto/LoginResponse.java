package com.cms.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT token
     */
    private String token;

    /**
     * token类型
     */
    private String tokenType = "Bearer";

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 角色代码列表
     */
    private Set<String> roles;

    /**
     * 权限代码列表
     */
    private Set<String> permissions;

    public LoginResponse(String token, Long userId, String username, String nickname, 
                        String email, String avatarUrl, Set<String> roles, Set<String> permissions) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.permissions = permissions;
    }
}

