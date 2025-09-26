package com.multisite.cms.service.impl;

import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.ContentCategory;
import com.multisite.cms.repository.ContentCategoryRepository;
import com.multisite.cms.service.ContentCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 内容分类服务类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentCategoryServiceImpl implements ContentCategoryService {

    private final ContentCategoryRepository contentCategoryRepository;

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
    @Transactional(readOnly = true)
    public PageResult<ContentCategory> getCategories(Long siteId, int page, int size, String sort, String direction, String keyword) {
        log.debug("Querying categories: siteId={}, page={}, size={}, sort={}, direction={}, keyword={}", 
                siteId, page, size, sort, direction, keyword);
        
        // 创建排序对象
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(sortDirection, sort != null ? sort : "sortOrder", "createdAt");
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, sortObj);
        
        Page<ContentCategory> categoryPage;
        
        if (StringUtils.hasText(keyword)) {
            // 按关键字搜索
            categoryPage = contentCategoryRepository.findBySiteIdAndKeyword(siteId, keyword, pageable);
        } else {
            // 查询所有
            categoryPage = contentCategoryRepository.findBySiteIdAndDeletedFalse(siteId, pageable);
        }
        
        return PageResult.of(categoryPage);
    }

    /**
     * 根据ID获取分类
     * 
     * @param categoryId 分类ID
     * @return 分类信息
     */
    @Transactional(readOnly = true)
    public Optional<ContentCategory> getCategoryById(Long categoryId) {
        log.debug("Getting category by ID: {}", categoryId);
        return contentCategoryRepository.findById(categoryId)
                .filter(category -> !category.isDeleted());
    }

    /**
     * 根据站点ID和代码获取分类
     * 
     * @param siteId 站点ID
     * @param code 分类代码
     * @return 分类信息
     */
    @Transactional(readOnly = true)
    public Optional<ContentCategory> getCategoryByCode(Long siteId, String code) {
        log.debug("Getting category by code: siteId={}, code={}", siteId, code);
        return contentCategoryRepository.findBySiteIdAndCodeAndDeletedFalse(siteId, code);
    }

    /**
     * 根据站点ID和路径获取分类
     * 
     * @param siteId 站点ID
     * @param path 分类路径
     * @return 分类信息
     */
    @Transactional(readOnly = true)
    public Optional<ContentCategory> getCategoryByPath(Long siteId, String path) {
        log.debug("Getting category by path: siteId={}, path={}", siteId, path);
        return contentCategoryRepository.findBySiteIdAndPathAndDeletedFalse(siteId, path);
    }

    /**
     * 获取根分类列表
     * 
     * @param siteId 站点ID
     * @return 根分类列表
     */
    @Transactional(readOnly = true)
    public List<ContentCategory> getRootCategories(Long siteId) {
        log.debug("Getting root categories for site: {}", siteId);
        return contentCategoryRepository.findBySiteIdAndParentIdIsNullAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(siteId);
    }

    /**
     * 获取子分类列表
     * 
     * @param siteId 站点ID
     * @param parentCategoryId 父分类ID
     * @return 子分类列表
     */
    @Transactional(readOnly = true)
    public List<ContentCategory> getChildCategories(Long siteId, Long parentCategoryId) {
        log.debug("Getting child categories: siteId={}, parentId={}", siteId, parentCategoryId);
        return contentCategoryRepository.findBySiteIdAndParentIdAndDeletedFalseOrderBySortOrderAscCreatedAtAsc(siteId, parentCategoryId);
    }

    /**
     * 创建分类
     * 
     * @param category 分类信息
     * @param createdBy 创建者ID
     * @return 创建的分类
     */
    @Transactional
    public ContentCategory createCategory(ContentCategory category, Long createdBy) {
        log.info("Creating category: siteId={}, code={}", category.getSite().getId(), category.getCode());
        
        // 验证分类代码唯一性
        if (contentCategoryRepository.existsBySiteIdAndNameAndDeletedFalse(category.getSite().getId(), category.getName())) {
            throw new RuntimeException("分类名称已存在: " + category.getName());
        }
        
        // 生成分类路径
        String path = generateCategoryPath(category);
        category.setPath(path);
        
        // 设置创建信息
        category.setCreatedBy(createdBy);
        category.setUpdatedBy(createdBy);
        
        // 设置默认排序值
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        
        ContentCategory savedCategory = contentCategoryRepository.save(category);
        log.info("Category created successfully: {} (ID: {})", savedCategory.getCode(), savedCategory.getId());
        
        return savedCategory;
    }

    /**
     * 更新分类
     * 
     * @param categoryId 分类ID
     * @param updateCategory 更新的分类信息
     * @param updatedBy 更新者ID
     * @return 更新后的分类
     */
    @Transactional
    public ContentCategory updateCategory(Long categoryId, ContentCategory updateCategory, Long updatedBy) {
        log.info("Updating category: {}", categoryId);
        
        ContentCategory existingCategory = contentCategoryRepository.findById(categoryId)
                .filter(category -> !category.isDeleted())
                .orElseThrow(() -> new RuntimeException("分类不存在: " + categoryId));
        
        // 验证分类代码唯一性（排除当前分类）
        if (StringUtils.hasText(updateCategory.getCode()) && 
            !updateCategory.getCode().equals(existingCategory.getCode()) &&
            contentCategoryRepository.existsBySiteIdAndCodeAndIdNotAndDeletedFalse(
                    existingCategory.getSite().getId(), updateCategory.getCode(), categoryId)) {
            throw new RuntimeException("分类代码已存在: " + updateCategory.getCode());
        }
        
        // 更新字段
        boolean pathChanged = false;
        if (StringUtils.hasText(updateCategory.getName())) {
            existingCategory.setName(updateCategory.getName());
        }
        if (StringUtils.hasText(updateCategory.getCode())) {
            existingCategory.setCode(updateCategory.getCode());
            pathChanged = true;
        }
        if (StringUtils.hasText(updateCategory.getDescription())) {
            existingCategory.setDescription(updateCategory.getDescription());
        }
        if (updateCategory.getSortOrder() != null) {
            existingCategory.setSortOrder(updateCategory.getSortOrder());
        }
        if (updateCategory.getParentCategory() != null) {
            existingCategory.setParentCategory(updateCategory.getParentCategory());
            pathChanged = true;
        }
        
        // 如果路径相关字段发生变化，重新生成路径
        if (pathChanged) {
            String newPath = generateCategoryPath(existingCategory);
            String oldPath = existingCategory.getPath();
            existingCategory.setPath(newPath);
            
            // 更新子分类的路径
            if (!oldPath.equals(newPath)) {
                updateChildCategoryPaths(oldPath, newPath, existingCategory.getSite().getId());
            }
        }
        
        existingCategory.setUpdatedBy(updatedBy);
        
        ContentCategory savedCategory = contentCategoryRepository.save(existingCategory);
        log.info("Category updated successfully: {} (ID: {})", savedCategory.getCode(), savedCategory.getId());
        
        return savedCategory;
    }

    /**
     * 删除分类（软删除）
     * 
     * @param categoryId 分类ID
     * @param deletedBy 删除者ID
     */
    @Transactional
    public void deleteCategory(Long categoryId, Long deletedBy) {
        log.info("Deleting category: {}", categoryId);
        
        ContentCategory category = contentCategoryRepository.findById(categoryId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new RuntimeException("分类不存在: " + categoryId));
        
        // 检查是否有子分类
        long childCount = contentCategoryRepository.countByParentIdAndDeletedFalse(categoryId);
        if (childCount > 0) {
            throw new RuntimeException("存在子分类，无法删除");
        }
        
        category.markAsDeleted();
        category.setUpdatedBy(deletedBy);
        contentCategoryRepository.save(category);
        
        log.info("Category deleted successfully: {} (ID: {})", category.getCode(), categoryId);
    }

    /**
     * 批量删除分类
     * 
     * @param categoryIds 分类ID列表
     * @param deletedBy 删除者ID
     */
    @Transactional
    public void deleteCategories(List<Long> categoryIds, Long deletedBy) {
        log.info("Batch deleting categories: {}", categoryIds);
        
        List<ContentCategory> categories = contentCategoryRepository.findAllById(categoryIds);
        for (ContentCategory category : categories) {
            if (!category.isDeleted()) {
                // 检查是否有子分类
                long childCount = contentCategoryRepository.countByParentIdAndDeletedFalse(category.getId());
                if (childCount > 0) {
                    throw new RuntimeException("分类 " + category.getCode() + " 存在子分类，无法删除");
                }
                
                category.markAsDeleted();
                category.setUpdatedBy(deletedBy);
            }
        }
        
        contentCategoryRepository.saveAll(categories);
        log.info("Batch delete completed for {} categories", categories.size());
    }

    /**
     * 更新分类排序
     * 
     * @param categoryId 分类ID
     * @param sortOrder 排序值
     */
    @Transactional
    public void updateCategorySortOrder(Long categoryId, Integer sortOrder) {
        log.info("Updating category sort order: {} -> {}", categoryId, sortOrder);
        contentCategoryRepository.updateSortOrder(categoryId, sortOrder);
        log.info("Category sort order updated: {}", categoryId);
    }

    /**
     * 检查分类代码是否可用
     * 
     * @param siteId 站点ID
     * @param code 分类代码
     * @param excludeCategoryId 排除的分类ID
     * @return 是否可用
     */
    @Transactional(readOnly = true)
    public boolean isCategoryCodeAvailable(Long siteId, String code, Long excludeCategoryId) {
        if (excludeCategoryId != null) {
            return !contentCategoryRepository.existsBySiteIdAndCodeAndIdNotAndDeletedFalse(siteId, code, excludeCategoryId);
        } else {
            return !contentCategoryRepository.existsBySiteIdAndCodeAndDeletedFalse(siteId, code);
        }
    }

    /**
     * 获取分类统计信息
     * 
     * @param siteId 站点ID
     * @return 统计信息
     */
    @Transactional(readOnly = true)
    public ContentCategoryService.CategoryStats getCategoryStats(Long siteId) {
        long totalCategories = contentCategoryRepository.countBySiteIdAndDeletedFalse(siteId);

        return new ContentCategoryService.CategoryStats(totalCategories, 0L, 0L);
    }

    /**
     * 生成分类路径
     * 
     * @param category 分类
     * @return 路径
     */
    private String generateCategoryPath(ContentCategory category) {
        if (category.getParentCategory() == null) {
            return "/" + category.getCode();
        } else {
            return category.getParentCategory().getPath() + "/" + category.getCode();
        }
    }

    /**
     * 更新子分类路径
     * 
     * @param oldPathPrefix 旧路径前缀
     * @param newPathPrefix 新路径前缀
     * @param siteId 站点ID
     */
    private void updateChildCategoryPaths(String oldPathPrefix, String newPathPrefix, Long siteId) {
        contentCategoryRepository.updatePathsByPrefix(oldPathPrefix, newPathPrefix, siteId);
    }

    /**
     * 分类统计信息类
     */
    @lombok.Data
    @lombok.Builder
    public static class CategoryStats {
        private long totalCategories;
    }
}
