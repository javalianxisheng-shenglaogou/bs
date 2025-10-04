# 多站点内容管理系统（Multi-Site CMS）

## 项目简介

这是一个基于Spring Boot和Vue 3开发的现代化多站点内容管理系统，支持多站点管理、内容发布、分类管理、用户权限控制、文件上传、系统日志等功能。

### 主要特性

- ✅ **多站点管理** - 支持创建和管理多个独立站点,每个站点独立配置
- ✅ **内容管理** - 富文本编辑器、图片上传、内容分类、状态管理
- ✅ **用户权限** - 基于RBAC的细粒度权限控制
- ✅ **分类管理** - 树形分类结构、无限级分类、排序和可见性控制
- ✅ **文件上传** - 支持图片、文档等文件上传,自动生成缩略图
- ✅ **系统日志** - 实时查看系统运行日志,支持筛选和搜索
- ✅ **响应式设计** - 支持PC和移动端访问

## 技术栈

### 后端技术

- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **安全**: Spring Security + JWT
- **数据库迁移**: Flyway
- **日志**: Logback (按天分割)
- **API文档**: Swagger/OpenAPI 3.0
- **工具**: Lombok, MapStruct

### 前端技术

- **框架**: Vue 3.3 + TypeScript 5.0
- **构建工具**: Vite 5.4
- **UI组件**: Element Plus 2.8
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.2
- **HTTP客户端**: Axios 1.7
- **富文本编辑器**: Quill (@vueup/vue-quill)

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

## 已实现功能

### 后端（已完成）
- ✅ JWT认证和授权
- ✅ 用户登录接口（POST /api/auth/login）
- ✅ 获取用户信息接口（GET /api/auth/me）
- ✅ 退出登录接口（POST /api/auth/logout）
- ✅ 用户管理API（完整CRUD）
  - GET /api/users - 获取用户列表（分页、搜索、筛选）
  - GET /api/users/{id} - 获取用户详情
  - POST /api/users - 创建用户
  - PUT /api/users/{id} - 更新用户
  - DELETE /api/users/{id} - 删除用户
  - PUT /api/users/{id}/avatar - 更新用户头像
- ✅ 文件上传API
  - POST /api/files/avatar - 上传头像（最大5MB，支持JPG/PNG/GIF/WEBP）
  - POST /api/files/image - 上传图片（最大5MB）
  - POST /api/files/upload - 上传文件（最大10MB）
- ✅ 静态资源访问（/files/**）
- ✅ Spring Security配置
- ✅ CORS跨域配置
- ✅ 权限控制（@PreAuthorize）
- ✅ 数据库迁移（Flyway）
- ✅ 测试数据初始化

### 前端（已完成）
- ✅ 用户登录页面
- ✅ 真实API集成
- ✅ Token自动管理
- ✅ 路由守卫
- ✅ 主布局（侧边栏、顶部栏）
- ✅ 仪表盘页面
- ✅ 用户管理页面（完整CRUD功能）
  - 用户列表（表格、分页）
  - 关键词搜索（用户名、邮箱、昵称）
  - 状态筛选
  - 新增用户（表单验证）
  - 编辑用户
  - 删除用户（确认对话框）
  - 角色分配
- ✅ 个人中心页面
  - 查看个人信息
  - 编辑个人资料
  - 头像上传和显示
  - 修改密码表单（待实现）
  - 显示角色和权限
- ✅ 文件上传功能
  - 头像上传组件（el-upload）
  - 文件类型验证
  - 文件大小验证
  - 上传进度显示
  - 实时预览
- ✅ 用户头像显示（顶部栏、个人中心）
- ✅ 站点管理页面UI（卡片展示）
- ✅ 内容管理页面UI（表格、筛选）
- ✅ 响应式设计
- ✅ TypeScript类型支持

## 计划功能

- ✅ 用户管理CRUD API（已完成）
- ✅ 文件上传功能（已完成）
- 🔄 站点管理CRUD API
- 🔄 内容管理CRUD API
- 🔄 分类和标签管理
- 🔄 工作流审批
- 🔄 版本控制
- 🔄 多语言支持
- 🔄 内容共享
- 🔄 数据统计分析
- 🔄 媒体文件管理（文件列表、删除等）

## 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | admin | password | 拥有所有权限 |
| 站点管理员 | siteadmin | password | 管理指定站点 |
| 编辑者 | editor1 | password | 创建和编辑内容 |
| 审核者 | reviewer1 | password | 审核内容 |
| 翻译者 | translator1 | password | 翻译内容 |

**注意：** 密码已从 `admin123` 更新为 `password`（BCrypt加密）

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

## 更新日志

### 2025-01-14

#### v0.6.0 - 站点管理功能和头像上传修复
- ✅ 修复头像上传功能
  - 创建WebMvcConfig配置类
  - 配置静态资源映射（/files/** -> uploads/）
  - 配置CORS跨域访问
  - 修复文件上传后无法访问的问题
- ✅ 实现站点管理后端API
  - SiteController（站点CRUD接口）
  - SiteService（站点业务逻辑）
  - SiteRepository（站点数据访问）
  - SiteDTO、SiteQueryDTO（数据传输对象）
  - Site实体类
- ✅ 配置日志系统
  - 创建logback-spring.xml配置
  - 按日期分割日志文件
  - 分级别存储日志（INFO、ERROR、DEBUG、SQL）
  - 日志保留策略（INFO/ERROR 30天，DEBUG/SQL 7天）
- ✅ 创建任务管理系统
  - .tasks文件夹存储开发任务记录
  - 任务文件格式：YYYY-MM-DD_n_task-name.md
  - 记录问题分析、解决方案、进度跟踪
- ✅ 更新.gitignore
  - 添加logs/目录忽略
  - 添加uploads/目录忽略

### 2025-10-03

#### v0.5.0 - 文件上传功能
- ✅ 实现文件上传后端API
  - FileController（头像、图片、文件上传）
  - FileService（文件验证、保存、URL生成）
  - FileUploadResponse DTO
- ✅ 实现用户头像上传和显示
  - 头像上传组件（el-upload）
  - 文件类型验证（JPG/PNG/GIF/WEBP）
  - 文件大小验证（最大5MB）
  - 实时预览
  - 圆形头像显示
- ✅ 更新个人中心页面
  - 头像上传区域
  - 头像预览
  - 上传成功/失败提示
- ✅ 更新主布局
  - 顶部栏显示用户头像
  - 头像+用户名布局
- ✅ 配置静态资源访问
  - /files/** 映射到 uploads/ 目录
  - 文件按日期分类存储（yyyy/MM/dd）
  - UUID文件名防止冲突

#### v0.4.0 - 个人中心功能和错误修复
- ✅ 修复user_roles表granted_by字段错误
- ✅ 实现个人中心页面
- ✅ 查看和编辑个人信息
- ✅ 修改密码表单（待实现）
- ✅ 显示角色和权限
- ✅ 显示注册时间和最后登录时间

#### v0.3.0 - 用户管理功能完成
- ✅ 实现用户管理后端API（完整CRUD）
- ✅ 前端集成用户管理真实API
- ✅ 创建UserDialog组件（新增/编辑用户）
- ✅ 实现表单验证
- ✅ 实现角色分配功能
- ✅ 实现搜索和筛选功能
- ✅ 实现分页功能
- ✅ 完整的错误处理和提示

### 2025-10-02

#### v0.2.0 - 用户认证和前端UI完善
- ✅ 实现JWT认证框架
- ✅ 实现用户登录、获取用户信息、退出登录接口
- ✅ 前端集成真实登录API
- ✅ 完善用户、站点、内容管理页面UI
- ✅ 修复TypeScript类型导入错误
- ✅ 修复axios类型导入错误
- ✅ 统一使用路径别名@导入模块
- ✅ 更新密码为BCrypt加密（password）

#### v0.1.0 - 项目初始化
- ✅ 创建Spring Boot后端项目
- ✅ 创建Vue.js前端项目
- ✅ 设计并创建21张数据表
- ✅ 初始化测试数据
- ✅ 配置Flyway数据库迁移
- ✅ 配置开发环境

## 许可证

MIT License

## 作者

毕业设计项目

