package com.multisite.cms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 内容实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "contents", indexes = {
    @Index(name = "idx_site_id", columnList = "site_id"),
    @Index(name = "idx_slug", columnList = "slug"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_content_type", columnList = "content_type"),
    @Index(name = "idx_author_id", columnList = "author_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_publish_at", columnList = "publish_at"),
    @Index(name = "idx_content_site_status_publish", columnList = "site_id, status, publish_at"),
    @Index(name = "idx_content_category_status", columnList = "category_id, status, created_at"),
    @Index(name = "idx_content_author_created", columnList = "author_id, created_at"),
    @Index(name = "idx_content_type_status", columnList = "content_type, status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"site", "author", "editor", "category"})
@ToString(callSuper = true, exclude = {"site", "author", "editor", "category"})
public class Content extends BaseEntity {

    /**
     * 所属站点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contents_site"))
    private Site site;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题长度不能超过255")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * URL别名
     */
    @Size(max = 255, message = "URL别名长度不能超过255")
    @Column(name = "slug")
    private String slug;

    /**
     * 摘要
     */
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    /**
     * 内容
     */
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    /**
     * 内容类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    @Builder.Default
    private ContentType contentType = ContentType.ARTICLE;

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT;

    /**
     * 发布时间
     */
    @Column(name = "publish_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishAt;

    /**
     * 特色图片
     */
    @Size(max = 500, message = "特色图片URL长度不能超过500")
    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    /**
     * SEO标题
     */
    @Size(max = 255, message = "SEO标题长度不能超过255")
    @Column(name = "seo_title")
    private String seoTitle;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500")
    @Column(name = "seo_description", length = 500)
    private String seoDescription;

    /**
     * SEO关键词
     */
    @Size(max = 500, message = "SEO关键词长度不能超过500")
    @Column(name = "seo_keywords", length = 500)
    private String seoKeywords;

    /**
     * 浏览次数
     */
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    /**
     * 点赞次数
     */
    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    /**
     * 评论次数
     */
    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    /**
     * 作者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_contents_author"))
    private User author;

    /**
     * 最后编辑者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", foreignKey = @ForeignKey(name = "fk_contents_editor"))
    private User editor;

    /**
     * 分类
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_contents_category"))
    private ContentCategory category;

    /**
     * 排序
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 是否置顶
     */
    @Column(name = "is_top", nullable = false)
    @Builder.Default
    private Boolean isTop = Boolean.FALSE;

    /**
     * 是否允许评论
     */
    @Column(name = "allow_comment", nullable = false)
    @Builder.Default
    private Boolean allowComment = Boolean.TRUE;

    /**
     * 模板名称
     */
    @Size(max = 100, message = "模板名称长度不能超过100")
    @Column(name = "template", length = 100)
    private String template;

    // 注意：版本控制、内容引用、多语言翻译等功能将在后续版本中实现

    /**
     * 判断是否已发布
     */
    @Transient
    public boolean isPublished() {
        return ContentStatus.PUBLISHED.equals(this.status) &&
               this.publishAt != null &&
               !this.publishAt.isAfter(LocalDateTime.now()) &&
               !isDeleted();
    }

    /**
     * 判断是否为草稿
     */
    @Transient
    public boolean isDraft() {
        return ContentStatus.DRAFT.equals(this.status);
    }

    /**
     * 判断是否待审核
     */
    @Transient
    public boolean isPending() {
        return ContentStatus.PENDING.equals(this.status);
    }

    /**
     * 判断是否已归档
     */
    @Transient
    public boolean isArchived() {
        return ContentStatus.ARCHIVED.equals(this.status);
    }

    /**
     * 判断是否置顶
     */
    @Transient
    public boolean isTopContent() {
        return Boolean.TRUE.equals(this.isTop);
    }

    /**
     * 判断是否允许评论
     */
    @Transient
    public boolean isCommentAllowed() {
        return Boolean.TRUE.equals(this.allowComment);
    }

    /**
     * 获取显示标题（SEO标题优先）
     */
    @Transient
    public String getDisplayTitle() {
        return seoTitle != null && !seoTitle.trim().isEmpty() ? seoTitle : title;
    }

    /**
     * 获取显示描述（SEO描述优先）
     */
    @Transient
    public String getDisplayDescription() {
        if (seoDescription != null && !seoDescription.trim().isEmpty()) {
            return seoDescription;
        }
        if (summary != null && !summary.trim().isEmpty()) {
            return summary;
        }
        // 从内容中提取前200个字符作为描述
        if (content != null && !content.trim().isEmpty()) {
            String plainText = content.replaceAll("<[^>]*>", ""); // 简单去除HTML标签
            return plainText.length() > 200 ? plainText.substring(0, 200) + "..." : plainText;
        }
        return "";
    }

    /**
     * 发布内容
     */
    public void publish() {
        this.status = ContentStatus.PUBLISHED;
        if (this.publishAt == null) {
            this.publishAt = LocalDateTime.now();
        }
    }

    /**
     * 取消发布
     */
    public void unpublish() {
        this.status = ContentStatus.DRAFT;
        this.publishAt = null;
    }

    /**
     * 归档内容
     */
    public void archive() {
        this.status = ContentStatus.ARCHIVED;
    }

    /**
     * 提交审核
     */
    public void submitForReview() {
        this.status = ContentStatus.PENDING;
    }

    /**
     * 置顶
     */
    public void setTop() {
        this.isTop = Boolean.TRUE;
    }

    /**
     * 取消置顶
     */
    public void unsetTop() {
        this.isTop = Boolean.FALSE;
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    /**
     * 增加点赞次数
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    /**
     * 减少点赞次数
     */
    public void decrementLikeCount() {
        this.likeCount = Math.max(0, (this.likeCount == null ? 0 : this.likeCount) - 1);
    }

    /**
     * 增加评论次数
     */
    public void incrementCommentCount() {
        this.commentCount = (this.commentCount == null ? 0 : this.commentCount) + 1;
    }

    /**
     * 减少评论次数
     */
    public void decrementCommentCount() {
        this.commentCount = Math.max(0, (this.commentCount == null ? 0 : this.commentCount) - 1);
    }

    /**
     * 获取内容摘要（自动生成）
     */
    @Transient
    public String getAutoSummary(int maxLength) {
        if (summary != null && !summary.trim().isEmpty()) {
            return summary.length() > maxLength ? summary.substring(0, maxLength) + "..." : summary;
        }

        if (content != null && !content.trim().isEmpty()) {
            String plainText = content.replaceAll("<[^>]*>", "").trim();
            return plainText.length() > maxLength ? plainText.substring(0, maxLength) + "..." : plainText;
        }

        return "";
    }

    /**
     * 预持久化操作
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (this.slug == null || this.slug.trim().isEmpty()) {
            // 如果没有设置slug，使用ID作为slug
            this.slug = this.getId() != null ? this.getId().toString() : null;
        }
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        if (this.commentCount == null) {
            this.commentCount = 0;
        }
    }
}
