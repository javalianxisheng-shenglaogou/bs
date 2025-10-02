# 多站点CMS系统 - API接口文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 项目名称 | 多站点内容管理系统 API |
| 文档版本 | v2.0 |
| 基础URL | http://localhost:8080/api |
| 认证方式 | JWT Bearer Token |
| 编写日期 | 2025-10-02 |

---

## 目录

1. [通用说明](#1-通用说明)
2. [认证接口](#2-认证接口)
3. [用户管理接口](#3-用户管理接口)
4. [站点管理接口](#4-站点管理接口)
5. [内容管理接口](#5-内容管理接口)
6. [分类管理接口](#6-分类管理接口)
7. [工作流接口](#7-工作流接口)
8. [统计接口](#8-统计接口)

---

## 1. 通用说明

### 1.1 请求格式

所有请求必须包含以下Header：

```
Content-Type: application/json
Authorization: Bearer {access_token}  // 除登录、注册等公开接口外
```

### 1.2 响应格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 业务数据
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

#### 错误响应

```json
{
  "code": 400,
  "message": "参数验证失败",
  "errors": [
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ],
  "timestamp": "2025-10-02T18:30:00"
}
```

#### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      // 数据列表
    ],
    "page": 1,
    "size": 20,
    "total": 100,
    "totalPages": 5
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 1.3 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 204 | 删除成功（无返回内容） |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

---

## 2. 认证接口

### 2.1 用户登录

**接口地址**：`POST /auth/login`

**请求参数**：

```json
{
  "username": "admin",           // 用户名或邮箱，必填
  "password": "admin123",        // 密码，必填
  "remember": false              // 是否记住我，可选
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "nickname": "系统管理员",
      "avatar": "https://example.com/avatar.jpg",
      "roles": ["SUPER_ADMIN"],
      "permissions": ["*:*:*"]
    }
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 2.2 用户注册

**接口地址**：`POST /auth/register`

**请求参数**：

```json
{
  "username": "newuser",         // 用户名，必填，3-20字符
  "email": "user@example.com",   // 邮箱，必填
  "password": "password123",     // 密码，必填，至少8位
  "nickname": "新用户",           // 昵称，可选
  "mobile": "13800138000"        // 手机号，可选
}
```

**响应示例**：

```json
{
  "code": 201,
  "message": "注册成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "user@example.com",
    "nickname": "新用户",
    "status": "PENDING"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 2.3 刷新令牌

**接口地址**：`POST /auth/refresh`

**请求参数**：

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 2.4 用户登出

**接口地址**：`POST /auth/logout`

**请求Header**：需要 Authorization

**响应示例**：

```json
{
  "code": 200,
  "message": "登出成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 2.5 忘记密码

**接口地址**：`POST /auth/forgot-password`

**请求参数**：

```json
{
  "email": "user@example.com"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "密码重置邮件已发送",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 2.6 重置密码

**接口地址**：`POST /auth/reset-password`

**请求参数**：

```json
{
  "token": "reset_token_here",
  "newPassword": "newpassword123"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "密码重置成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 3. 用户管理接口

### 3.1 获取用户列表

**接口地址**：`GET /users`

**权限要求**：`user:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认20 |
| keyword | string | 否 | 搜索关键词（用户名、邮箱、昵称） |
| status | string | 否 | 用户状态：ACTIVE, INACTIVE, LOCKED, PENDING |
| roleId | long | 否 | 角色ID |
| siteId | long | 否 | 站点ID |
| sortBy | string | 否 | 排序字段，默认createdAt |
| sortOrder | string | 否 | 排序方向：ASC, DESC，默认DESC |

**请求示例**：

```
GET /users?page=1&size=20&keyword=admin&status=ACTIVE&sortBy=createdAt&sortOrder=DESC
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "nickname": "系统管理员",
        "avatar": "https://example.com/avatar.jpg",
        "status": "ACTIVE",
        "roles": [
          {
            "id": 1,
            "name": "超级管理员",
            "code": "SUPER_ADMIN"
          }
        ],
        "lastLoginAt": "2025-10-02T18:00:00",
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "page": 1,
    "size": 20,
    "total": 100,
    "totalPages": 5
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.2 获取用户详情

**接口地址**：`GET /users/{id}`

**权限要求**：`user:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "mobile": "13800138000",
    "nickname": "系统管理员",
    "realName": "张三",
    "avatar": "https://example.com/avatar.jpg",
    "gender": "MALE",
    "birthday": "1990-01-01",
    "bio": "系统管理员",
    "status": "ACTIVE",
    "emailVerified": true,
    "mobileVerified": true,
    "roles": [
      {
        "id": 1,
        "name": "超级管理员",
        "code": "SUPER_ADMIN",
        "siteId": null
      }
    ],
    "permissions": ["*:*:*"],
    "lastLoginAt": "2025-10-02T18:00:00",
    "lastLoginIp": "192.168.1.100",
    "loginCount": 100,
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-10-02T18:00:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.3 创建用户

**接口地址**：`POST /users`

**权限要求**：`user:create`

**请求参数**：

```json
{
  "username": "newuser",         // 必填，3-20字符
  "email": "user@example.com",   // 必填
  "password": "password123",     // 必填，至少8位
  "nickname": "新用户",           // 可选
  "mobile": "13800138000",       // 可选
  "realName": "张三",            // 可选
  "gender": "MALE",              // 可选：MALE, FEMALE, OTHER
  "birthday": "1990-01-01",      // 可选
  "bio": "个人简介",             // 可选
  "roleIds": [2, 3],             // 可选，角色ID列表
  "siteId": 1                    // 可选，站点ID
}
```

**响应示例**：

```json
{
  "code": 201,
  "message": "用户创建成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "user@example.com",
    "nickname": "新用户",
    "status": "ACTIVE",
    "createdAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.4 更新用户

**接口地址**：`PUT /users/{id}`

**权限要求**：`user:update`

**请求参数**：

```json
{
  "email": "newemail@example.com",  // 可选
  "nickname": "新昵称",              // 可选
  "mobile": "13900139000",          // 可选
  "realName": "李四",               // 可选
  "gender": "FEMALE",               // 可选
  "birthday": "1995-05-05",         // 可选
  "bio": "更新的个人简介",           // 可选
  "avatar": "https://example.com/new-avatar.jpg"  // 可选
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "用户更新成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "newemail@example.com",
    "nickname": "新昵称",
    "updatedAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.5 删除用户

**接口地址**：`DELETE /users/{id}`

**权限要求**：`user:delete`

**响应示例**：

```json
{
  "code": 200,
  "message": "用户删除成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.6 修改用户状态

**接口地址**：`PATCH /users/{id}/status`

**权限要求**：`user:update`

**请求参数**：

```json
{
  "status": "INACTIVE"  // ACTIVE, INACTIVE, LOCKED, PENDING
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "用户状态更新成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.7 分配角色

**接口地址**：`POST /users/{id}/roles`

**权限要求**：`user:assign-role`

**请求参数**：

```json
{
  "roleIds": [2, 3],    // 角色ID列表
  "siteId": 1           // 可选，站点ID
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "角色分配成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 3.8 修改密码

**接口地址**：`POST /users/{id}/change-password`

**权限要求**：`user:update` 或 本人

**请求参数**：

```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "密码修改成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 4. 站点管理接口

### 4.1 获取站点列表

**接口地址**：`GET /sites`

**权限要求**：`site:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认20 |
| keyword | string | 否 | 搜索关键词（站点名称、代码） |
| status | string | 否 | 站点状态 |
| ownerId | long | 否 | 所有者ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "主站",
        "code": "main",
        "primaryDomain": "www.example.com",
        "status": "ACTIVE",
        "owner": {
          "id": 1,
          "username": "admin",
          "nickname": "系统管理员"
        },
        "contentCount": 100,
        "userCount": 50,
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "page": 1,
    "size": 20,
    "total": 10,
    "totalPages": 1
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 4.2 获取站点详情

**接口地址**：`GET /sites/{id}`

**权限要求**：`site:view`

**响应示例**：略（包含完整站点信息和配置）

### 4.3 创建站点

**接口地址**：`POST /sites`

**权限要求**：`site:create`

**请求参数**：

```json
{
  "name": "新站点",
  "code": "newsite",
  "primaryDomain": "newsite.example.com",
  "description": "站点描述",
  "theme": "default",
  "language": "zh-CN",
  "timezone": "Asia/Shanghai"
}
```

### 4.4 更新站点

**接口地址**：`PUT /sites/{id}`

**权限要求**：`site:update`

**请求参数**：

```json
{
  "name": "更新的站点名称",
  "description": "更新的描述",
  "primaryDomain": "updated.example.com",
  "theme": "modern",
  "language": "en-US"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "站点更新成功",
  "data": {
    "id": 1,
    "name": "更新的站点名称",
    "updatedAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 4.5 删除站点

**接口地址**：`DELETE /sites/{id}`

**权限要求**：`site:delete`

**响应示例**：

```json
{
  "code": 200,
  "message": "站点删除成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 4.6 获取站点配置

**接口地址**：`GET /sites/{id}/configs`

**权限要求**：`site:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "siteTitle": "我的网站",
    "siteDescription": "网站描述",
    "seoKeywords": "关键词1,关键词2",
    "theme": "default",
    "language": "zh-CN",
    "timezone": "Asia/Shanghai",
    "dateFormat": "YYYY-MM-DD",
    "allowComment": true,
    "allowRegister": true
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 4.7 更新站点配置

**接口地址**：`PUT /sites/{id}/configs`

**权限要求**：`site:update`

**请求参数**：

```json
{
  "siteTitle": "新的网站标题",
  "siteDescription": "新的网站描述",
  "allowComment": false
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "配置更新成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 5. 内容管理接口

### 5.1 获取内容列表

**接口地址**：`GET /contents`

**权限要求**：`content:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认20 |
| siteId | long | 是 | 站点ID |
| keyword | string | 否 | 搜索关键词（标题、内容） |
| categoryId | long | 否 | 分类ID |
| status | string | 否 | 状态：DRAFT, PENDING, APPROVED, PUBLISHED, SCHEDULED, ARCHIVED, REJECTED |
| contentType | string | 否 | 内容类型：ARTICLE, PAGE, NEWS, PRODUCT, CUSTOM |
| authorId | long | 否 | 作者ID |
| startDate | string | 否 | 开始日期（YYYY-MM-DD） |
| endDate | string | 否 | 结束日期（YYYY-MM-DD） |
| isTop | boolean | 否 | 是否置顶 |
| isFeatured | boolean | 否 | 是否推荐 |
| sortBy | string | 否 | 排序字段：createdAt, publishAt, viewCount, likeCount |
| sortOrder | string | 否 | 排序方向：ASC, DESC |

**请求示例**：

```
GET /contents?siteId=1&page=1&size=20&status=PUBLISHED&sortBy=publishAt&sortOrder=DESC
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "文章标题",
        "slug": "article-slug",
        "summary": "文章摘要",
        "contentType": "ARTICLE",
        "status": "PUBLISHED",
        "author": {
          "id": 1,
          "username": "admin",
          "nickname": "管理员"
        },
        "category": {
          "id": 1,
          "name": "技术"
        },
        "tags": [
          {"id": 1, "name": "Java"},
          {"id": 2, "name": "Spring Boot"}
        ],
        "featuredImage": "https://example.com/image.jpg",
        "isTop": false,
        "isFeatured": true,
        "viewCount": 100,
        "likeCount": 10,
        "commentCount": 5,
        "publishAt": "2025-10-01T10:00:00",
        "createdAt": "2025-09-30T15:00:00",
        "updatedAt": "2025-10-01T09:00:00"
      }
    ],
    "page": 1,
    "size": 20,
    "total": 100,
    "totalPages": 5
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.2 获取内容详情

**接口地址**：`GET /contents/{id}`

**权限要求**：`content:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "siteId": 1,
    "title": "文章标题",
    "slug": "article-slug",
    "summary": "文章摘要",
    "content": "<p>文章内容HTML</p>",
    "contentType": "ARTICLE",
    "template": "default",
    "status": "PUBLISHED",
    "workflowInstanceId": 123,
    "author": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "avatar": "https://example.com/avatar.jpg"
    },
    "editor": {
      "id": 2,
      "username": "editor",
      "nickname": "编辑"
    },
    "reviewer": {
      "id": 3,
      "username": "reviewer",
      "nickname": "审核员"
    },
    "category": {
      "id": 1,
      "name": "技术",
      "slug": "tech"
    },
    "tags": [
      {"id": 1, "name": "Java", "slug": "java"},
      {"id": 2, "name": "Spring Boot", "slug": "spring-boot"}
    ],
    "featuredImage": "https://example.com/image.jpg",
    "attachments": [
      {
        "name": "文档.pdf",
        "url": "https://example.com/doc.pdf",
        "size": 1024000
      }
    ],
    "isTop": false,
    "isFeatured": true,
    "isHot": false,
    "sortOrder": 0,
    "allowComment": true,
    "allowShare": true,
    "viewCount": 100,
    "likeCount": 10,
    "commentCount": 5,
    "shareCount": 3,
    "seoTitle": "SEO标题",
    "seoKeywords": "关键词1,关键词2",
    "seoDescription": "SEO描述",
    "publishAt": "2025-10-01T10:00:00",
    "scheduledAt": null,
    "archivedAt": null,
    "createdAt": "2025-09-30T15:00:00",
    "updatedAt": "2025-10-01T09:00:00",
    "version": 5
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.3 创建内容

**接口地址**：`POST /contents`

**权限要求**：`content:create`

**请求参数**：

```json
{
  "siteId": 1,                          // 必填
  "title": "新文章标题",                 // 必填，最多255字符
  "slug": "new-article",                // 可选，URL别名
  "summary": "文章摘要",                 // 可选
  "content": "<p>文章内容</p>",         // 必填
  "contentType": "ARTICLE",             // 必填：ARTICLE, PAGE, NEWS, PRODUCT, CUSTOM
  "template": "default",                // 可选，模板名称
  "categoryId": 1,                      // 可选，分类ID
  "tagIds": [1, 2, 3],                  // 可选，标签ID列表
  "featuredImage": "https://example.com/image.jpg",  // 可选
  "attachments": [                      // 可选，附件列表
    {
      "name": "文档.pdf",
      "url": "https://example.com/doc.pdf",
      "size": 1024000
    }
  ],
  "isTop": false,                       // 可选，是否置顶
  "isFeatured": true,                   // 可选，是否推荐
  "isHot": false,                       // 可选，是否热门
  "sortOrder": 0,                       // 可选，排序
  "allowComment": true,                 // 可选，是否允许评论
  "allowShare": true,                   // 可选，是否允许分享
  "seoTitle": "SEO标题",                // 可选
  "seoKeywords": "关键词1,关键词2",      // 可选
  "seoDescription": "SEO描述",          // 可选
  "publishType": "DRAFT",               // 必填：DRAFT(草稿), IMMEDIATE(立即发布), SCHEDULED(定时发布)
  "scheduledAt": "2025-10-03T10:00:00"  // 定时发布时间（publishType为SCHEDULED时必填）
}
```

**响应示例**：

```json
{
  "code": 201,
  "message": "内容创建成功",
  "data": {
    "id": 2,
    "title": "新文章标题",
    "status": "DRAFT",
    "createdAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.4 更新内容

**接口地址**：`PUT /contents/{id}`

**权限要求**：`content:update`

**请求参数**：与创建内容类似，所有字段都是可选的

**响应示例**：

```json
{
  "code": 200,
  "message": "内容更新成功",
  "data": {
    "id": 2,
    "title": "更新的文章标题",
    "version": 6,
    "updatedAt": "2025-10-02T18:35:00"
  },
  "timestamp": "2025-10-02T18:35:00"
}
```

### 5.5 删除内容

**接口地址**：`DELETE /contents/{id}`

**权限要求**：`content:delete`

**响应示例**：

```json
{
  "code": 200,
  "message": "内容删除成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.6 批量删除内容

**接口地址**：`POST /contents/batch-delete`

**权限要求**：`content:delete`

**请求参数**：

```json
{
  "ids": [1, 2, 3, 4, 5]
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": {
    "successCount": 5,
    "failedCount": 0
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.7 发布内容

**接口地址**：`POST /contents/{id}/publish`

**权限要求**：`content:publish`

**响应示例**：

```json
{
  "code": 200,
  "message": "内容发布成功",
  "data": {
    "id": 1,
    "status": "PUBLISHED",
    "publishAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.8 定时发布内容

**接口地址**：`POST /contents/{id}/schedule`

**权限要求**：`content:publish`

**请求参数**：

```json
{
  "scheduledAt": "2025-10-03T10:00:00"
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "定时发布设置成功",
  "data": {
    "id": 1,
    "status": "SCHEDULED",
    "scheduledAt": "2025-10-03T10:00:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.9 归档内容

**接口地址**：`POST /contents/{id}/archive`

**权限要求**：`content:update`

**响应示例**：

```json
{
  "code": 200,
  "message": "内容归档成功",
  "data": {
    "id": 1,
    "status": "ARCHIVED",
    "archivedAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.10 置顶/取消置顶

**接口地址**：`PATCH /contents/{id}/top`

**权限要求**：`content:update`

**请求参数**：

```json
{
  "isTop": true
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.11 推荐/取消推荐

**接口地址**：`PATCH /contents/{id}/featured`

**权限要求**：`content:update`

**请求参数**：

```json
{
  "isFeatured": true
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.12 增加浏览次数

**接口地址**：`POST /contents/{id}/view`

**权限要求**：无（公开接口）

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "viewCount": 101
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.13 点赞内容

**接口地址**：`POST /contents/{id}/like`

**权限要求**：需要登录

**响应示例**：

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "likeCount": 11,
    "isLiked": true
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.14 搜索内容

**接口地址**：`GET /contents/search`

**权限要求**：`content:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| siteId | long | 是 | 站点ID |
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |

**响应示例**：与获取内容列表相同

### 5.15 获取内容版本列表

**接口地址**：`GET /contents/{id}/versions`

**权限要求**：`content:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 10,
      "versionNumber": 5,
      "versionName": "v5",
      "changeType": "UPDATE",
      "changeDescription": "更新了标题和内容",
      "changedFields": ["title", "content"],
      "createdBy": {
        "id": 1,
        "username": "admin",
        "nickname": "管理员"
      },
      "createdAt": "2025-10-02T18:00:00"
    },
    {
      "id": 9,
      "versionNumber": 4,
      "versionName": "v4",
      "changeType": "UPDATE",
      "changeDescription": "添加了特色图片",
      "changedFields": ["featuredImage"],
      "createdBy": {
        "id": 2,
        "username": "editor",
        "nickname": "编辑"
      },
      "createdAt": "2025-10-01T15:00:00"
    }
  ],
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.16 获取版本详情

**接口地址**：`GET /contents/{id}/versions/{versionNumber}`

**权限要求**：`content:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "contentId": 1,
    "versionNumber": 5,
    "versionName": "v5",
    "title": "文章标题（版本5）",
    "summary": "文章摘要（版本5）",
    "content": "<p>文章内容（版本5）</p>",
    "contentSnapshot": {
      // 完整的内容快照JSON
    },
    "changeType": "UPDATE",
    "changeDescription": "更新了标题和内容",
    "changedFields": ["title", "content"],
    "createdBy": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员"
    },
    "createdAt": "2025-10-02T18:00:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.17 回滚到指定版本

**接口地址**：`POST /contents/{id}/versions/{versionNumber}/rollback`

**权限要求**：`content:update`

**响应示例**：

```json
{
  "code": 200,
  "message": "版本回滚成功",
  "data": {
    "id": 1,
    "currentVersion": 6,
    "rolledBackToVersion": 4
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 5.18 导出内容

**接口地址**：`POST /contents/export`

**权限要求**：`content:export`

**请求参数**：

```json
{
  "siteId": 1,
  "format": "EXCEL",              // EXCEL 或 CSV
  "filters": {
    "status": "PUBLISHED",
    "categoryId": 1,
    "startDate": "2025-01-01",
    "endDate": "2025-12-31"
  },
  "fields": [                     // 要导出的字段
    "title", "author", "category",
    "status", "viewCount", "publishAt"
  ]
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "导出任务已创建",
  "data": {
    "taskId": "export_20251002183000",
    "downloadUrl": "https://example.com/exports/contents_20251002.xlsx",
    "expiresAt": "2025-10-03T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 6. 分类管理接口

### 6.1 获取分类列表

**接口地址**：`GET /categories`

**权限要求**：`category:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| siteId | long | 是 | 站点ID |
| parentId | long | 否 | 父分类ID（不传则获取所有分类） |
| level | int | 否 | 层级 |
| isNav | boolean | 否 | 是否显示在导航 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "技术",
      "slug": "tech",
      "description": "技术相关文章",
      "parentId": null,
      "level": 1,
      "path": "/tech",
      "icon": "icon-tech",
      "coverImage": "https://example.com/tech-cover.jpg",
      "sortOrder": 1,
      "isNav": true,
      "contentCount": 50,
      "children": [
        {
          "id": 2,
          "name": "Java",
          "slug": "java",
          "parentId": 1,
          "level": 2,
          "path": "/tech/java",
          "contentCount": 20,
          "children": []
        }
      ],
      "createdAt": "2025-01-01T00:00:00"
    }
  ],
  "timestamp": "2025-10-02T18:30:00"
}
```

### 6.2 获取分类树

**接口地址**：`GET /categories/tree`

**权限要求**：`category:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| siteId | long | 是 | 站点ID |

**响应示例**：返回完整的树形结构

### 6.3 获取分类详情

**接口地址**：`GET /categories/{id}`

**权限要求**：`category:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "siteId": 1,
    "name": "技术",
    "slug": "tech",
    "description": "技术相关文章",
    "parentId": null,
    "level": 1,
    "path": "/tech",
    "icon": "icon-tech",
    "coverImage": "https://example.com/tech-cover.jpg",
    "sortOrder": 1,
    "isNav": true,
    "seoTitle": "技术文章",
    "seoKeywords": "技术,编程,开发",
    "seoDescription": "技术相关文章分类",
    "contentCount": 50,
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-10-01T10:00:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 6.4 创建分类

**接口地址**：`POST /categories`

**权限要求**：`category:create`

**请求参数**：

```json
{
  "siteId": 1,                    // 必填
  "name": "新分类",                // 必填
  "slug": "new-category",         // 可选
  "description": "分类描述",       // 可选
  "parentId": 1,                  // 可选，父分类ID
  "icon": "icon-new",             // 可选
  "coverImage": "https://example.com/cover.jpg",  // 可选
  "sortOrder": 10,                // 可选
  "isNav": true,                  // 可选
  "seoTitle": "SEO标题",          // 可选
  "seoKeywords": "关键词",        // 可选
  "seoDescription": "SEO描述"     // 可选
}
```

**响应示例**：

```json
{
  "code": 201,
  "message": "分类创建成功",
  "data": {
    "id": 10,
    "name": "新分类",
    "level": 2,
    "path": "/tech/new-category",
    "createdAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 6.5 更新分类

**接口地址**：`PUT /categories/{id}`

**权限要求**：`category:update`

**请求参数**：与创建分类类似，所有字段都是可选的

### 6.6 删除分类

**接口地址**：`DELETE /categories/{id}`

**权限要求**：`category:delete`

**响应示例**：

```json
{
  "code": 200,
  "message": "分类删除成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

**注意**：如果分类下有内容或子分类，将无法删除

### 6.7 移动分类

**接口地址**：`POST /categories/{id}/move`

**权限要求**：`category:update`

**请求参数**：

```json
{
  "targetParentId": 2,    // 目标父分类ID，null表示移动到根级别
  "sortOrder": 5          // 新的排序位置
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "分类移动成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 7. 工作流接口

### 7.1 获取工作流列表

**接口地址**：`GET /workflows`

**权限要求**：`workflow:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| siteId | long | 否 | 站点ID |
| businessType | string | 否 | 业务类型 |
| status | string | 否 | 状态：DRAFT, ACTIVE, INACTIVE, ARCHIVED |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "内容审批流程",
        "code": "content_approval",
        "description": "内容发布前的审批流程",
        "triggerType": "MANUAL",
        "businessType": "CONTENT",
        "status": "ACTIVE",
        "isDefault": true,
        "nodeCount": 3,
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "page": 1,
    "size": 20,
    "total": 10,
    "totalPages": 1
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.2 获取工作流详情

**接口地址**：`GET /workflows/{id}`

**权限要求**：`workflow:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "内容审批流程",
    "code": "content_approval",
    "description": "内容发布前的审批流程",
    "triggerType": "MANUAL",
    "businessType": "CONTENT",
    "siteId": 1,
    "status": "ACTIVE",
    "isDefault": true,
    "config": {
      "autoApprove": false,
      "notifyOnComplete": true
    },
    "nodes": [
      {
        "id": 1,
        "name": "开始",
        "code": "start",
        "nodeType": "START",
        "positionX": 100,
        "positionY": 100
      },
      {
        "id": 2,
        "name": "编辑审核",
        "code": "editor_review",
        "nodeType": "APPROVAL",
        "assigneeType": "ROLE",
        "assigneeIds": [3],
        "approvalMode": "SINGLE",
        "positionX": 300,
        "positionY": 100
      },
      {
        "id": 3,
        "name": "主编审核",
        "code": "chief_review",
        "nodeType": "APPROVAL",
        "assigneeType": "ROLE",
        "assigneeIds": [4],
        "approvalMode": "SINGLE",
        "positionX": 500,
        "positionY": 100
      },
      {
        "id": 4,
        "name": "结束",
        "code": "end",
        "nodeType": "END",
        "positionX": 700,
        "positionY": 100
      }
    ],
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-10-01T10:00:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.3 创建工作流

**接口地址**：`POST /workflows`

**权限要求**：`workflow:create`

**请求参数**：

```json
{
  "name": "新审批流程",
  "code": "new_approval",
  "description": "新的审批流程",
  "triggerType": "MANUAL",
  "businessType": "CONTENT",
  "siteId": 1,
  "nodes": [
    {
      "name": "开始",
      "code": "start",
      "nodeType": "START",
      "positionX": 100,
      "positionY": 100
    },
    {
      "name": "审核",
      "code": "review",
      "nodeType": "APPROVAL",
      "assigneeType": "ROLE",
      "assigneeIds": [3],
      "approvalMode": "SINGLE",
      "positionX": 300,
      "positionY": 100
    },
    {
      "name": "结束",
      "code": "end",
      "nodeType": "END",
      "positionX": 500,
      "positionY": 100
    }
  ]
}
```

**响应示例**：

```json
{
  "code": 201,
  "message": "工作流创建成功",
  "data": {
    "id": 2,
    "name": "新审批流程",
    "code": "new_approval",
    "createdAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.4 启动工作流

**接口地址**：`POST /workflows/{workflowId}/start`

**权限要求**：根据业务类型决定

**请求参数**：

```json
{
  "businessId": 1,          // 业务ID（如内容ID）
  "businessType": "CONTENT" // 业务类型
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "工作流启动成功",
  "data": {
    "instanceId": 100,
    "workflowId": 1,
    "businessId": 1,
    "businessType": "CONTENT",
    "status": "RUNNING",
    "currentNodeId": 2,
    "initiatedAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.5 获取工作流实例列表

**接口地址**：`GET /workflow-instances`

**权限要求**：`workflow:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| workflowId | long | 否 | 工作流ID |
| businessType | string | 否 | 业务类型 |
| status | string | 否 | 状态：RUNNING, COMPLETED, REJECTED, CANCELLED, ERROR |
| initiatorId | long | 否 | 发起人ID |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 100,
        "workflow": {
          "id": 1,
          "name": "内容审批流程"
        },
        "businessId": 1,
        "businessType": "CONTENT",
        "status": "RUNNING",
        "currentNode": {
          "id": 2,
          "name": "编辑审核"
        },
        "initiator": {
          "id": 1,
          "username": "admin",
          "nickname": "管理员"
        },
        "initiatedAt": "2025-10-02T18:00:00",
        "completedAt": null
      }
    ],
    "page": 1,
    "size": 20,
    "total": 50,
    "totalPages": 3
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.6 获取工作流实例详情

**接口地址**：`GET /workflow-instances/{id}`

**权限要求**：`workflow:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 100,
    "workflow": {
      "id": 1,
      "name": "内容审批流程",
      "code": "content_approval"
    },
    "businessId": 1,
    "businessType": "CONTENT",
    "businessTitle": "文章标题",
    "status": "RUNNING",
    "currentNode": {
      "id": 2,
      "name": "编辑审核",
      "nodeType": "APPROVAL"
    },
    "initiator": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员"
    },
    "initiatedAt": "2025-10-02T18:00:00",
    "completedAt": null,
    "completionReason": null,
    "tasks": [
      {
        "id": 200,
        "taskName": "编辑审核",
        "taskType": "APPROVAL",
        "status": "PENDING",
        "assignee": {
          "id": 2,
          "username": "editor",
          "nickname": "编辑"
        },
        "assignedAt": "2025-10-02T18:00:00",
        "dueDate": "2025-10-03T18:00:00"
      }
    ]
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.7 获取待办任务

**接口地址**：`GET /workflow-tasks/pending`

**权限要求**：需要登录

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| businessType | string | 否 | 业务类型 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 200,
        "instance": {
          "id": 100,
          "workflow": {
            "id": 1,
            "name": "内容审批流程"
          },
          "businessId": 1,
          "businessType": "CONTENT",
          "businessTitle": "文章标题"
        },
        "taskName": "编辑审核",
        "taskType": "APPROVAL",
        "status": "PENDING",
        "assignedAt": "2025-10-02T18:00:00",
        "dueDate": "2025-10-03T18:00:00",
        "isOverdue": false
      }
    ],
    "page": 1,
    "size": 20,
    "total": 5,
    "totalPages": 1
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.8 审批任务

**接口地址**：`POST /workflow-tasks/{taskId}/approve`

**权限要求**：任务分配给当前用户

**请求参数**：

```json
{
  "approved": true,           // true=通过，false=拒绝
  "comment": "审批通过"        // 审批意见
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "审批成功",
  "data": {
    "taskId": 200,
    "status": "APPROVED",
    "processedAt": "2025-10-02T18:30:00",
    "nextTask": {
      "id": 201,
      "taskName": "主编审核",
      "assignee": {
        "id": 3,
        "username": "chief",
        "nickname": "主编"
      }
    }
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.9 获取工作流历史

**接口地址**：`GET /workflow-instances/{instanceId}/history`

**权限要求**：`workflow:view`

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 200,
      "taskName": "编辑审核",
      "taskType": "APPROVAL",
      "status": "APPROVED",
      "assignee": {
        "id": 2,
        "username": "editor",
        "nickname": "编辑"
      },
      "comment": "审批通过",
      "assignedAt": "2025-10-02T18:00:00",
      "processedAt": "2025-10-02T18:30:00",
      "duration": 1800
    },
    {
      "id": 201,
      "taskName": "主编审核",
      "taskType": "APPROVAL",
      "status": "PENDING",
      "assignee": {
        "id": 3,
        "username": "chief",
        "nickname": "主编"
      },
      "assignedAt": "2025-10-02T18:30:00",
      "dueDate": "2025-10-03T18:30:00"
    }
  ],
  "timestamp": "2025-10-02T18:30:00"
}
```

### 7.10 撤回工作流

**接口地址**：`POST /workflow-instances/{instanceId}/withdraw`

**权限要求**：工作流发起人

**响应示例**：

```json
{
  "code": 200,
  "message": "工作流撤回成功",
  "data": {
    "instanceId": 100,
    "status": "CANCELLED",
    "completedAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 8. 统计接口

### 8.1 获取仪表盘统计

**接口地址**：`GET /statistics/dashboard`

**权限要求**：`statistics:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| siteId | long | 否 | 站点ID（不传则统计所有站点） |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "overview": {
      "userCount": 1234,
      "contentCount": 5678,
      "visitCount": 98765,
      "commentCount": 432
    },
    "trends": {
      "userGrowth": 12.5,        // 用户增长率（%）
      "contentGrowth": 8.3,      // 内容增长率（%）
      "visitGrowth": 15.7,       // 访问增长率（%）
      "commentGrowth": -2.1      // 评论增长率（%）
    },
    "contentStats": {
      "draft": 50,
      "pending": 20,
      "published": 5000,
      "archived": 100
    },
    "recentContents": [
      {
        "id": 1,
        "title": "最新文章",
        "author": "admin",
        "viewCount": 100,
        "createdAt": "2025-10-02T10:00:00"
      }
    ],
    "pendingTasks": [
      {
        "id": 200,
        "taskName": "内容审批",
        "businessTitle": "文章标题",
        "assignedAt": "2025-10-02T18:00:00"
      }
    ],
    "topContents": [
      {
        "id": 1,
        "title": "热门文章",
        "viewCount": 10000,
        "likeCount": 500
      }
    ]
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 8.2 获取内容统计

**接口地址**：`GET /statistics/contents`

**权限要求**：`statistics:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| siteId | long | 否 | 站点ID |
| startDate | string | 否 | 开始日期（YYYY-MM-DD） |
| endDate | string | 否 | 结束日期（YYYY-MM-DD） |
| groupBy | string | 否 | 分组方式：DAY, WEEK, MONTH |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": {
      "totalCount": 5678,
      "publishedCount": 5000,
      "draftCount": 50,
      "pendingCount": 20,
      "archivedCount": 100
    },
    "byCategory": [
      {
        "categoryId": 1,
        "categoryName": "技术",
        "count": 2000
      },
      {
        "categoryId": 2,
        "categoryName": "生活",
        "count": 1500
      }
    ],
    "byAuthor": [
      {
        "authorId": 1,
        "authorName": "admin",
        "count": 500
      },
      {
        "authorId": 2,
        "authorName": "editor",
        "count": 300
      }
    ],
    "timeline": [
      {
        "date": "2025-10-01",
        "count": 10,
        "viewCount": 1000,
        "likeCount": 50
      },
      {
        "date": "2025-10-02",
        "count": 15,
        "viewCount": 1500,
        "likeCount": 75
      }
    ]
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 8.3 获取用户统计

**接口地址**：`GET /statistics/users`

**权限要求**：`statistics:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| groupBy | string | 否 | 分组方式 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": {
      "totalCount": 1234,
      "activeCount": 800,
      "inactiveCount": 400,
      "lockedCount": 34
    },
    "byRole": [
      {
        "roleId": 1,
        "roleName": "超级管理员",
        "count": 5
      },
      {
        "roleId": 2,
        "roleName": "站点管理员",
        "count": 20
      },
      {
        "roleId": 3,
        "roleName": "编辑者",
        "count": 100
      }
    ],
    "registrationTrend": [
      {
        "date": "2025-10-01",
        "count": 10
      },
      {
        "date": "2025-10-02",
        "count": 15
      }
    ],
    "loginActivity": [
      {
        "date": "2025-10-01",
        "loginCount": 500,
        "uniqueUsers": 300
      },
      {
        "date": "2025-10-02",
        "loginCount": 600,
        "uniqueUsers": 350
      }
    ]
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 8.4 获取访问统计

**接口地址**：`GET /statistics/visits`

**权限要求**：`statistics:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| siteId | long | 否 | 站点ID |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |
| groupBy | string | 否 | 分组方式 |

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": {
      "totalVisits": 98765,
      "uniqueVisitors": 45678,
      "pageViews": 234567,
      "avgDuration": 180
    },
    "timeline": [
      {
        "date": "2025-10-01",
        "visits": 5000,
        "uniqueVisitors": 3000,
        "pageViews": 15000
      },
      {
        "date": "2025-10-02",
        "visits": 6000,
        "uniqueVisitors": 3500,
        "pageViews": 18000
      }
    ],
    "topPages": [
      {
        "contentId": 1,
        "title": "热门文章",
        "url": "/articles/hot-article",
        "visits": 10000
      }
    ],
    "trafficSources": [
      {
        "source": "direct",
        "count": 40000,
        "percentage": 40.5
      },
      {
        "source": "search",
        "count": 30000,
        "percentage": 30.4
      },
      {
        "source": "social",
        "count": 20000,
        "percentage": 20.3
      }
    ],
    "deviceStats": {
      "desktop": 50000,
      "mobile": 40000,
      "tablet": 8765
    },
    "browserStats": [
      {
        "browser": "Chrome",
        "count": 50000,
        "percentage": 50.6
      },
      {
        "browser": "Safari",
        "count": 20000,
        "percentage": 20.3
      }
    ]
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 8.5 导出统计报表

**接口地址**：`POST /statistics/export`

**权限要求**：`statistics:export`

**请求参数**：

```json
{
  "reportType": "CONTENT",      // CONTENT, USER, VISIT
  "format": "EXCEL",            // EXCEL, PDF, CSV
  "siteId": 1,
  "startDate": "2025-01-01",
  "endDate": "2025-12-31",
  "includeCharts": true
}
```

**响应示例**：

```json
{
  "code": 200,
  "message": "报表生成成功",
  "data": {
    "taskId": "report_20251002183000",
    "downloadUrl": "https://example.com/reports/statistics_20251002.xlsx",
    "expiresAt": "2025-10-03T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 9. 媒体文件接口

### 9.1 上传文件

**接口地址**：`POST /media/upload`

**权限要求**：`media:upload`

**请求类型**：`multipart/form-data`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | file | 是 | 文件 |
| siteId | long | 是 | 站点ID |
| fileType | string | 否 | 文件类型：IMAGE, VIDEO, AUDIO, DOCUMENT |

**响应示例**：

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "fileName": "image_20251002183000.jpg",
    "originalName": "photo.jpg",
    "filePath": "/uploads/2025/10/02/image_20251002183000.jpg",
    "fileUrl": "https://example.com/uploads/2025/10/02/image_20251002183000.jpg",
    "fileType": "IMAGE",
    "mimeType": "image/jpeg",
    "fileSize": 1024000,
    "width": 1920,
    "height": 1080,
    "createdAt": "2025-10-02T18:30:00"
  },
  "timestamp": "2025-10-02T18:30:00"
}
```

### 9.2 获取文件列表

**接口地址**：`GET /media`

**权限要求**：`media:view`

**请求参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| siteId | long | 是 | 站点ID |
| fileType | string | 否 | 文件类型 |
| keyword | string | 否 | 搜索关键词 |

**响应示例**：略（与上传响应类似的列表）

### 9.3 删除文件

**接口地址**：`DELETE /media/{id}`

**权限要求**：`media:delete`

**响应示例**：

```json
{
  "code": 200,
  "message": "文件删除成功",
  "timestamp": "2025-10-02T18:30:00"
}
```

---

## 10. 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|---------|
| 400 | 请求参数错误 | 检查请求参数格式和必填项 |
| 401 | 未认证 | 请先登录获取令牌 |
| 403 | 无权限 | 联系管理员分配权限 |
| 404 | 资源不存在 | 检查资源ID是否正确 |
| 409 | 资源冲突 | 资源已存在或状态不允许操作 |
| 422 | 业务逻辑错误 | 查看错误消息了解具体原因 |
| 429 | 请求过于频繁 | 稍后再试 |
| 500 | 服务器内部错误 | 联系技术支持 |

---

## 11. 接口调用示例

### JavaScript (Axios)

```javascript
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器 - 添加令牌
api.interceptors.request.use(config => {
  const token = localStorage.getItem('access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // 令牌过期，跳转登录
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 使用示例
async function getContents() {
  try {
    const response = await api.get('/contents', {
      params: {
        siteId: 1,
        page: 1,
        size: 20
      }
    })
    console.log(response.data)
  } catch (error) {
    console.error('获取内容失败:', error)
  }
}
```

### Java (RestTemplate)

```java
RestTemplate restTemplate = new RestTemplate();

// 设置请求头
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(accessToken);

// 创建请求
HttpEntity<ContentCreateRequest> request =
    new HttpEntity<>(contentRequest, headers);

// 发送请求
ResponseEntity<ApiResponse<ContentResponse>> response =
    restTemplate.exchange(
        "http://localhost:8080/api/contents",
        HttpMethod.POST,
        request,
        new ParameterizedTypeReference<ApiResponse<ContentResponse>>() {}
    );

ContentResponse content = response.getBody().getData();
```

### cURL

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取内容列表
curl -X GET "http://localhost:8080/api/contents?siteId=1&page=1&size=20" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# 创建内容
curl -X POST http://localhost:8080/api/contents \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "siteId": 1,
    "title": "新文章",
    "content": "<p>内容</p>",
    "contentType": "ARTICLE",
    "publishType": "DRAFT"
  }'
```

---

## 附录

### A. 权限代码列表

| 权限代码 | 说明 |
|---------|------|
| `*:*:*` | 所有权限 |
| `user:view` | 查看用户 |
| `user:create` | 创建用户 |
| `user:update` | 更新用户 |
| `user:delete` | 删除用户 |
| `user:assign-role` | 分配角色 |
| `site:view` | 查看站点 |
| `site:create` | 创建站点 |
| `site:update` | 更新站点 |
| `site:delete` | 删除站点 |
| `content:view` | 查看内容 |
| `content:create` | 创建内容 |
| `content:update` | 更新内容 |
| `content:delete` | 删除内容 |
| `content:publish` | 发布内容 |
| `content:export` | 导出内容 |
| `category:view` | 查看分类 |
| `category:create` | 创建分类 |
| `category:update` | 更新分类 |
| `category:delete` | 删除分类 |
| `workflow:view` | 查看工作流 |
| `workflow:create` | 创建工作流 |
| `workflow:update` | 更新工作流 |
| `workflow:delete` | 删除工作流 |
| `statistics:view` | 查看统计 |
| `statistics:export` | 导出统计 |
| `media:view` | 查看媒体 |
| `media:upload` | 上传媒体 |
| `media:delete` | 删除媒体 |

### B. 枚举值说明

**用户状态 (UserStatus)**：
- `ACTIVE` - 激活
- `INACTIVE` - 未激活
- `LOCKED` - 锁定
- `PENDING` - 待审核

**内容状态 (ContentStatus)**：
- `DRAFT` - 草稿
- `PENDING` - 待审核
- `APPROVED` - 已审核
- `PUBLISHED` - 已发布
- `SCHEDULED` - 定时发布
- `ARCHIVED` - 已归档
- `REJECTED` - 已拒绝

**内容类型 (ContentType)**：
- `ARTICLE` - 文章
- `PAGE` - 页面
- `NEWS` - 新闻
- `PRODUCT` - 产品
- `CUSTOM` - 自定义

**工作流状态 (WorkflowStatus)**：
- `DRAFT` - 草稿
- `ACTIVE` - 激活
- `INACTIVE` - 未激活
- `ARCHIVED` - 已归档

**工作流实例状态 (InstanceStatus)**：
- `RUNNING` - 运行中
- `COMPLETED` - 已完成
- `REJECTED` - 已拒绝
- `CANCELLED` - 已取消
- `ERROR` - 错误

---
