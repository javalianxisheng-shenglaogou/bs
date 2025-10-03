package com.cms.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应
 *
 * @param <T> 数据类型
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {

    /**
     * 数据列表
     */
    private List<T> content;

    /**
     * 当前页码（从1开始）
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;
}

