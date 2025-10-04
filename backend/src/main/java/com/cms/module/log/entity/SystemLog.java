package com.cms.module.log.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 系统日志实体
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "system_logs", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_module", columnList = "module"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_level", columnList = "level"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class SystemLog extends BaseEntity {

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @Column(length = 50)
    private String username;

    /**
     * 模块
     */
    @Column(nullable = false, length = 50)
    private String module;

    /**
     * 操作
     */
    @Column(nullable = false, length = 50)
    private String action;

    /**
     * 日志级别
     */
    @Column(nullable = false, length = 20)
    private String level = "INFO";

    /**
     * 描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 请求方法
     */
    @Column(name = "request_method", length = 10)
    private String requestMethod;

    /**
     * 请求URL
     */
    @Column(name = "request_url", length = 500)
    private String requestUrl;

    /**
     * 请求参数
     */
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    /**
     * 响应结果
     */
    @Column(name = "response_result", columnDefinition = "TEXT")
    private String responseResult;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 浏览器
     */
    @Column(length = 200)
    private String browser;

    /**
     * 操作系统
     */
    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    /**
     * 执行时间(毫秒)
     */
    @Column(name = "execution_time")
    private Long executionTime;

    /**
     * 是否成功
     */
    @Column(name = "is_success", nullable = false)
    private Boolean isSuccess = true;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}

