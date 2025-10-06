package com.cms.module.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容版本DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class ContentVersionDTO {

    /**
     * 版本ID
     */
    private Long id;

    /**
     * 关联的内容ID
     */
    private Long contentId;

    /**
     * 版本号
     */
    private Integer versionNumber;

    /**
     * 标题快照
     */
    private String title;

    /**
     * URL别名快照
     */
    private String slug;

    /**
     * 摘要快照
     */
    private String summary;

    /**
     * 内容快照（富文本）
     */
    private String content;

    /**
     * 封面图快照
     */
    private String coverImage;

    /**
     * 内容类型快照
     */
    private String contentType;

    /**
     * 状态快照
     */
    private String status;

    /**
     * 是否置顶快照
     */
    private Boolean isTop;

    /**
     * 是否推荐快照
     */
    private Boolean isFeatured;

    /**
     * 是否原创快照
     */
    private Boolean isOriginal;

    /**
     * 标签快照
     */
    private String tags;

    /**
     * 元数据快照
     */
    private String metadata;

    /**
     * 修改摘要
     */
    private String changeSummary;

    /**
     * 修改类型
     */
    private String changeType;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建人姓名
     */
    private String createdByName;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否标记为重要版本
     */
    private Boolean isTagged;

    /**
     * 版本标签名称
     */
    private String tagName;

    /**
     * 版本数据大小（字节）
     */
    private Long fileSize;
}

