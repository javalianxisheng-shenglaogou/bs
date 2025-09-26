package com.multisite.cms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 站点状态枚举
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SiteStatus {
    
    /**
     * 激活状态 - 站点正常运行
     */
    ACTIVE("激活", "站点正常运行，用户可以正常访问"),
    
    /**
     * 未激活状态 - 站点未激活，无法访问
     */
    INACTIVE("未激活", "站点未激活，用户无法访问"),
    
    /**
     * 维护状态 - 站点维护中，暂时无法访问
     */
    MAINTENANCE("维护中", "站点正在维护，暂时无法访问");

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据名称获取状态
     * 
     * @param name 状态名称
     * @return 站点状态枚举
     */
    public static SiteStatus fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ACTIVE; // 默认状态
        }
        
        for (SiteStatus status : values()) {
            if (status.name().equalsIgnoreCase(name.trim()) || 
                status.getName().equals(name.trim())) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的站点状态: " + name);
    }

    /**
     * 判断是否为可访问状态
     * 
     * @return true 如果站点可以访问
     */
    public boolean isAccessible() {
        return this == ACTIVE;
    }

    /**
     * 判断是否为不可访问状态
     * 
     * @return true 如果站点不能访问
     */
    public boolean isInaccessible() {
        return !isAccessible();
    }

    /**
     * 获取状态的CSS样式类
     * 
     * @return CSS样式类名
     */
    public String getCssClass() {
        switch (this) {
            case ACTIVE:
                return "status-active";
            case INACTIVE:
                return "status-inactive";
            case MAINTENANCE:
                return "status-maintenance";
            default:
                return "status-unknown";
        }
    }

    /**
     * 获取状态的颜色代码
     * 
     * @return 颜色代码
     */
    public String getColorCode() {
        switch (this) {
            case ACTIVE:
                return "#52c41a";      // 绿色
            case INACTIVE:
                return "#d9d9d9";    // 灰色
            case MAINTENANCE:
                return "#faad14"; // 橙色
            default:
                return "#000000";
        }
    }

    /**
     * 获取状态图标
     * 
     * @return 图标名称
     */
    public String getIcon() {
        switch (this) {
            case ACTIVE:
                return "check-circle";
            case INACTIVE:
                return "stop-circle";
            case MAINTENANCE:
                return "tool";
            default:
                return "question-circle";
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
