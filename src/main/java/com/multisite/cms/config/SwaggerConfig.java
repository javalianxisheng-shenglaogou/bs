package com.multisite.cms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 3.0 配置类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    
    @Value("${app.name:基于Spring Boot的多站点内容管理系统}")
    private String appName;
    
    @Value("${app.version:1.0.0}")
    private String appVersion;
    
    @Value("${app.description:支持多站点管理、内容共享、工作流审批的现代化CMS系统}")
    private String appDescription;

    /**
     * 创建OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createApiServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }
    
    /**
     * 创建API信息
     */
    private Info createApiInfo() {
        return new Info()
                .title(appName + " API")
                .description(appDescription + "\n\n" + 
                    "## 主要功能\n" +
                    "- 🏢 **多站点管理**：支持创建和管理多个独立站点\n" +
                    "- 📄 **内容管理**：强大的内容创建、编辑和发布功能\n" +
                    "- 🔄 **内容共享**：跨站点内容引用和实时同步\n" +
                    "- 🔀 **工作流审批**：可配置的多层级审批流程\n" +
                    "- 📝 **版本控制**：完整的内容版本管理和回滚\n" +
                    "- 🌍 **多语言支持**：统一的多语言内容管理框架\n" +
                    "- 👥 **权限管理**：细粒度的用户权限控制\n" +
                    "- 📊 **在线文档**：完整的API文档和在线测试\n\n" +
                    "## 技术栈\n" +
                    "- **后端**：Spring Boot 3 + Spring Security + JPA + MySQL\n" +
                    "- **前端**：Vue.js 3 + Element Plus + Pinia\n" +
                    "- **文档**：Swagger/OpenAPI 3.0\n\n" +
                    "## 认证说明\n" +
                    "本API使用JWT Bearer Token进行身份认证。请先调用登录接口获取token，然后在请求头中添加：\n" +
                    "```\n" +
                    "Authorization: Bearer {your-jwt-token}\n" +
                    "```")
                .version(appVersion)
                .contact(new Contact()
                        .name("姚奇奇")
                        .email("your-email@example.com")
                        .url("https://github.com/your-username"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
    
    /**
     * 创建API服务器列表
     */
    private List<Server> createApiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort + contextPath)
                        .description("开发环境"),
                new Server()
                        .url("https://api.example.com" + contextPath)
                        .description("生产环境"),
                new Server()
                        .url("https://test-api.example.com" + contextPath)
                        .description("测试环境")
        );
    }
    
    /**
     * 创建安全认证方案
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请输入JWT token，格式：Bearer {token}\n\n" +
                    "获取token的步骤：\n" +
                    "1. 调用 `/auth/login` 接口进行登录\n" +
                    "2. 从响应中获取 `accessToken`\n" +
                    "3. 在此处输入：`Bearer {accessToken}`\n" +
                    "4. 点击 Authorize 按钮完成认证\n\n" +
                    "默认管理员账户：\n" +
                    "- 用户名：admin\n" +
                    "- 密码：admin123");
    }
}
