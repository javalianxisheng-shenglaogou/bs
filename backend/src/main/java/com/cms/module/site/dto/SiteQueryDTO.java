package com.cms.module.site.dto;

import lombok.Data;

/**
 * 站点查询DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class SiteQueryDTO {

    /**
     * 站点名称（模糊查询）
     */
    private String name;

    /**
     * 站点代码（模糊查询）
     */
    private String code;

    /**
     * 站点域名（模糊查询）
     */
    private String domain;

    /**
     * 站点状态
     */
    private String status;

    /**
     * 是否默认站点
     */
    private Boolean isDefault;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sortBy = "createdAt";

    /**
     * 排序方向
     */
    private String sortDir = "desc";
}

