package com.cms.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT密钥
     */
    private String secret = "multi-site-cms-secret-key-change-in-production-environment-2025";

    /**
     * token有效期（毫秒）默认7天
     */
    private Long expiration = 7 * 24 * 60 * 60 * 1000L;

    /**
     * token前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * token请求头名称
     */
    private String header = "Authorization";
}

