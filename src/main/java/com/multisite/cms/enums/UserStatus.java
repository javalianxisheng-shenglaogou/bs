package com.multisite.cms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
    
    /**
     * 激活状态 - 用户可以正常使用系统
     */
    ACTIVE("激活", "用户账户处于正常激活状态，可以正常使用系统功能"),
    
    /**
     * 未激活状态 - 用户账户未激活，无法登录
     */
    INACTIVE("未激活", "用户账户未激活，需要激活后才能使用系统"),
    
    /**
     * 锁定状态 - 用户账户被锁定，无法登录
     */
    LOCKED("锁定", "用户账户被锁定，无法登录系统，需要管理员解锁");

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
     * @return 用户状态枚举
     */
    public static UserStatus fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ACTIVE; // 默认状态
        }
        
        for (UserStatus status : values()) {
            if (status.name().equalsIgnoreCase(name.trim()) || 
                status.getName().equals(name.trim())) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("未知的用户状态: " + name);
    }

    /**
     * 判断是否为有效状态（可以登录）
     * 
     * @return true 如果用户可以登录
     */
    public boolean isValid() {
        return this == ACTIVE;
    }

    /**
     * 判断是否为无效状态（不能登录）
     * 
     * @return true 如果用户不能登录
     */
    public boolean isInvalid() {
        return !isValid();
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
            case LOCKED:
                return "status-locked";
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
            case ACTIVE:
                return "#52c41a";    // 绿色;
            case INACTIVE:
                return "#faad14";  // 橙色;
            case LOCKED:
                return "#ff4d4f";    // 红色;
            default:
                return "unknown";
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
