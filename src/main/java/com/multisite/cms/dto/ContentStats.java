package com.multisite.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 内容统计信息DTO
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentStats {

    /**
     * 总内容数
     */
    private long totalContents;

    /**
     * 已发布内容数
     */
    private long publishedContents;

    /**
     * 草稿内容数
     */
    private long draftContents;

    /**
     * 待审核内容数
     */
    private long pendingContents;

    /**
     * 已归档内容数
     */
    private long archivedContents;

    /**
     * 今日新增内容数
     */
    private long todayContents;

    /**
     * 本周新增内容数
     */
    private long weekContents;

    /**
     * 本月新增内容数
     */
    private long monthContents;
}
