package com.cms.module.content.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 内容实体
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "contents")
public class Content extends BaseEntity {

    /**
     * 站点ID
     */
    @Column(name = "site_id", nullable = false)
    private Long siteId;

    /**
     * 分类ID
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 标题
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * URL别名
     */
    @Column(nullable = false, length = 200)
    private String slug;

    /**
     * 摘要
     */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /**
     * 内容
     */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 内容类型
     */
    @Column(name = "content_type", length = 20)
    private String contentType = "ARTICLE";

    /**
     * 模板名称
     */
    @Column(length = 100)
    private String template;

    /**
     * 封面图
     */
    @Column(name = "cover_image", length = 500)
    private String coverImage;



    /**
     * 作者ID
     */
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    /**
     * 作者名称
     */
    @Column(name = "author_name", length = 50)
    private String authorName;

    /**
     * 状态
     */
    @Column(length = 20)
    private String status = "DRAFT";

    /**
     * 发布时间
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * 浏览次数
     */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 是否置顶
     */
    @Column(name = "is_top")
    private Boolean isTop = false;

    /**
     * 是否推荐
     */
    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    /**
     * 是否原创
     */
    @Column(name = "is_original")
    private Boolean isOriginal = true;
}

