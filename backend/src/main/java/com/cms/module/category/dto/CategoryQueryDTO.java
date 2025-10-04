package com.cms.module.category.dto;

import lombok.Data;

/**
 * 分类查询DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class CategoryQueryDTO {

    /**
     * 站点ID
     */
    private Long siteId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称(模糊查询)
     */
    private String name;

    /**
     * 分类编码
     */
    private String code;

    /**
     * 是否可见
     */
    private Boolean isVisible;

    /**
     * 层级
     */
    private Integer level;
}

