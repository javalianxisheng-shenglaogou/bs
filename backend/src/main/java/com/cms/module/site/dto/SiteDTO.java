package com.cms.module.site.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 站点DTO
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
public class SiteDTO {

    /**
     * 站点ID
     */
    private Long id;

    /**
     * 站点名称
     */
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 100, message = "站点名称长度不能超过100")
    private String name;

    /**
     * 站点代码
     */
    @NotBlank(message = "站点代码不能为空")
    @Size(max = 50, message = "站点代码长度不能超过50")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "站点代码只能包含小写字母、数字、下划线和连字符")
    private String code;

    /**
     * 站点域名
     */
    @NotBlank(message = "站点域名不能为空")
    @Size(max = 255, message = "站点域名长度不能超过255")
    private String domain;

    /**
     * 站点描述
     */
    private String description;

    /**
     * Logo URL
     */
    private String logoUrl;

    /**
     * Favicon URL
     */
    private String faviconUrl;

    /**
     * 默认语言
     */
    private String language;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 站点状态
     */
    private String status;

    /**
     * 是否默认站点
     */
    private Boolean isDefault;

    /**
     * SEO标题
     */
    private String seoTitle;

    /**
     * SEO关键词
     */
    private String seoKeywords;

    /**
     * SEO描述
     */
    private String seoDescription;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 版本号
     */
    private Integer version;
}

