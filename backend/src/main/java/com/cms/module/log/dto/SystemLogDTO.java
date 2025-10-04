package com.cms.module.log.dto;

import lombok.Data;

/**
 * 系统日志DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class SystemLogDTO {

    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String action;
    private String level;
    private String description;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private String responseResult;
    private String ipAddress;
    private String browser;
    private String operatingSystem;
    private Long executionTime;
    private Boolean isSuccess;
    private String errorMessage;
    private String createdAt;  // 改为String类型,方便从日志文件解析
}

