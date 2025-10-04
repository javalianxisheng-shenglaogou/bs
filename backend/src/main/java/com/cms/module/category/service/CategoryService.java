package com.cms.module.category.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.category.dto.CategoryDTO;
import com.cms.module.category.dto.CategoryQueryDTO;
import com.cms.module.category.entity.Category;
import com.cms.module.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类Service
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 获取分类树(按站点)
     *
     * @param siteId 站点ID
     * @return 分类树
     */
    public List<CategoryDTO> getCategoryTree(Long siteId) {
        List<Category> allCategories = categoryRepository.findBySiteIdAndDeletedFalseOrderBySortOrderAsc(siteId);
        return buildTree(allCategories, null);
    }

    /**
     * 获取可见分类树(按站点)
     *
     * @param siteId 站点ID
     * @return 可见分类树
     */
    public List<CategoryDTO> getVisibleCategoryTree(Long siteId) {
        List<Category> visibleCategories = categoryRepository.findBySiteIdAndIsVisibleAndDeletedFalseOrderBySortOrderAsc(siteId, true);
        return buildTree(visibleCategories, null);
    }

    /**
     * 构建分类树
     *
     * @param categories 分类列表
     * @param parentId   父分类ID
     * @return 分类树
     */
    private List<CategoryDTO> buildTree(List<Category> categories, Long parentId) {
        return categories.stream()
                .filter(category -> {
                    if (parentId == null) {
                        return category.getParentId() == null || category.getParentId() == 0;
                    }
                    return parentId.equals(category.getParentId());
                })
                .map(category -> {
                    CategoryDTO dto = convertToDTO(category);
                    List<CategoryDTO> children = buildTree(categories, category.getId());
                    if (!children.isEmpty()) {
                        dto.setChildren(children);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 分页查询分类
     *
     * @param queryDTO 查询条件
     * @param pageable 分页参数
     * @return 分类分页数据
     */
    public Page<CategoryDTO> getCategories(CategoryQueryDTO queryDTO, Pageable pageable) {
        Specification<Category> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 未删除
            predicates.add(cb.equal(root.get("deleted"), false));

            // 站点ID
            if (queryDTO.getSiteId() != null) {
                predicates.add(cb.equal(root.get("siteId"), queryDTO.getSiteId()));
            }

            // 父分类ID
            if (queryDTO.getParentId() != null) {
                predicates.add(cb.equal(root.get("parentId"), queryDTO.getParentId()));
            }

            // 分类名称(模糊查询)
            if (StringUtils.hasText(queryDTO.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + queryDTO.getName() + "%"));
            }

            // 分类编码
            if (StringUtils.hasText(queryDTO.getCode())) {
                predicates.add(cb.equal(root.get("code"), queryDTO.getCode()));
            }

            // 是否可见
            if (queryDTO.getIsVisible() != null) {
                predicates.add(cb.equal(root.get("isVisible"), queryDTO.getIsVisible()));
            }

            // 层级
            if (queryDTO.getLevel() != null) {
                predicates.add(cb.equal(root.get("level"), queryDTO.getLevel()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return categoryRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类DTO
     */
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        if (category.getDeleted()) {
            throw new BusinessException("分类已删除");
        }
        return convertToDTO(category);
    }

    /**
     * 创建分类
     *
     * @param dto 分类DTO
     * @return 创建的分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO createCategory(CategoryDTO dto) {
        // 检查编码是否重复
        if (categoryRepository.existsBySiteIdAndCodeAndDeletedFalse(dto.getSiteId(), dto.getCode())) {
            throw new BusinessException("分类编码已存在");
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category, "id", "createdAt", "updatedAt", "version");

        // 设置层级和路径
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BusinessException("父分类不存在"));
            category.setLevel(parent.getLevel() + 1);
            category.setPath(parent.getPath() + "/" + category.getId());
        } else {
            category.setParentId(null);
            category.setLevel(1);
            category.setPath("/" + category.getId());
        }

        category = categoryRepository.save(category);

        // 更新路径(因为需要ID)
        category.setPath(category.getPath().replace("null", category.getId().toString()));
        category = categoryRepository.save(category);

        log.info("创建分类成功: {}", category.getName());
        return convertToDTO(category);
    }

    /**
     * 更新分类
     *
     * @param id  分类ID
     * @param dto 分类DTO
     * @return 更新后的分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));

        if (category.getDeleted()) {
            throw new BusinessException("分类已删除");
        }

        // 检查编码是否重复
        if (!category.getCode().equals(dto.getCode())) {
            if (categoryRepository.existsByCodeExcludingId(dto.getSiteId(), dto.getCode(), id)) {
                throw new BusinessException("分类编码已存在");
            }
        }

        // 不允许修改站点ID和父分类ID(避免破坏树形结构)
        BeanUtils.copyProperties(dto, category, "id", "siteId", "parentId", "level", "path", "createdAt", "updatedAt", "createdBy", "version");

        category = categoryRepository.save(category);
        log.info("更新分类成功: {}", category.getName());
        return convertToDTO(category);
    }

    /**
     * 删除分类(软删除)
     *
     * @param id 分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));

        if (category.getDeleted()) {
            throw new BusinessException("分类已删除");
        }

        // 检查是否有子分类
        List<Category> children = categoryRepository.findBySiteIdAndParentIdAndDeletedFalseOrderBySortOrderAsc(
                category.getSiteId(), category.getId());
        if (!children.isEmpty()) {
            throw new BusinessException("该分类下有子分类,无法删除");
        }

        // TODO: 检查是否有内容关联

        category.setDeleted(true);
        categoryRepository.save(category);
        log.info("删除分类成功: {}", category.getName());
    }

    /**
     * 更新分类可见性
     *
     * @param id        分类ID
     * @param isVisible 是否可见
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateVisibility(Long id, Boolean isVisible) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));

        if (category.getDeleted()) {
            throw new BusinessException("分类已删除");
        }

        category.setIsVisible(isVisible);
        categoryRepository.save(category);
        log.info("更新分类可见性成功: {} -> {}", category.getName(), isVisible);
    }

    /**
     * 转换为DTO
     *
     * @param category 分类实体
     * @return 分类DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}

