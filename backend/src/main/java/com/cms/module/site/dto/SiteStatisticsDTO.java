package com.cms.module.site.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 站点统计DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteStatisticsDTO {

    /**
     * 站点ID
     */
    private Long siteId;

    /**
     * 内容总数
     */
    private Long contentCount;

    /**
     * 已发布内容数
     */
    private Long publishedContentCount;

    /**
     * 草稿内容数
     */
    private Long draftContentCount;

    /**
     * 分类总数
     */
    private Long categoryCount;

    /**
     * 用户总数
     */
    private Long userCount;

    /**
     * 今日访问量
     */
    private Long todayVisits;

    /**
     * 总访问量
     */
    private Long totalVisits;
}

