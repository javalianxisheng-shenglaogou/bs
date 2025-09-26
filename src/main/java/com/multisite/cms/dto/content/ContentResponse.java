package com.multisite.cms.dto.content;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multisite.cms.entity.Content;
import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容响应DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容响应")
public class ContentResponse {

    /**
     * 内容ID
     */
    @Schema(description = "内容ID", example = "1")
    private Long id;

    /**
     * 标题
     */
    @Schema(description = "标题", example = "示例文章标题")
    private String title;

    /**
     * URL别名
     */
    @Schema(description = "URL别名", example = "example-article")
    private String slug;

    /**
     * 摘要
     */
    @Schema(description = "摘要", example = "这是一篇示例文章的摘要")
    private String summary;

    /**
     * 内容
     */
    @Schema(description = "内容", example = "这是文章的详细内容...")
    private String content;

    /**
     * 内容类型
     */
    @Schema(description = "内容类型", example = "ARTICLE")
    private ContentType contentType;

    /**
     * 内容状态
     */
    @Schema(description = "内容状态", example = "PUBLISHED")
    private ContentStatus status;

    /**
     * 站点信息
     */
    @Schema(description = "站点信息")
    private SiteInfo site;

    /**
     * 分类信息
     */
    @Schema(description = "分类信息")
    private CategoryInfo category;

    /**
     * 作者信息
     */
    @Schema(description = "作者信息")
    private AuthorInfo author;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数", example = "100")
    private Long viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数", example = "10")
    private Long likeCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishAt;

    /**
     * 特色图片
     */
    @Schema(description = "特色图片URL", example = "https://example.com/image.jpg")
    private String featuredImage;

    /**
     * SEO标题
     */
    @Schema(description = "SEO标题", example = "示例文章 - 网站名称")
    private String seoTitle;

    /**
     * SEO描述
     */
    @Schema(description = "SEO描述", example = "这是一篇关于示例的文章，包含了详细的说明和案例")
    private String seoDescription;

    /**
     * SEO关键词
     */
    @Schema(description = "SEO关键词", example = "示例,文章,教程")
    private String seoKeywords;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin")
    private String createdBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "admin")
    private String updatedBy;

    /**
     * 站点信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "站点信息")
    public static class SiteInfo {
        @Schema(description = "站点ID", example = "1")
        private Long id;
        
        @Schema(description = "站点名称", example = "示例站点")
        private String name;
        
        @Schema(description = "站点代码", example = "example_site")
        private String code;
    }

    /**
     * 分类信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类信息")
    public static class CategoryInfo {
        @Schema(description = "分类ID", example = "1")
        private Long id;
        
        @Schema(description = "分类名称", example = "技术文章")
        private String name;
        
        @Schema(description = "分类代码", example = "tech")
        private String code;
        
        @Schema(description = "分类路径", example = "/tech")
        private String path;
    }

    /**
     * 作者信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "作者信息")
    public static class AuthorInfo {
        @Schema(description = "用户ID", example = "1")
        private Long id;
        
        @Schema(description = "用户名", example = "admin")
        private String username;
        
        @Schema(description = "显示名称", example = "管理员")
        private String displayName;
        
        @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
        private String avatarUrl;
    }

    /**
     * 从Content实体创建ContentResponse
     * 
     * @param content Content实体
     * @return ContentResponse
     */
    public static ContentResponse from(Content content) {
        ContentResponseBuilder builder = ContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .slug(content.getSlug())
                .summary(content.getSummary())
                .content(content.getContent())
                .contentType(content.getContentType())
                .status(content.getStatus())
                .isTop(content.getIsTop())
                .viewCount(content.getViewCount() != null ? content.getViewCount().longValue() : 0L)
                .likeCount(content.getLikeCount() != null ? content.getLikeCount().longValue() : 0L)
                .publishAt(content.getPublishAt())
                .featuredImage(content.getFeaturedImage())
                .seoTitle(content.getSeoTitle())
                .seoDescription(content.getSeoDescription())
                .seoKeywords(content.getSeoKeywords())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .createdBy(content.getCreatedBy())
                .updatedBy(content.getUpdatedBy());

        // 设置站点信息
        if (content.getSite() != null) {
            builder.site(SiteInfo.builder()
                    .id(content.getSite().getId())
                    .name(content.getSite().getName())
                    .code(content.getSite().getCode())
                    .build());
        }

        // 设置分类信息
        if (content.getCategory() != null) {
            builder.category(CategoryInfo.builder()
                    .id(content.getCategory().getId())
                    .name(content.getCategory().getName())
                    .code(content.getCategory().getCode())
                    .path(content.getCategory().getPath())
                    .build());
        }

        // 设置作者信息
        if (content.getAuthor() != null) {
            builder.author(AuthorInfo.builder()
                    .id(content.getAuthor().getId())
                    .username(content.getAuthor().getUsername())
                    .displayName(content.getAuthor().getDisplayName())
                    .avatarUrl(content.getAuthor().getAvatarUrl())
                    .build());
        }

        return builder.build();
    }

    /**
     * 从Content实体创建简化的ContentResponse（不包含内容正文）
     * 
     * @param content Content实体
     * @return ContentResponse
     */
    public static ContentResponse fromSummary(Content content) {
        ContentResponse response = from(content);
        response.setContent(null); // 移除内容正文以减少数据传输
        return response;
    }
}
