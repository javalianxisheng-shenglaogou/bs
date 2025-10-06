package com.cms.module.content.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 内容版本操作日志实体
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "content_version_logs", indexes = {
        @Index(name = "idx_content_id", columnList = "content_id"),
        @Index(name = "idx_version_id", columnList = "version_id"),
        @Index(name = "idx_operator_id", columnList = "operator_id"),
        @Index(name = "idx_operation_type", columnList = "operation_type"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class ContentVersionLog {

    /**
     * 日志ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 内容ID
     */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    /**
     * 版本ID
     */
    @Column(name = "version_id")
    private Long versionId;

    /**
     * 操作类型（CREATE/VIEW/RESTORE/DELETE/TAG/COMPARE）
     */
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    /**
     * 操作人ID
     */
    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @Column(name = "operator_name", length = 100)
    private String operatorName;

    /**
     * 操作详情（JSON对象）
     */
    @Column(name = "operation_detail", columnDefinition = "JSON")
    private String operationDetail;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 操作时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 在保存前设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

