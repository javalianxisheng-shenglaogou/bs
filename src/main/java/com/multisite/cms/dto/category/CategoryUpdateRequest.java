package com.multisite.cms.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内容分类更新请求DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容分类更新请求")
public class CategoryUpdateRequest {

    /**
     * 分类名称
     */
    @Schema(description = "分类名称", example = "技术文章")
    @Size(min = 1, max = 100, message = "分类名称长度必须在1-100个字符之间")
    private String name;

    /**
     * 分类代码
     */
    @Schema(description = "分类代码", example = "tech")
    @Size(min = 1, max = 50, message = "分类代码长度必须在1-50个字符之间")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "分类代码只能包含小写字母、数字、下划线和连字符")
    private String code;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述", example = "技术相关的文章分类")
    @Size(max = 500, message = "分类描述长度不能超过500个字符")
    private String description;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID", example = "1")
    private Long parentCategoryId;

    /**
     * 排序值
     */
    @Schema(description = "排序值", example = "0")
    private Integer sortOrder;
}
