package com.multisite.cms.dto.content;

import com.multisite.cms.enums.ContentStatus;
import com.multisite.cms.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容创建请求DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容创建请求")
public class ContentCreateRequest {

    /**
     * 标题
     */
    @Schema(description = "标题", example = "示例文章标题", required = true)
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度必须在1-200个字符之间")
    private String title;

    /**
     * URL别名
     */
    @Schema(description = "URL别名", example = "example-article")
    @Size(max = 200, message = "URL别名长度不能超过200个字符")
    private String slug;

    /**
     * 摘要
     */
    @Schema(description = "摘要", example = "这是一篇示例文章的摘要")
    @Size(max = 1000, message = "摘要长度不能超过1000个字符")
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
    private ContentType contentType = ContentType.ARTICLE;

    /**
     * 内容状态
     */
    @Schema(description = "内容状态", example = "DRAFT")
    private ContentStatus status = ContentStatus.DRAFT;

    /**
     * 站点ID
     */
    @Schema(description = "站点ID", example = "1", required = true)
    private Long siteId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶", example = "false")
    private Boolean isTop = false;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间", example = "2024-01-01T10:00:00")
    private LocalDateTime publishAt;

    /**
     * 特色图片
     */
    @Schema(description = "特色图片URL", example = "https://example.com/image.jpg")
    @Size(max = 500, message = "特色图片URL长度不能超过500个字符")
    private String featuredImage;

    /**
     * SEO标题
     */
    @Schema(description = "SEO标题", example = "示例文章 - 网站名称")
    @Size(max = 200, message = "SEO标题长度不能超过200个字符")
    private String seoTitle;

    /**
     * SEO描述
     */
    @Schema(description = "SEO描述", example = "这是一篇关于示例的文章，包含了详细的说明和案例")
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    private String seoDescription;

    /**
     * SEO关键词
     */
    @Schema(description = "SEO关键词", example = "示例,文章,教程")
    @Size(max = 200, message = "SEO关键词长度不能超过200个字符")
    private String seoKeywords;
}
