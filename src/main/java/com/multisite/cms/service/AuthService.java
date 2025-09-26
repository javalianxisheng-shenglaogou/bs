package com.multisite.cms.service;

import com.multisite.cms.entity.User;

import java.util.Map;

/**
 * 认证服务接口
 *
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param usernameOrEmail 用户名或邮箱
     * @param password 密码
     * @return 登录结果（包含令牌信息）
     */
    Map<String, Object> login(String usernameOrEmail, String password);

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的令牌信息
     */
    Map<String, Object> refreshToken(String refreshToken);

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param nickname 昵称
     * @return 注册结果
     */
    Map<String, Object> register(String username, String email, String password, String nickname);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param email 邮箱
     * @param newPassword 新密码
     */
    void resetPassword(String email, String newPassword);

    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @return 验证结果
     */
    Map<String, Object> validateToken(String token);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     */
    void logout(Long userId);
}