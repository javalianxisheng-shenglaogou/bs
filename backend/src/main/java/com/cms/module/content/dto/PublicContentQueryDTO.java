package com.cms.module.content.dto;

import lombok.Data;

/**
 * 公开内容查询DTO
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Data
public class PublicContentQueryDTO {
    
    /**
     * 站点ID
     */
    private Long siteId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 页码（从0开始）
     */
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
}

