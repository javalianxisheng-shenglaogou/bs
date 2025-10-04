package com.cms.module.log.dto;

import lombok.Data;

/**
 * 系统日志查询DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class SystemLogQueryDTO {

    private Long userId;
    private String username;
    private String module;
    private String action;
    private String level;
    private String ipAddress;
    private Boolean isSuccess;
    private String startTime;
    private String endTime;
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}

