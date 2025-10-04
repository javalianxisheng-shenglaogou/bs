package com.cms.module.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class CategoryDTO {

    private Long id;

    @NotNull(message = "站点ID不能为空")
    private Long siteId;

    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotBlank(message = "分类编码不能为空")
    private String code;

    private String description;

    private String iconUrl;

    private String coverUrl;

    private Integer sortOrder = 0;

    private Integer level = 1;

    private String path;

    private Boolean isVisible = true;

    private String seoTitle;

    private String seoKeywords;

    private String seoDescription;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    private Long updatedBy;

    private Integer version;

    /**
     * 子分类列表(用于树形结构)
     */
    private List<CategoryDTO> children;
}

