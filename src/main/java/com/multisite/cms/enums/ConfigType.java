package com.multisite.cms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置类型枚举
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ConfigType {
    
    /**
     * 字符串类型
     */
    STRING("字符串", "文本字符串类型的配置值"),
    
    /**
     * 数字类型
     */
    NUMBER("数字", "数字类型的配置值，支持整数和小数"),
    
    /**
     * 布尔类型
     */
    BOOLEAN("布尔", "布尔类型的配置值，true或false"),
    
    /**
     * JSON类型
     */
    JSON("JSON", "JSON格式的配置值，用于复杂数据结构");

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据名称获取配置类型
     * 
     * @param name 类型名称
     * @return 配置类型枚举
     */
    public static ConfigType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return STRING; // 默认类型
        }
        
        for (ConfigType type : values()) {
            if (type.name().equalsIgnoreCase(name.trim()) || 
                type.getName().equals(name.trim())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的配置类型: " + name);
    }

    /**
     * 验证值是否符合类型要求
     * 
     * @param value 要验证的值
     * @return true 如果值符合类型要求
     */
    public boolean isValidValue(String value) {
        if (value == null) {
            return true; // 允许空值
        }
        
        switch (this) {
            case STRING:
                return true; // 字符串总是有效;
            case NUMBER:
                return isValidNumber(value);
            case BOOLEAN:
                return isValidBoolean(value);
            case JSON:
                return isValidJson(value);
            default:
                return false;
        }
    }

    /**
     * 验证是否为有效数字
     */
    private boolean isValidNumber(String value) {
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证是否为有效布尔值
     */
    private boolean isValidBoolean(String value) {
        String trimmed = value.trim().toLowerCase();
        return "true".equals(trimmed) || "false".equals(trimmed) ||
               "1".equals(trimmed) || "0".equals(trimmed) ||
               "yes".equals(trimmed) || "no".equals(trimmed) ||
               "on".equals(trimmed) || "off".equals(trimmed);
    }

    /**
     * 验证是否为有效JSON
     */
    private boolean isValidJson(String value) {
        try {
            // 简单的JSON格式验证
            String trimmed = value.trim();
            return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                   (trimmed.startsWith("[") && trimmed.endsWith("]")) ||
                   trimmed.startsWith("\"") && trimmed.endsWith("\"") ||
                   "null".equals(trimmed) ||
                   "true".equals(trimmed) || "false".equals(trimmed) ||
                   isValidNumber(trimmed);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取类型的默认值
     * 
     * @return 默认值字符串
     */
    public String getDefaultValue() {
        switch (this) {
            case STRING:
                return "";
            case NUMBER:
                return "0";
            case BOOLEAN:
                return "false";
            case JSON:
                return "{}";
            default:
                return "";
        }
    }

    /**
     * 获取类型的示例值
     * 
     * @return 示例值字符串
     */
    public String getExampleValue() {
        switch (this) {
            case STRING:
                return "示例文本";
            case NUMBER:
                return "123.45";
            case BOOLEAN:
                return "true";
            case JSON:
                return "{\"key\": \"value\"}";
            default:
                return "";
        }
    }

    /**
     * 获取前端输入组件类型
     * 
     * @return 前端组件类型
     */
    public String getInputType() {
        switch (this) {
            case STRING:
                return "text";
            case NUMBER:
                return "number";
            case BOOLEAN:
                return "switch";
            case JSON:
                return "textarea";
            default:
                return "unknown";
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
