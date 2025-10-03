package com.cms.module.content.dto;

import lombok.Data;

/**
 * 内容查询DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class ContentQueryDTO {

    private Long siteId;

    private Long categoryId;

    private String title;

    private String contentType;

    private String status;

    private Long authorId;

    private Boolean isTop;

    private Boolean isFeatured;

    private Integer page = 1;

    private Integer size = 10;

    private String sortBy = "createdAt";

    private String sortDir = "desc";
}

