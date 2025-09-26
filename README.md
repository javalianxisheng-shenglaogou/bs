# 基于Spring Boot的多站点内容管理系统

## 项目简介

这是一个基于Spring Boot 3开发的多站点内容管理系统，支持多站点管理、内容共享与同步、发布工作流、版本控制、多语言支持等功能。

## 技术栈

### 后端技术
- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问
- **MySQL 8.0** - 数据库
- **JWT** - 身份认证
- **Swagger/OpenAPI 3.0** - API文档
- **Lombok** - 代码简化
- **HikariCP** - 连接池

### 前端技术（计划）
- **Vue.js 3** - 前端框架
- **Element Plus** - UI组件库
- **Pinia** - 状态管理
- **Vue Router** - 路由管理

## 核心功能

### 1. 多站点管理
- 站点创建、配置、管理
- 站点层级结构支持
- 站点级权限控制
- 灵活的站点配置系统

### 2. 内容管理
- 文章、页面、媒体内容管理
- 内容分类和标签
- 富文本编辑器
- SEO优化支持

### 3. 用户权限管理
- 多级角色权限体系
- 全局角色和站点级角色
- 细粒度权限控制
- 用户状态管理

### 4. 内容发布工作流
- 草稿、待审核、已发布状态
- 定时发布功能
- 内容版本控制
- 审核流程管理

### 5. API文档
- 完整的Swagger文档
- 在线API测试
- JWT认证集成
- 详细的接口说明

## 快速开始

### 环境要求

- **JDK 17 LTS** 或更高版本
- **Maven 3.8+**
- **MySQL 8.0**
- **Node.js 18+ LTS**（前端开发）

### 1. 克隆项目

```bash
git clone <repository-url>
cd multi-site-cms
```

### 2. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE multi_site_cms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本：
```bash
# 按顺序执行以下SQL文件
mysql -u root -p multi_site_cms < database/01_create_database.sql
mysql -u root -p multi_site_cms < database/02_create_tables.sql
mysql -u root -p multi_site_cms < database/03_create_content_tables.sql
mysql -u root -p multi_site_cms < database/04_create_sharing_i18n_tables.sql
mysql -u root -p multi_site_cms < database/05_insert_initial_data.sql
```

### 3. 配置应用

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/multi_site_cms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8
    username: your_username
    password: your_password
```

### 4. 启动应用

#### 方式一：使用启动脚本（Windows）
```bash
start.bat
```

#### 方式二：使用Maven命令
```bash
mvn spring-boot:run
```

#### 方式三：使用IDE
直接运行 `MultiSiteCmsApplication.java` 主类

### 5. 访问应用

- **应用首页**: http://localhost:8080
- **Swagger文档**: http://localhost:8080/swagger-ui/index.html
- **API文档**: http://localhost:8080/v3/api-docs

## API使用指南

### 认证流程

1. **用户注册**
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "displayName": "测试用户"
}
```

2. **用户登录**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

3. **使用JWT令牌**
```bash
Authorization: Bearer <your-jwt-token>
```

### 主要API端点

#### 用户管理
- `GET /api/users` - 获取用户列表
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

#### 站点管理
- `GET /api/sites` - 获取站点列表
- `POST /api/sites` - 创建站点
- `PUT /api/sites/{id}` - 更新站点
- `DELETE /api/sites/{id}` - 删除站点

#### 内容管理
- `GET /api/contents` - 获取内容列表
- `POST /api/contents` - 创建内容
- `PUT /api/contents/{id}` - 更新内容
- `DELETE /api/contents/{id}` - 删除内容

#### 分类管理
- `GET /api/categories` - 获取分类列表
- `POST /api/categories` - 创建分类
- `PUT /api/categories/{id}` - 更新分类
- `DELETE /api/categories/{id}` - 删除分类

## 默认账户

系统初始化后会创建以下默认账户：

- **超级管理员**
  - 用户名: `admin`
  - 密码: `admin123`
  - 邮箱: `admin@example.com`

- **编辑用户**
  - 用户名: `editor`
  - 密码: `editor123`
  - 邮箱: `editor@example.com`

- **作者用户**
  - 用户名: `author`
  - 密码: `author123`
  - 邮箱: `author@example.com`

## 项目结构

```
src/
├── main/
│   ├── java/com/multisite/cms/
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── dto/            # 数据传输对象
│   │   ├── entity/         # 实体类
│   │   ├── enums/          # 枚举类
│   │   ├── exception/      # 异常处理
│   │   ├── repository/     # 数据访问层
│   │   ├── security/       # 安全配置
│   │   ├── service/        # 业务逻辑层
│   │   └── common/         # 通用组件
│   └── resources/
│       ├── application.yml # 应用配置
│       └── static/         # 静态资源
├── database/               # 数据库脚本
└── docs/                  # 项目文档
```

## 开发指南

### 代码规范
- 使用Lombok减少样板代码
- 遵循RESTful API设计原则
- 使用Swagger注解完善API文档
- 统一异常处理和响应格式
- 使用事务管理确保数据一致性

### 数据库设计
- 使用软删除机制
- 添加审计字段（创建时间、更新时间、创建者、更新者）
- 合理设计索引提升查询性能
- 使用外键约束保证数据完整性

### 安全考虑
- JWT令牌认证
- 密码BCrypt加密
- 角色级权限控制
- SQL注入防护
- XSS攻击防护

## 部署指南

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 打包应用
mvn clean package -DskipTests

# 运行JAR包
java -jar target/multi-site-cms-1.0.0.jar
```

### Docker部署
```dockerfile
# Dockerfile示例
FROM openjdk:17-jdk-slim
COPY target/multi-site-cms-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 作者: 姚奇奇
- 邮箱: your-email@example.com
- 项目链接: [GitHub Repository](https://github.com/your-username/multi-site-cms)

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础的多站点管理功能
- 完成用户权限管理系统
- 集成Swagger API文档
- 实现JWT认证机制
