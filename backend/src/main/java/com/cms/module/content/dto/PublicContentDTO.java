package com.cms.module.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公开内容DTO（用于列表展示）
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Data
public class PublicContentDTO {
    
    /**
     * 内容ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 摘要
     */
    private String summary;
    
    /**
     * 封面图
     */
    private String coverImage;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 作者名称
     */
    private String authorName;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;
    
    /**
     * 是否置顶
     */
    private Boolean isTop;
    
    /**
     * 是否推荐
     */
    private Boolean isFeatured;
}

