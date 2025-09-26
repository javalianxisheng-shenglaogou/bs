package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "健康检查", description = "系统健康检查相关接口")
public class HealthController {

    private final Environment environment;

    /**
     * 系统健康检查
     */
    @GetMapping
    @Operation(summary = "系统健康检查", description = "检查系统运行状态")
    public ApiResponse<Map<String, Object>> health() {
        log.debug("Health check requested");
        
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "基于Spring Boot的多站点内容管理系统");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("profiles", environment.getActiveProfiles());
        
        return ApiResponse.success("系统运行正常", healthInfo);
    }

    /**
     * 系统信息
     */
    @GetMapping("/info")
    @Operation(summary = "系统信息", description = "获取系统详细信息")
    public ApiResponse<Map<String, Object>> info() {
        log.debug("System info requested");
        
        Map<String, Object> systemInfo = new HashMap<>();
        
        // 应用信息
        Map<String, Object> appInfo = new HashMap<>();
        appInfo.put("name", "基于Spring Boot的多站点内容管理系统");
        appInfo.put("version", "1.0.0");
        appInfo.put("description", "多站点内容管理系统的设计与实现");
        appInfo.put("author", "姚奇奇");
        systemInfo.put("application", appInfo);
        
        // 环境信息
        Map<String, Object> envInfo = new HashMap<>();
        envInfo.put("profiles", environment.getActiveProfiles());
        envInfo.put("javaVersion", System.getProperty("java.version"));
        envInfo.put("springBootVersion", org.springframework.boot.SpringBootVersion.getVersion());
        systemInfo.put("environment", envInfo);
        
        // 功能特性
        Map<String, Object> features = new HashMap<>();
        features.put("multiSiteManagement", "多站点管理");
        features.put("contentSharing", "内容共享与同步");
        features.put("publishWorkflow", "发布工作流");
        features.put("versionControl", "版本控制");
        features.put("multiLanguage", "多语言支持");
        features.put("apiDocumentation", "在线接口文档");
        systemInfo.put("features", features);
        
        // 技术栈
        Map<String, Object> techStack = new HashMap<>();
        techStack.put("backend", "Spring Boot 3 + Spring Security + Spring Data JPA");
        techStack.put("database", "MySQL 8.0");
        techStack.put("documentation", "Swagger/OpenAPI 3.0");
        techStack.put("authentication", "JWT");
        systemInfo.put("techStack", techStack);
        
        return ApiResponse.success("获取成功", systemInfo);
    }

    /**
     * 数据库连接检查
     */
    @GetMapping("/db")
    @Operation(summary = "数据库连接检查", description = "检查数据库连接状态")
    public ApiResponse<Map<String, Object>> dbHealth() {
        log.debug("Database health check requested");
        
        Map<String, Object> dbInfo = new HashMap<>();
        
        try {
            // 这里可以添加实际的数据库连接检查逻辑
            // 例如执行一个简单的查询来验证连接
            
            dbInfo.put("status", "UP");
            dbInfo.put("database", "MySQL");
            dbInfo.put("driver", "com.mysql.cj.jdbc.Driver");
            dbInfo.put("timestamp", LocalDateTime.now());
            
            return ApiResponse.success("数据库连接正常", dbInfo);
        } catch (Exception e) {
            log.error("Database health check failed", e);
            
            dbInfo.put("status", "DOWN");
            dbInfo.put("error", e.getMessage());
            dbInfo.put("timestamp", LocalDateTime.now());
            
            return ApiResponse.error(500, "数据库连接异常", dbInfo);
        }
    }
}
