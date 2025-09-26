package com.multisite.cms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容状态枚举
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ContentStatus {
    
    /**
     * 草稿状态 - 内容正在编辑中
     */
    DRAFT("草稿", "内容正在编辑中，尚未提交审核"),
    
    /**
     * 待审核状态 - 内容已提交，等待审核
     */
    PENDING("待审核", "内容已提交，等待审核人员审核"),
    
    /**
     * 已发布状态 - 内容已审核通过并发布
     */
    PUBLISHED("已发布", "内容已审核通过并发布，用户可以查看"),
    
    /**
     * 已归档状态 - 内容已归档，不再显示
     */
    ARCHIVED("已归档", "内容已归档，不再对外显示");

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据名称获取内容状态
     * 
     * @param name 状态名称
     * @return 内容状态枚举
     */
    public static ContentStatus fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return DRAFT; // 默认状态
        }
        
        for (ContentStatus status : values()) {
            if (status.name().equalsIgnoreCase(name.trim()) || 
                status.getName().equals(name.trim())) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的内容状态: " + name);
    }

    /**
     * 判断是否为草稿状态
     * 
     * @return true 如果是草稿状态
     */
    public boolean isDraft() {
        return this == DRAFT;
    }

    /**
     * 判断是否为待审核状态
     * 
     * @return true 如果是待审核状态
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * 判断是否为已发布状态
     * 
     * @return true 如果是已发布状态
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }

    /**
     * 判断是否为已归档状态
     * 
     * @return true 如果是已归档状态
     */
    public boolean isArchived() {
        return this == ARCHIVED;
    }

    /**
     * 判断是否为可编辑状态
     * 
     * @return true 如果可以编辑
     */
    public boolean isEditable() {
        return this == DRAFT || this == PENDING;
    }

    /**
     * 判断是否为可见状态（对外可见）
     * 
     * @return true 如果对外可见
     */
    public boolean isVisible() {
        return this == PUBLISHED;
    }

    /**
     * 获取状态的CSS样式类
     * 
     * @return CSS样式类名
     */
    public String getCssClass() {
        switch (this) {
            case DRAFT:
                return "status-draft";
            case PENDING:
                return "status-pending";
            case PUBLISHED:
                return "status-published";
            case ARCHIVED:
                return "status-archived";
            default:
                return "unknown";
        }
    }

    /**
     * 获取状态的颜色代码
     * 
     * @return 颜色代码
     */
    public String getColorCode() {
        switch (this) {
            case DRAFT:
                return "#d9d9d9";     // 灰色;
            case PENDING:
                return "#faad14";   // 橙色;
            case PUBLISHED:
                return "#52c41a"; // 绿色;
            case ARCHIVED:
                return "#8c8c8c";  // 深灰色;
            default:
                return "unknown";
        }
    }

    /**
     * 获取状态图标
     * 
     * @return 图标名称
     */
    public String getIcon() {
        switch (this) {
            case DRAFT:
                return "edit";
            case PENDING:
                return "clock-circle";
            case PUBLISHED:
                return "check-circle";
            case ARCHIVED:
                return "inbox";
            default:
                return "unknown";
        }
    }

    /**
     * 获取下一个可能的状态列表
     * 
     * @return 下一个可能的状态数组
     */
    public ContentStatus[] getNextPossibleStatuses() {
        switch (this) {
            case DRAFT:
                return new ContentStatus[]{PENDING, PUBLISHED}; // 草稿可以提交审核或直接发布
            case PENDING:
                return new ContentStatus[]{DRAFT, PUBLISHED, ARCHIVED}; // 待审核可以退回草稿、发布或归档
            case PUBLISHED:
                return new ContentStatus[]{DRAFT, ARCHIVED}; // 已发布可以退回草稿或归档
            case ARCHIVED:
                return new ContentStatus[]{DRAFT, PUBLISHED}; // 已归档可以恢复为草稿或发布
            default:
                return new ContentStatus[]{};
        }
    }

    /**
     * 判断是否可以转换到指定状态
     * 
     * @param targetStatus 目标状态
     * @return true 如果可以转换
     */
    public boolean canTransitionTo(ContentStatus targetStatus) {
        if (targetStatus == null || targetStatus == this) {
            return false;
        }
        
        ContentStatus[] possibleStatuses = getNextPossibleStatuses();
        for (ContentStatus status : possibleStatuses) {
            if (status == targetStatus) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
