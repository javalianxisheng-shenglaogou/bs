package com.multisite.cms.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * 内容分类实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "content_categories", indexes = {
    @Index(name = "idx_site_id", columnList = "site_id"),
    @Index(name = "idx_slug", columnList = "slug"),
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_level", columnList = "level"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"site", "parent", "children", "contents"})
@ToString(callSuper = true, exclude = {"site", "parent", "children", "contents"})
public class ContentCategory extends BaseEntity {

    /**
     * 所属站点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_categories_site"))
    private Site site;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * 分类代码
     */
    @Size(max = 50, message = "分类代码长度不能超过50")
    @Column(name = "code", length = 50, unique = true)
    private String code;

    /**
     * URL别名
     */
    @Size(max = 100, message = "URL别名长度不能超过100")
    @Column(name = "slug", length = 100)
    private String slug;

    /**
     * 分类描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 父分类
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_content_categories_parent"))
    private ContentCategory parent;

    /**
     * 子分类集合
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ContentCategory> children = new HashSet<>();

    /**
     * 层级
     */
    @Column(name = "level", nullable = false)
    @Builder.Default
    private Integer level = 1;

    /**
     * 分类路径
     */
    @Size(max = 500, message = "分类路径长度不能超过500")
    @Column(name = "path", length = 500)
    private String path;

    /**
     * 图标
     */
    @Size(max = 100, message = "图标长度不能超过100")
    @Column(name = "icon", length = 100)
    private String icon;

    /**
     * 封面图片
     */
    @Size(max = 500, message = "封面图片URL长度不能超过500")
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /**
     * 排序
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 是否显示在导航
     */
    @Column(name = "is_nav", nullable = false)
    @Builder.Default
    private Boolean isNav = Boolean.FALSE;

    /**
     * SEO标题
     */
    @Size(max = 255, message = "SEO标题长度不能超过255")
    @Column(name = "seo_title")
    private String seoTitle;

    /**
     * SEO描述
     */
    @Size(max = 500, message = "SEO描述长度不能超过500")
    @Column(name = "seo_description", length = 500)
    private String seoDescription;

    /**
     * 分类下的内容
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Content> contents = new HashSet<>();

    /**
     * 判断是否为根分类
     */
    @Transient
    public boolean isRootCategory() {
        return this.parent == null;
    }

    /**
     * 判断是否为子分类
     */
    @Transient
    public boolean isChildCategory() {
        return this.parent != null;
    }

    /**
     * 判断是否显示在导航
     */
    @Transient
    public boolean isShowInNav() {
        return Boolean.TRUE.equals(this.isNav);
    }

    /**
     * 获取完整路径
     */
    @Transient
    public String getFullPath() {
        if (this.path != null && !this.path.isEmpty()) {
            return this.path;
        }
        
        StringBuilder fullPath = new StringBuilder();
        ContentCategory current = this;
        while (current != null) {
            if (fullPath.length() > 0) {
                fullPath.insert(0, "/");
            }
            String slug = current.getSlug();
            if (slug == null || slug.isEmpty()) {
                slug = current.getId().toString();
            }
            fullPath.insert(0, slug);
            current = current.getParent();
        }
        return "/" + fullPath.toString();
    }

    /**
     * 获取分类层级路径（名称）
     */
    @Transient
    public String getHierarchyPath() {
        StringBuilder hierarchyPath = new StringBuilder();
        ContentCategory current = this;
        while (current != null) {
            if (hierarchyPath.length() > 0) {
                hierarchyPath.insert(0, " > ");
            }
            hierarchyPath.insert(0, current.getName());
            current = current.getParent();
        }
        return hierarchyPath.toString();
    }

    /**
     * 添加子分类
     */
    public void addChild(ContentCategory child) {
        this.children.add(child);
        child.setParent(this);
        child.setLevel(this.level + 1);
    }

    /**
     * 移除子分类
     */
    public void removeChild(ContentCategory child) {
        this.children.remove(child);
        child.setParent(null);
        child.setLevel(1);
    }

    /**
     * 更新分类路径
     */
    public void updatePath() {
        this.path = getFullPath();
        // 递归更新子分类路径
        for (ContentCategory child : this.children) {
            child.updatePath();
        }
    }

    /**
     * 更新层级
     */
    public void updateLevel() {
        if (this.parent != null) {
            this.level = this.parent.getLevel() + 1;
        } else {
            this.level = 1;
        }
        // 递归更新子分类层级
        for (ContentCategory child : this.children) {
            child.updateLevel();
        }
    }

    /**
     * 获取内容数量
     */
    @Transient
    public int getContentCount() {
        return contents != null ? contents.size() : 0;
    }

    /**
     * 获取子分类数量
     */
    @Transient
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * 预持久化操作
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (this.slug == null || this.slug.trim().isEmpty()) {
            // 如果没有设置slug，使用ID作为slug
            this.slug = this.getId() != null ? this.getId().toString() : null;
        }
        updatePath();
        updateLevel();
    }

    /**
     * 预更新操作
     */
    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        updatePath();
        updateLevel();
    }

    /**
     * 获取父分类（别名方法，兼容现有代码）
     */
    public ContentCategory getParentCategory() {
        return this.parent;
    }

    /**
     * 设置父分类（别名方法，兼容现有代码）
     */
    public void setParentCategory(ContentCategory parentCategory) {
        this.parent = parentCategory;
    }
}
