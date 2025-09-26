package com.multisite.cms.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页结果类
 * 
 * @param <T> 数据类型
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> content;

    /**
     * 当前页码（从1开始）
     */
    @Schema(description = "当前页码", example = "1")
    private Integer page;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer size;

    /**
     * 总元素数量
     */
    @Schema(description = "总元素数量", example = "100")
    private Long totalElements;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;

    /**
     * 是否为第一页
     */
    @Schema(description = "是否为第一页", example = "true")
    private Boolean first;

    /**
     * 是否为最后一页
     */
    @Schema(description = "是否为最后一页", example = "false")
    private Boolean last;

    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    /**
     * 当前页元素数量
     */
    @Schema(description = "当前页元素数量", example = "10")
    private Integer numberOfElements;

    /**
     * 是否为空页
     */
    @Schema(description = "是否为空页", example = "false")
    private Boolean empty;

    /**
     * 从Spring Data Page对象创建PageResult
     * 
     * @param page Spring Data Page对象
     * @param <T> 数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return PageResult.<T>builder()
                .content(page.getContent())
                .page(page.getNumber() + 1) // Spring Data页码从0开始，转换为从1开始
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .build();
    }

    /**
     * 创建空的分页结果
     * 
     * @param page 页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return 空的PageResult对象
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return PageResult.<T>builder()
                .content(List.of())
                .page(page)
                .size(size)
                .totalElements(0L)
                .totalPages(0)
                .first(true)
                .last(true)
                .hasPrevious(false)
                .hasNext(false)
                .numberOfElements(0)
                .empty(true)
                .build();
    }

    /**
     * 创建单页结果
     * 
     * @param content 数据列表
     * @param <T> 数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(List<T> content) {
        int size = content.size();
        return PageResult.<T>builder()
                .content(content)
                .page(1)
                .size(size)
                .totalElements((long) size)
                .totalPages(size > 0 ? 1 : 0)
                .first(true)
                .last(true)
                .hasPrevious(false)
                .hasNext(false)
                .numberOfElements(size)
                .empty(content.isEmpty())
                .build();
    }

    /**
     * 手动创建分页结果
     * 
     * @param content 数据列表
     * @param page 当前页码
     * @param size 每页大小
     * @param totalElements 总元素数量
     * @param <T> 数据类型
     * @return PageResult对象
     */
    public static <T> PageResult<T> of(List<T> content, Integer page, Integer size, Long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean first = page == 1;
        boolean last = page >= totalPages;
        
        return PageResult.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(first)
                .last(last)
                .hasPrevious(!first)
                .hasNext(!last)
                .numberOfElements(content.size())
                .empty(content.isEmpty())
                .build();
    }

    /**
     * 获取分页信息摘要
     *
     * @return 分页信息字符串
     */
    public String getSummary() {
        // 检查所有可能为null的字段
        if (empty == null || empty || totalElements == null || totalElements == 0 ||
            page == null || size == null || totalPages == null) {
            return "无数据";
        }

        try {
            long startElement = (long) (page - 1) * size + 1;
            long endElement = Math.min((long) page * size, totalElements);

            return String.format("第 %d-%d 条，共 %d 条记录，第 %d/%d 页",
                    startElement, endElement, totalElements, page, totalPages);
        } catch (Exception e) {
            // 如果计算过程中出现任何异常，返回默认信息
            return "分页信息不可用";
        }
    }

    /**
     * 判断是否有数据
     * 
     * @return 是否有数据
     */
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    /**
     * 获取数据数量
     * 
     * @return 数据数量
     */
    public int getContentSize() {
        return content != null ? content.size() : 0;
    }
}
