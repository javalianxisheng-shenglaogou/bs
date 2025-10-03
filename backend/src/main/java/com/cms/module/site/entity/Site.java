package com.cms.module.site.entity;

import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * 站点实体
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sites")
public class Site extends BaseEntity {

    /**
     * 站点名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 站点代码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * 站点域名
     */
    @Column(nullable = false, length = 255)
    private String domain;

    /**
     * 站点描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Logo URL
     */
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /**
     * Favicon URL
     */
    @Column(name = "favicon_url", length = 500)
    private String faviconUrl;

    /**
     * 默认语言
     */
    @Column(length = 10)
    private String language = "zh_CN";

    /**
     * 时区
     */
    @Column(length = 50)
    private String timezone = "Asia/Shanghai";

    /**
     * 站点状态
     */
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE'")
    private String status = "ACTIVE";

    /**
     * 是否默认站点
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    /**
     * SEO标题
     */
    @Column(name = "seo_title", length = 200)
    private String seoTitle;

    /**
     * SEO关键词
     */
    @Column(name = "seo_keywords", length = 500)
    private String seoKeywords;

    /**
     * SEO描述
     */
    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;

    /**
     * 联系邮箱
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * 联系电话
     */
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    /**
     * 联系地址
     */
    @Column(name = "contact_address", length = 500)
    private String contactAddress;
}

