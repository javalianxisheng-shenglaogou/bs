# 多站点内容管理系统（Multi-Site CMS）

## 项目简介

这是一个基于Spring Boot和Vue 3开发的现代化多站点内容管理系统，支持多站点管理、内容发布、分类管理、用户权限控制、文件上传、系统日志等功能。

### 主要特性

- ✅ **多站点管理** - 支持创建和管理多个独立站点,每个站点独立配置
- ✅ **内容管理** - 富文本编辑器、图片上传、内容分类、状态管理
- ✅ **访客门户** - 独立的访客浏览界面，支持内容展示、搜索和详情查看
- ✅ **版本控制** - 自动记录内容变更历史，支持版本比较、恢复、标记
- ✅ **国际化支持** - 完整的中英文双语界面，支持语言切换和持久化
- ✅ **用户权限** - 基于RBAC的细粒度权限控制
- ✅ **工作流管理** - 灵活的审批流程配置，支持多级审批
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
- **国际化**: Vue I18n 9
- **富文本编辑器**: Quill (@vueup/vue-quill)
- **文本差异对比**: diff-match-patch

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
mysql -uroot -p < database-setup.sql
```

或手动创建：
```sql
CREATE DATABASE multi_site_cms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 配置环境变量：

复制 `.env.example` 文件为 `.env` 并修改配置：
```bash
cp .env.example .env
```

编辑 `.env` 文件，设置数据库密码和其他配置：
```
DB_USERNAME=root
DB_PASSWORD=your_password_here
JWT_SECRET=your-secret-key-change-this-in-production
```

3. 启动后端项目，Flyway会自动执行数据库迁移并插入测试数据

**注意**：生产环境请务必修改默认密码和JWT密钥！

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

## 已实现功能（V1.2）

### 核心功能
- ✅ 用户管理CRUD API
- ✅ 站点管理CRUD API
- ✅ 内容管理CRUD API
- ✅ 分类和标签管理
- ✅ 工作流审批系统
- ✅ 版本控制功能
- ✅ 国际化支持（中英文）
- ✅ 文件上传功能
- ✅ 系统日志管理

### 版本控制功能（V1.2新增）
- ✅ 自动版本创建（创建内容时创建v1，更新时自动递增）
- ✅ 版本历史查看（分页显示所有历史版本）
- ✅ 版本比较（对比两个版本的差异）
- ✅ 版本恢复（恢复到任意历史版本）
- ✅ 版本标记（为重要版本添加标签）
- ✅ 版本删除（管理员权限）
- ✅ 版本详情查看（查看完整的版本快照）

### 国际化功能（V1.2新增）
- ✅ 全局语言切换（中文/English）
- ✅ 语言选择持久化（localStorage）
- ✅ 所有页面完整国际化
  - 仪表盘、用户管理、站点管理
  - 内容管理、分类管理、工作流管理
  - 系统日志、版本历史
- ✅ 表单验证消息国际化
- ✅ 系统提示消息国际化
- ✅ 日期时间格式化

### 计划功能
- 🔄 内容共享功能
- 🔄 数据统计分析
- 🔄 媒体文件管理（文件列表、删除等）
- 🔄 内容定时发布
- 🔄 内容模板管理

## 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 超级管理员 | admin | password | 拥有所有权限 |
| 站点管理员 | siteadmin | password | 管理指定站点 |
| 编辑者 | editor1 | password | 创建和编辑内容 |
| 审核者 | reviewer1 | password | 审核内容 |
| 翻译者 | translator1 | password | 翻译内容 |
| 访客用户 | guest_user | password | 浏览已发布内容 |

**注意：** 密码已从 `admin123` 更新为 `password`（BCrypt加密）

### 访客功能

系统提供独立的访客浏览门户：
- 📖 **首页展示** - 展示置顶推荐、精选内容和最新发布
- 🔍 **内容搜索** - 支持关键词模糊搜索和分类筛选
- 📄 **内容详情** - 查看完整的文章内容和相关推荐
- 👤 **个人中心** - 编辑个人信息和修改密码

访客端访问：登录后如果用户只有访客角色，将自动进入访客界面（`/guest`）

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

### 需求和设计文档
- [需求分析文档](./docs/多站点CMS系统需求分析文档.txt)
- [技术实现文档](./docs/技术实现文档.md)
- [版本控制系统设计方案](./docs/内容版本控制系统设计方案.txt)
- [国际化功能设计方案](./docs/网站中英文互译功能设计方案.txt)

### 用户指南
- [测试指南](./README_TESTING_GUIDE.md)
- [工作流使用说明](./README_WORKFLOW.md)

### 开发文档
- [API接口文档](./文档/API接口文档.md)
- [功能测试指南](./文档/功能测试指南.md)

### 问题解决文档
- [问题诊断和解决方案](./docs/问题诊断和解决方案.md)
- [问题解决总结报告](./docs/问题解决总结报告.md)


## 作者
玖夏旳达

