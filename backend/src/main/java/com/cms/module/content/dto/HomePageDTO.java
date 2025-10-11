package com.cms.module.content.dto;

import com.cms.module.category.dto.CategoryTreeDTO;
import lombok.Data;

import java.util.List;

/**
 * 首页数据DTO
 *
 * @author CMS Team
 * @since 1.3.0
 */
@Data
public class HomePageDTO {
    
    /**
     * 分类树列表
     */
    private List<CategoryTreeDTO> categories;
    
    /**
     * 置顶内容列表
     */
    private List<PublicContentDTO> topContents;
    
    /**
     * 推荐内容列表
     */
    private List<PublicContentDTO> featuredContents;
}

