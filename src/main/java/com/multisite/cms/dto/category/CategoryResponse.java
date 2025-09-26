package com.multisite.cms.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.multisite.cms.entity.ContentCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 内容分类响应DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容分类响应")
public class CategoryResponse {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "技术文章")
    private String name;

    /**
     * 分类代码
     */
    @Schema(description = "分类代码", example = "tech")
    private String code;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述", example = "技术相关的文章分类")
    private String description;

    /**
     * 分类路径
     */
    @Schema(description = "分类路径", example = "/tech")
    private String path;

    /**
     * 站点信息
     */
    @Schema(description = "站点信息")
    private SiteInfo site;

    /**
     * 父分类信息
     */
    @Schema(description = "父分类信息")
    private CategoryInfo parentCategory;

    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表")
    private List<CategoryInfo> children;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "0")
    private Integer sortOrder;

    /**
     * 内容数量
     */
    @Schema(description = "内容数量", example = "10")
    private Long contentCount;

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
        
        @Schema(description = "排序值", example = "0")
        private Integer sortOrder;
        
        @Schema(description = "内容数量", example = "5")
        private Long contentCount;
    }

    /**
     * 从ContentCategory实体创建CategoryResponse
     * 
     * @param category ContentCategory实体
     * @return CategoryResponse
     */
    public static CategoryResponse from(ContentCategory category) {
        CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .path(category.getPath())
                .sortOrder(category.getSortOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy());

        // 设置站点信息
        if (category.getSite() != null) {
            builder.site(SiteInfo.builder()
                    .id(category.getSite().getId())
                    .name(category.getSite().getName())
                    .code(category.getSite().getCode())
                    .build());
        }

        // 设置父分类信息
        if (category.getParentCategory() != null) {
            builder.parentCategory(CategoryInfo.builder()
                    .id(category.getParentCategory().getId())
                    .name(category.getParentCategory().getName())
                    .code(category.getParentCategory().getCode())
                    .path(category.getParentCategory().getPath())
                    .sortOrder(category.getParentCategory().getSortOrder())
                    .build());
        }

        // 设置子分类信息
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryInfo> children = category.getChildren().stream()
                    .filter(child -> !child.isDeleted())
                    .map(child -> CategoryInfo.builder()
                            .id(child.getId())
                            .name(child.getName())
                            .code(child.getCode())
                            .path(child.getPath())
                            .sortOrder(child.getSortOrder())
                            .build())
                    .collect(Collectors.toList());
            builder.children(children);
        }

        return builder.build();
    }

    /**
     * 从ContentCategory实体创建简化的CategoryResponse（不包含子分类）
     * 
     * @param category ContentCategory实体
     * @return CategoryResponse
     */
    public static CategoryResponse fromSimple(ContentCategory category) {
        CategoryResponse response = from(category);
        response.setChildren(null); // 移除子分类信息以减少数据传输
        return response;
    }

    /**
     * 创建分类树结构的CategoryResponse
     * 
     * @param category ContentCategory实体
     * @param includeContentCount 是否包含内容数量
     * @return CategoryResponse
     */
    public static CategoryResponse fromTree(ContentCategory category, boolean includeContentCount) {
        CategoryResponse response = from(category);
        
        if (includeContentCount) {
            // 这里可以设置内容数量，需要在调用处计算
            response.setContentCount(0L);
        }
        
        return response;
    }
}
