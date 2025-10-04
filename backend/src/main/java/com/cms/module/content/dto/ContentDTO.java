package com.cms.module.content.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 内容DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class ContentDTO {

    private Long id;

    @NotNull(message = "站点ID不能为空")
    private Long siteId;

    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "URL别名不能为空")
    private String slug;

    private String summary;

    private String content;

    private String contentType = "ARTICLE";

    private String template;

    private String coverImage;

    private Long authorId;

    private String authorName;

    private String status = "DRAFT";

    private LocalDateTime publishedAt;

    private Integer viewCount = 0;

    private Boolean isTop = false;

    private Boolean isFeatured = false;

    private Boolean isOriginal = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    private Integer version;
}

