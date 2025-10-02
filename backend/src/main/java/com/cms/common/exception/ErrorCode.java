package com.cms.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误码 1xxx
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统错误"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    CONFLICT(409, "资源冲突"),
    
    // 用户相关错误码 2xxx
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    USER_DISABLED(2003, "用户已禁用"),
    USER_LOCKED(2004, "用户已锁定"),
    PASSWORD_ERROR(2005, "密码错误"),
    USERNAME_ALREADY_EXISTS(2006, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(2007, "邮箱已存在"),
    MOBILE_ALREADY_EXISTS(2008, "手机号已存在"),
    ROLE_NOT_FOUND(2009, "角色不存在"),

    // 认证相关错误码 3xxx
    TOKEN_INVALID(3001, "令牌无效"),
    TOKEN_EXPIRED(3002, "令牌已过期"),
    LOGIN_FAILED(3003, "登录失败"),
    
    // 站点相关错误码 4xxx
    SITE_NOT_FOUND(4001, "站点不存在"),
    SITE_CODE_EXISTS(4002, "站点代码已存在"),
    
    // 内容相关错误码 5xxx
    CONTENT_NOT_FOUND(5001, "内容不存在"),
    CONTENT_PUBLISHED(5002, "内容已发布，无法修改"),
    
    // 工作流相关错误码 6xxx
    WORKFLOW_NOT_FOUND(6001, "工作流不存在"),
    WORKFLOW_INSTANCE_NOT_FOUND(6002, "工作流实例不存在"),
    TASK_NOT_FOUND(6003, "任务不存在"),
    TASK_ALREADY_PROCESSED(6004, "任务已处理");

    private final Integer code;
    private final String message;
}

