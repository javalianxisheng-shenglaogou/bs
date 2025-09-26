package com.multisite.cms.service;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.ContentCategory;

import java.util.List;
import java.util.Optional;

/**
 * 内容分类服务接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface ContentCategoryService {

    /**
     * 分页查询分类
     * 
     * @param siteId 站点ID
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序字段
     * @param direction 排序方向
     * @param keyword 搜索关键字
     * @return 分类分页结果
     */
    PageResult<ContentCategory> getCategories(Long siteId, int page, int size, String sort, String direction, String keyword);

    /**
     * 根据ID获取分类
     * 
     * @param categoryId 分类ID
     * @return 分类Optional
     */
    Optional<ContentCategory> getCategoryById(Long categoryId);

    /**
     * 根据代码获取分类
     * 
     * @param siteId 站点ID
     * @param code 分类代码
     * @return 分类Optional
     */
    Optional<ContentCategory> getCategoryByCode(Long siteId, String code);

    /**
     * 根据路径获取分类
     * 
     * @param siteId 站点ID
     * @param path 分类路径
     * @return 分类Optional
     */
    Optional<ContentCategory> getCategoryByPath(Long siteId, String path);

    /**
     * 获取根分类列表
     * 
     * @param siteId 站点ID
     * @return 根分类列表
     */
    List<ContentCategory> getRootCategories(Long siteId);

    /**
     * 获取子分类列表
     * 
     * @param siteId 站点ID
     * @param parentCategoryId 父分类ID
     * @return 子分类列表
     */
    List<ContentCategory> getChildCategories(Long siteId, Long parentCategoryId);

    /**
     * 创建分类
     * 
     * @param category 分类信息
     * @param createdBy 创建者
     * @return 创建的分类
     */
    ContentCategory createCategory(ContentCategory category, String createdBy);

    /**
     * 更新分类
     * 
     * @param categoryId 分类ID
     * @param updateCategory 更新的分类信息
     * @param updatedBy 更新者
     * @return 更新后的分类
     */
    ContentCategory updateCategory(Long categoryId, ContentCategory updateCategory, String updatedBy);

    /**
     * 删除分类
     * 
     * @param categoryId 分类ID
     * @param deletedBy 删除者
     */
    void deleteCategory(Long categoryId, String deletedBy);

    /**
     * 批量删除分类
     * 
     * @param categoryIds 分类ID列表
     * @param deletedBy 删除者
     */
    void deleteCategories(List<Long> categoryIds, String deletedBy);

    /**
     * 更新分类排序
     * 
     * @param categoryId 分类ID
     * @param sortOrder 排序值
     */
    void updateCategorySortOrder(Long categoryId, Integer sortOrder);

    /**
     * 检查分类代码是否可用
     * 
     * @param siteId 站点ID
     * @param code 分类代码
     * @param excludeCategoryId 排除的分类ID
     * @return 是否可用
     */
    boolean isCategoryCodeAvailable(Long siteId, String code, Long excludeCategoryId);

    /**
     * 获取分类统计信息
     * 
     * @param siteId 站点ID
     * @return 分类统计
     */
    CategoryStats getCategoryStats(Long siteId);

    /**
     * 分类统计信息内部类
     */
    class CategoryStats {
        private final long totalCategories;
        private final long rootCategories;
        private final long maxDepth;

        public CategoryStats(long totalCategories, long rootCategories, long maxDepth) {
            this.totalCategories = totalCategories;
            this.rootCategories = rootCategories;
            this.maxDepth = maxDepth;
        }

        public long getTotalCategories() {
            return totalCategories;
        }

        public long getRootCategories() {
            return rootCategories;
        }

        public long getMaxDepth() {
            return maxDepth;
        }
    }
}
