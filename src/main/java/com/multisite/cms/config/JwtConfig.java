package com.multisite.cms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT密钥 - 必须至少64字节用于HS512算法
     */
    private String secret = "multiSiteCmsSecretKey2024ForJWTTokenGenerationWithLongerKeyForHS512AlgorithmSecurity";

    /**
     * JWT过期时间（毫秒）
     */
    private Long expiration = 604800000L; // 7天

    /**
     * 刷新令牌过期时间（毫秒）
     */
    private Long refreshExpiration = 2592000000L; // 30天

    /**
     * JWT令牌前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * JWT请求头名称
     */
    private String headerName = "Authorization";

    /**
     * JWT发行者
     */
    private String issuer = "multi-site-cms";

    /**
     * JWT受众
     */
    private String audience = "multi-site-cms-users";
}
