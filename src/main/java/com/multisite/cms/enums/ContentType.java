package com.multisite.cms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容类型枚举
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ContentType {
    
    /**
     * 文章类型 - 普通文章内容
     */
    ARTICLE("文章", "普通文章内容，如新闻、博客等"),
    
    /**
     * 页面类型 - 静态页面内容
     */
    PAGE("页面", "静态页面内容，如关于我们、联系我们等"),
    
    /**
     * 媒体类型 - 媒体文件内容
     */
    MEDIA("媒体", "媒体文件内容，如图片、视频、音频等");

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据名称获取内容类型
     * 
     * @param name 类型名称
     * @return 内容类型枚举
     */
    public static ContentType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ARTICLE; // 默认类型
        }
        
        for (ContentType type : values()) {
            if (type.name().equalsIgnoreCase(name.trim()) || 
                type.getName().equals(name.trim())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的内容类型: " + name);
    }

    /**
     * 判断是否为文章类型
     * 
     * @return true 如果是文章类型
     */
    public boolean isArticle() {
        return this == ARTICLE;
    }

    /**
     * 判断是否为页面类型
     * 
     * @return true 如果是页面类型
     */
    public boolean isPage() {
        return this == PAGE;
    }

    /**
     * 判断是否为媒体类型
     * 
     * @return true 如果是媒体类型
     */
    public boolean isMedia() {
        return this == MEDIA;
    }

    /**
     * 获取类型的CSS样式类
     * 
     * @return CSS样式类名
     */
    public String getCssClass() {
        switch (this) {
            case ARTICLE:
                return "content-article";
            case PAGE:
                return "content-page";
            case MEDIA:
                return "content-media";
            default:
                return "content-unknown";
        }
    }

    /**
     * 获取类型的颜色代码
     * 
     * @return 颜色代码
     */
    public String getColorCode() {
        switch (this) {
            case ARTICLE:
                return "#1890ff";  // 蓝色
            case PAGE:
                return "#52c41a";     // 绿色
            case MEDIA:
                return "#fa8c16";    // 橙色
            default:
                return "#000000";
        }
    }

    /**
     * 获取类型图标
     * 
     * @return 图标名称
     */
    public String getIcon() {
        switch (this) {
            case ARTICLE:
                return "file-text";
            case PAGE:
                return "file";
            case MEDIA:
                return "picture";
            default:
                return "file";
        }
    }

    /**
     * 获取默认模板
     * 
     * @return 默认模板名称
     */
    public String getDefaultTemplate() {
        switch (this) {
            case ARTICLE:
                return "article";
            case PAGE:
                return "page";
            case MEDIA:
                return "media";
            default:
                return "default";
        }
    }

    /**
     * 判断是否支持评论
     * 
     * @return true 如果支持评论
     */
    public boolean supportsComments() {
        return this == ARTICLE; // 只有文章支持评论
    }

    /**
     * 判断是否支持分类
     * 
     * @return true 如果支持分类
     */
    public boolean supportsCategory() {
        return this == ARTICLE || this == PAGE; // 文章和页面支持分类
    }

    /**
     * 判断是否支持标签
     * 
     * @return true 如果支持标签
     */
    public boolean supportsTags() {
        return this == ARTICLE; // 只有文章支持标签
    }

    @Override
    public String toString() {
        return this.name;
    }
}
