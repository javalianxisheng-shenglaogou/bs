package com.cms.module.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公开内容详情DTO
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Data
public class PublicContentDetailDTO {
    
    /**
     * 内容ID
     */
    private Long id;
    
    /**
     * 站点ID
     */
    private Long siteId;
    
    /**
     * 站点名称
     */
    private String siteName;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 完整内容
     */
    private String content;
    
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
    
    /**
     * 是否原创
     */
    private Boolean isOriginal;
}

