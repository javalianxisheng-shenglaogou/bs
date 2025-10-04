package com.cms.module.category.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 分类实体
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    /**
     * 站点ID
     */
    @Column(name = "site_id", nullable = false)
    private Long siteId;

    /**
     * 父分类ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 分类名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 分类代码
     */
    @Column(nullable = false, length = 50)
    private String code;

    /**
     * 分类描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 分类图标
     */
    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    /**
     * 分类封面
     */
    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    /**
     * 排序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 层级
     */
    @Column(nullable = false)
    private Integer level = 1;

    /**
     * 路径(用于快速查询子分类)
     */
    @Column(length = 500)
    private String path;

    /**
     * 是否显示
     */
    @Column(name = "is_visible")
    private Boolean isVisible = true;

    /**
     * SEO标题
     */
    @Column(name = "seo_title", length = 200)
    private String seoTitle;

    /**
     * SEO关键词
     */
    @Column(name = "seo_keywords", length = 500)
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;
}

