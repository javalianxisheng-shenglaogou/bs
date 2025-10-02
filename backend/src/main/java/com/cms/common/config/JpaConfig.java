package com.cms.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * JPA配置
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    /**
     * 审计功能 - 自动填充创建人和更新人
     * TODO: 后续从Security Context中获取当前用户ID
     */
    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.of(1L); // 暂时返回固定值，后续从认证信息中获取
    }
}

