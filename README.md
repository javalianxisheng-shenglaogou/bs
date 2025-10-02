# 多站点内容管理系统（Multi-Site CMS）

## 项目简介

这是一个基于Spring Boot和Vue.js开发的多站点内容管理系统，支持多站点管理、内容共享、权限隔离、工作流审批等功能。

## 技术栈

### 后端
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA
- MySQL 8.0+
- JWT认证
- Flyway数据库迁移
- Swagger API文档
- Lombok
- MapStruct

### 前端
- Vue.js 3.3+
- TypeScript 5.0+
- Vite 5.4 (兼容Node.js 18)
- Element Plus 2.8+
- Pinia 2.1+
- Vue Router 4.2+
- Axios 1.7+

## 项目结构

```
multi-site-hub/
├── backend/           # 后端Spring Boot项目
├── frontend/          # 前端Vue.js项目
├── docs/              # 项目文档
└── README.md
```

## 快速开始

### 环境要求

- JDK 11+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

访问：
- API接口：http://localhost:8080/api/test/health
- Swagger文档：http://localhost:8080/api/swagger-ui.html

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

访问前端页面：http://localhost:3000

### 数据库配置

1. 创建数据库：
```bash
mysql -uroot -p123456 < database-setup.sql
```

或手动创建：
```sql
CREATE DATABASE multi_site_cms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 数据库配置（已在 `backend/src/main/resources/application-dev.yml` 中配置）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/multi_site_cms
    username: root
    password: 123456
```

3. 启动后端项目，Flyway会自动执行数据库迁移并插入测试数据

## 核心功能

- ✅ 用户权限管理（RBAC）
- ✅ 多站点管理
- ✅ 内容管理（文章、页面、新闻等）
- ✅ 分类和标签管理
- ✅ 工作流审批
- ✅ 版本控制
- ✅ 多语言支持
- ✅ 内容共享
- ✅ 数据统计分析
- ✅ 媒体文件管理

## 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | admin | admin123 | 拥有所有权限 |
| 站点管理员 | siteadmin | admin123 | 管理指定站点 |
| 编辑者 | editor1 | admin123 | 创建和编辑内容 |
| 审核者 | reviewer1 | admin123 | 审核内容 |
| 翻译者 | translator1 | admin123 | 翻译内容 |

## 测试数据

系统已自动初始化以下测试数据：
- 6个角色，60+权限
- 26个测试用户
- 8个站点
- 40个分类
- 120个标签
- 250+篇内容
- 4个工作流定义
- 50个媒体文件

## 开发规范

- 遵循阿里巴巴Java开发规范
- 使用ESLint和Prettier格式化代码
- Git提交遵循Conventional Commits规范
- 代码审查后合并到主分支

## 文档

- [需求文档](./文档/需求文档.md)
- [API接口文档](./文档/API接口文档.md)

## 许可证

MIT License

## 作者

毕业设计项目

