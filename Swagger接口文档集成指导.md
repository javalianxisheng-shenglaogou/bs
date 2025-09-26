# Swagger接口文档集成指导

## 概述
本文档详细介绍如何在"基于Spring Boot的多站点内容管理系统"中集成Swagger/OpenAPI 3.0，实现在线接口文档的自动生成和测试功能。

---

## 1. 依赖配置

### 1.1 Maven依赖
在`pom.xml`中添加Swagger依赖：

```xml
<dependencies>
    <!-- Swagger/OpenAPI 3.0 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.1.0</version>
    </dependency>
    
    <!-- 如果使用Spring Security，需要额外配置 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-security</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

### 1.2 application.yml配置
```yaml
# Swagger配置
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
    try-it-out-enabled: true
    filter: true
    display-request-duration: true
  packages-to-scan: com.example.graduation.controller
  paths-to-match: /api/**
  show-actuator: false
```

---

## 2. Swagger配置类

### 2.1 基础配置类
```java
package com.example.graduation.config;

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

@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
    
    private Info apiInfo() {
        return new Info()
                .title("基于Spring Boot的多站点内容管理系统API")
                .description("多站点CMS系统的RESTful API文档，支持在线测试")
                .version("1.0.0")
                .contact(new Contact()
                        .name("姚奇奇")
                        .email("your-email@example.com")
                        .url("https://github.com/your-username"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
    
    private List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("开发环境"),
                new Server()
                        .url("https://api.example.com")
                        .description("生产环境")
        );
    }
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("请输入JWT token，格式：Bearer {token}");
    }
}
```

### 2.2 Spring Security集成配置
```java
package com.example.graduation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                // 允许Swagger相关路径无需认证
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
        );
        
        return http.build();
    }
}
```

---

## 3. 控制器注解使用

### 3.1 基础注解示例
```java
package com.example.graduation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户相关的API接口")
public class UserController {
    
    @Operation(
        summary = "创建用户",
        description = "创建新的用户账户，需要提供用户名、邮箱和密码"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "用户创建成功",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "用户名或邮箱已存在"
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Parameter(
                description = "用户创建信息",
                required = true,
                schema = @Schema(implementation = UserCreateDTO.class)
            )
            @Valid @RequestBody UserCreateDTO createDTO) {
        // 实现逻辑
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("用户创建成功", userResponseDTO));
    }
    
    @Operation(
        summary = "获取用户列表",
        description = "分页获取用户列表，支持关键字搜索"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<UserResponseDTO>>> getUsers(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") int page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            
            @Parameter(description = "排序方向", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,
            
            @Parameter(description = "搜索关键字，可搜索用户名、邮箱、昵称")
            @RequestParam(required = false) String keyword) {
        // 实现逻辑
        return successPage(responsePage);
    }
}
```

### 3.2 DTO类注解示例
```java
package com.example.graduation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户创建请求DTO")
public class UserCreateDTO {
    
    @Schema(description = "用户名", example = "john_doe", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    private String username;
    
    @Schema(description = "邮箱地址", example = "john@example.com", required = true)
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;
    
    @Schema(description = "密码", example = "password123", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
    
    @Schema(description = "昵称", example = "John Doe")
    private String nickname;
    
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
}

@Data
@Schema(description = "用户响应DTO")
public class UserResponseDTO {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "john_doe")
    private String username;
    
    @Schema(description = "邮箱地址", example = "john@example.com")
    private String email;
    
    @Schema(description = "昵称", example = "John Doe")
    private String nickname;
    
    @Schema(description = "用户角色", example = "USER")
    private String role;
    
    @Schema(description = "用户状态", example = "ACTIVE")
    private String status;
    
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;
}
```

---

## 4. 访问和使用

### 4.1 访问地址
- **Swagger UI界面**：http://localhost:8080/swagger-ui.html
- **API文档JSON**：http://localhost:8080/v3/api-docs
- **API文档YAML**：http://localhost:8080/v3/api-docs.yaml

### 4.2 使用步骤
1. **启动应用**：运行Spring Boot应用
2. **访问文档**：在浏览器中打开Swagger UI地址
3. **查看接口**：浏览所有可用的API接口
4. **测试接口**：
   - 点击接口展开详情
   - 点击"Try it out"按钮
   - 填写请求参数
   - 点击"Execute"执行请求
   - 查看响应结果

### 4.3 JWT认证测试
1. 首先调用登录接口获取JWT token
2. 点击页面右上角的"Authorize"按钮
3. 在弹出框中输入：`Bearer {your-jwt-token}`
4. 点击"Authorize"确认
5. 现在可以测试需要认证的接口

---

## 5. 最佳实践

### 5.1 文档编写规范
- **接口描述**：清晰描述接口功能和用途
- **参数说明**：详细说明每个参数的含义和格式
- **响应示例**：提供完整的响应数据示例
- **错误码说明**：列出可能的错误码和处理方式

### 5.2 注解使用建议
- 在Controller类上使用`@Tag`注解进行分组
- 在方法上使用`@Operation`注解描述接口功能
- 在参数上使用`@Parameter`注解说明参数含义
- 在DTO类上使用`@Schema`注解描述数据结构

### 5.3 安全考虑
- 生产环境可通过配置禁用Swagger UI
- 敏感信息不要在示例中暴露
- 合理配置访问权限

### 5.4 性能优化
- 合理配置扫描包路径，避免扫描不必要的类
- 在生产环境考虑禁用某些功能以提升性能

---

## 6. 故障排除

### 6.1 常见问题
1. **404错误**：检查路径配置和Spring Security配置
2. **接口不显示**：检查包扫描路径配置
3. **认证失败**：检查JWT token格式和有效性
4. **样式加载失败**：检查静态资源配置

### 6.2 调试技巧
- 查看控制台日志确认Swagger是否正确启动
- 检查`/v3/api-docs`端点是否可以正常访问
- 使用浏览器开发者工具检查网络请求

通过以上配置，您的多站点CMS系统将拥有完整的在线API文档，大大提升开发效率和接口测试便利性。
