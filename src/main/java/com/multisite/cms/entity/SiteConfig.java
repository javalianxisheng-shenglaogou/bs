package com.multisite.cms.entity;

import com.multisite.cms.enums.ConfigType;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 站点配置实体类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "site_configs", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_site_config", columnNames = {"site_id", "config_key"})
    },
    indexes = {
        @Index(name = "idx_site_id", columnList = "site_id"),
        @Index(name = "idx_config_key", columnList = "config_key")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"site"})
@ToString(callSuper = true, exclude = {"site"})
public class SiteConfig extends BaseEntity {

    /**
     * 所属站点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, foreignKey = @ForeignKey(name = "fk_site_configs_site"))
    private Site site;

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100")
    @Column(name = "config_key", length = 100, nullable = false)
    private String configKey;

    /**
     * 配置值
     */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    /**
     * 配置类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "config_type", nullable = false)
    @Builder.Default
    private ConfigType configType = ConfigType.STRING;

    /**
     * 配置描述
     */
    @Size(max = 255, message = "配置描述长度不能超过255")
    @Column(name = "description")
    private String description;

    /**
     * 是否公开配置（前端可见）
     */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = Boolean.FALSE;

    /**
     * 判断是否为公开配置
     */
    @Transient
    public boolean isPublicConfig() {
        return Boolean.TRUE.equals(this.isPublic);
    }

    /**
     * 判断是否为私有配置
     */
    @Transient
    public boolean isPrivateConfig() {
        return !isPublicConfig();
    }

    /**
     * 获取字符串值
     */
    @Transient
    public String getStringValue() {
        return this.configValue;
    }

    /**
     * 获取整数值
     */
    @Transient
    public Integer getIntegerValue() {
        if (this.configValue == null || this.configValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(this.configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取长整数值
     */
    @Transient
    public Long getLongValue() {
        if (this.configValue == null || this.configValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(this.configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取布尔值
     */
    @Transient
    public Boolean getBooleanValue() {
        if (this.configValue == null || this.configValue.trim().isEmpty()) {
            return null;
        }
        String value = this.configValue.trim().toLowerCase();
        return "true".equals(value) || "1".equals(value) || "yes".equals(value) || "on".equals(value);
    }

    /**
     * 获取双精度值
     */
    @Transient
    public Double getDoubleValue() {
        if (this.configValue == null || this.configValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(this.configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 设置字符串值
     */
    public void setStringValue(String value) {
        this.configValue = value;
        this.configType = ConfigType.STRING;
    }

    /**
     * 设置整数值
     */
    public void setIntegerValue(Integer value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = ConfigType.NUMBER;
    }

    /**
     * 设置长整数值
     */
    public void setLongValue(Long value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = ConfigType.NUMBER;
    }

    /**
     * 设置布尔值
     */
    public void setBooleanValue(Boolean value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = ConfigType.BOOLEAN;
    }

    /**
     * 设置双精度值
     */
    public void setDoubleValue(Double value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = ConfigType.NUMBER;
    }

    /**
     * 设置JSON值
     */
    public void setJsonValue(String jsonValue) {
        this.configValue = jsonValue;
        this.configType = ConfigType.JSON;
    }

    /**
     * 根据类型获取值
     */
    @Transient
    public Object getTypedValue() {
        switch (this.configType) {
            case STRING:
                return getStringValue();
            case NUMBER:
                // 尝试解析为整数，如果失败则解析为双精度
                Integer intValue = getIntegerValue();
                if (intValue != null) {
                    return intValue;
                }
                return getDoubleValue();
            case BOOLEAN:
                return getBooleanValue();
            case JSON:
                return getStringValue(); // JSON作为字符串返回
            default:
                return getStringValue();
        }
    }

    /**
     * 验证配置值是否有效
     */
    @Transient
    public boolean isValidValue() {
        if (this.configValue == null) {
            return true; // 允许空值
        }
        
        switch (this.configType) {
            case STRING:
            case JSON:
                return true; // 字符串和JSON总是有效
            case NUMBER:
                return getDoubleValue() != null; // 能解析为数字
            case BOOLEAN:
                return true; // 布尔值总是有效（会转换为false如果无法解析）
            default:
                return true;
        }
    }
}
