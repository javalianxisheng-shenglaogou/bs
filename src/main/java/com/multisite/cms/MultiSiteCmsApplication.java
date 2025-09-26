package com.multisite.cms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 基于Spring Boot的多站点内容管理系统主应用类
 * 
 * 功能特性：
 * - 多站点管理
 * - 内容共享与同步
 * - 发布工作流
 * - 版本控制
 * - 多语言支持
 * - 在线接口文档
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class MultiSiteCmsApplication {

    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(MultiSiteCmsApplication.class);
            
            // 设置默认配置
            app.setAdditionalProfiles("dev");
            
            // 启动应用
            var context = app.run(args);
            
            // 获取应用信息
            String appName = context.getEnvironment().getProperty("app.name");
            String appVersion = context.getEnvironment().getProperty("app.version");
            String serverPort = context.getEnvironment().getProperty("server.port");
            String contextPath = context.getEnvironment().getProperty("server.servlet.context-path");
            String activeProfile = String.join(",", context.getEnvironment().getActiveProfiles());
            
            log.info("=".repeat(80));
            log.info("🚀 {} v{} 启动成功!", appName, appVersion);
            log.info("📝 当前环境: {}", activeProfile);
            log.info("🌐 访问地址: http://localhost:{}{}", serverPort, contextPath);
            log.info("📚 接口文档: http://localhost:{}{}/swagger-ui.html", serverPort, contextPath);
            log.info("💾 数据库: MySQL");
            log.info("🔧 技术栈: Spring Boot 3 + JPA + Security + Swagger");
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("❌ 应用启动失败: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
