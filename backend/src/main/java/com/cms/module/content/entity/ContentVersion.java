package com.cms.module.content.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 内容版本实体
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "content_versions", indexes = {
        @Index(name = "idx_content_id", columnList = "content_id"),
        @Index(name = "idx_version_number", columnList = "content_id,version_number"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_change_type", columnList = "change_type")
})
public class ContentVersion {

    /**
     * 版本ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的内容ID
     */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    /**
     * 版本号（从1开始递增）
     */
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    /**
     * 标题快照
     */
    @Column(nullable = false, length = 500)
    private String title;

    /**
     * URL别名快照
     */
    @Column(length = 200)
    private String slug;

    /**
     * 摘要快照
     */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /**
     * 内容快照（富文本）
     */
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 封面图快照
     */
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /**
     * 内容类型快照
     */
    @Column(name = "content_type", length = 50)
    private String contentType;

    /**
     * 状态快照
     */
    @Column(length = 50)
    private String status;

    /**
     * 是否置顶快照
     */
    @Column(name = "is_top")
    private Boolean isTop = false;

    /**
     * 是否推荐快照
     */
    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    /**
     * 是否原创快照
     */
    @Column(name = "is_original")
    private Boolean isOriginal = false;

    /**
     * 标签快照（JSON数组）
     */
    @Column(columnDefinition = "JSON")
    private String tags;

    /**
     * 元数据快照（JSON对象）
     */
    @Column(columnDefinition = "JSON")
    private String metadata;

    /**
     * 修改摘要（用户填写）
     */
    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    /**
     * 修改类型（CREATE/UPDATE/PUBLISH/UNPUBLISH/RESTORE）
     */
    @Column(name = "change_type", nullable = false, length = 50)
    private String changeType;

    /**
     * 创建人ID
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    /**
     * 创建人姓名
     */
    @Column(name = "created_by_name", length = 100)
    private String createdByName;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 是否标记为重要版本
     */
    @Column(name = "is_tagged")
    private Boolean isTagged = false;

    /**
     * 版本标签名称（如"上线版本"、"备份版本"）
     */
    @Column(name = "tag_name", length = 100)
    private String tagName;

    /**
     * 版本数据大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 在保存前设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

