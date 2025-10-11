package com.cms.module.category.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类树DTO
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Data
public class CategoryTreeDTO {
    
    /**
     * 分类ID
     */
    private Long id;
    
    /**
     * 分类名称
     */
    private String name;
    
    /**
     * 分类代码
     */
    private String code;
    
    /**
     * 图标URL
     */
    private String iconUrl;
    
    /**
     * 该分类下的内容数量
     */
    private Long contentCount;
    
    /**
     * 子分类列表
     */
    private List<CategoryTreeDTO> children = new ArrayList<>();
}

