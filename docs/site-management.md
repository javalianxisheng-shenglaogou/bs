# 站点管理模块

## 功能概述

站点管理模块是多站点CMS系统的核心模块,提供完整的站点创建、配置、管理功能。每个站点可以独立配置域名、语言、时区、主题等信息。

## 已完成功能

### 1. 后端实现

#### 1.1 数据模型 (Site Entity)
- **文件**: `backend/src/main/java/com/cms/module/site/entity/Site.java`
- **核心字段**:
  - `id`: 站点ID
  - `name`: 站点名称
  - `code`: 站点代码(唯一标识)
  - `domain`: 站点域名
  - `description`: 站点描述
  - `logoUrl`: Logo URL
  - `faviconUrl`: Favicon URL
  - `language`: 默认语言(zh_CN, en_US, zh_TW)
  - `timezone`: 时区(Asia/Shanghai等)
  - `status`: 站点状态(ACTIVE, INACTIVE, MAINTENANCE)
  - `isDefault`: 是否默认站点
  - `seoTitle`: SEO标题
  - `seoKeywords`: SEO关键词
  - `seoDescription`: SEO描述
  - `contactEmail`: 联系邮箱
  - `contactPhone`: 联系电话
  - `contactAddress`: 联系地址
  - 继承自BaseEntity的审计字段

#### 1.2 数据访问层 (SiteRepository)
- **文件**: `backend/src/main/java/com/cms/module/site/repository/SiteRepository.java`
- **方法**:
  - `findByCodeAndDeletedFalse`: 根据代码查询站点
  - `findByIsDefaultTrueAndDeletedFalse`: 查询默认站点
  - `existsByCodeAndDeletedFalse`: 检查代码是否存在
  - `existsByDomainAndDeletedFalse`: 检查域名是否存在
  - `findAllByDeletedFalse`: 查询所有站点

#### 1.3 DTO对象
- **SiteDTO**: 站点数据传输对象
  - 包含所有站点字段
  - 包含验证注解(@NotBlank, @Size等)

- **SiteQueryDTO**: 站点查询条件对象
  - `name`: 站点名称(模糊查询)
  - `code`: 站点代码
  - `domain`: 域名
  - `status`: 状态
  - `isDefault`: 是否默认站点
  - 分页和排序参数

- **SiteStatisticsDTO**: 站点统计对象
  - `contentCount`: 内容总数
  - `publishedContentCount`: 已发布内容数
  - `draftContentCount`: 草稿内容数
  - `categoryCount`: 分类总数
  - `userCount`: 用户总数
  - `todayVisits`: 今日访问量
  - `totalVisits`: 总访问量

#### 1.4 业务逻辑层 (SiteService)
- **文件**: `backend/src/main/java/com/cms/module/site/service/SiteService.java`
- **方法**:
  - `createSite(dto)`: 创建站点
  - `updateSite(id, dto)`: 更新站点
  - `deleteSite(id)`: 删除站点(软删除)
  - `getSiteById(id)`: 获取站点详情
  - `getSiteByCode(code)`: 根据代码获取站点
  - `getDefaultSite()`: 获取默认站点
  - `getSites(queryDTO)`: 分页查询站点
  - `getAllSites()`: 获取所有站点
  - `updateSiteStatus(id, status)`: 更新站点状态
  - `setDefaultSite(id)`: 设置默认站点

**业务规则**:
- 站点代码在系统中唯一
- 站点域名在系统中唯一
- 只能有一个默认站点
- 设置新的默认站点时,自动取消其他站点的默认状态
- 删除是软删除,数据不会真正删除

#### 1.5 控制器层 (SiteController)
- **文件**: `backend/src/main/java/com/cms/module/site/controller/SiteController.java`
- **接口**:
  - `POST /sites`: 创建站点
  - `PUT /sites/{id}`: 更新站点
  - `DELETE /sites/{id}`: 删除站点
  - `GET /sites/{id}`: 获取站点详情
  - `GET /sites/code/{code}`: 根据代码获取站点
  - `GET /sites/default`: 获取默认站点
  - `GET /sites`: 分页查询站点
  - `GET /sites/all`: 获取所有站点(不分页)
  - `PATCH /sites/{id}/status`: 更新站点状态
  - `PATCH /sites/{id}/default`: 设置默认站点
  - `GET /sites/{id}/statistics`: 获取站点统计(开发中)

**权限控制**:
- `site:view`: 查看站点
- `site:create`: 创建站点
- `site:update`: 更新站点
- `site:delete`: 删除站点

### 2. 前端实现

#### 2.1 API接口
- **文件**: `frontend/src/api/site.ts`
- **接口**:
  - `getAllSitesApi`: 获取所有站点
  - `getSitesApi`: 分页查询站点
  - `getSiteByIdApi`: 获取站点详情
  - `createSiteApi`: 创建站点
  - `updateSiteApi`: 更新站点
  - `deleteSiteApi`: 删除站点
  - `updateSiteStatusApi`: 更新站点状态
  - `setDefaultSiteApi`: 设置默认站点

#### 2.2 站点管理页面
- **文件**: `frontend/src/views/Sites.vue`
- **功能**:
  - 卡片式展示站点列表
  - 显示站点Logo
  - 显示站点基本信息(代码、域名、描述、语言、时区)
  - 显示站点状态(运行中/已停用)
  - 显示默认站点标识
  - 新增站点
  - 编辑站点
  - 删除站点
  - 站点配置(Logo、Favicon、主题、高级配置)

**新增/编辑表单**:
- 基本信息:
  - 站点名称(必填)
  - 站点代码(必填,只能包含小写字母、数字、下划线、连字符)
  - 站点域名(必填)
  - 站点描述
  - 默认语言(简体中文、English、繁體中文)
  - 时区(Asia/Shanghai等)
  - 站点状态(运行中/已停用)
  - 是否默认站点

- SEO设置:
  - SEO标题
  - SEO关键词
  - SEO描述

- 联系信息:
  - 联系邮箱
  - 联系电话
  - 联系地址

**站点配置对话框**:
- 基本配置:
  - Logo上传
  - Favicon上传

- 主题配置:
  - 主题色选择
  - 辅助色选择

- 高级配置:
  - 自定义CSS
  - 自定义JavaScript

#### 2.3 UI特性
- 响应式卡片布局(每行3个)
- 悬停效果
- 空状态提示
- 加载状态
- 表单验证
- 确认对话框
- 成功/错误提示

### 3. 数据库

#### 3.1 表结构
- **表名**: `sites`
- **索引**:
  - 主键: `id`
  - 唯一索引: `code`, `domain`
  - 普通索引: `status`, `is_default`

#### 3.2 测试数据
系统已有测试站点数据,可以通过以下SQL查看:
```sql
SELECT id, name, code, domain, status FROM sites LIMIT 5;
```

### 4. 权限配置

已在数据库中配置以下权限:
- `site:view`: 查看站点
- `site:create`: 创建站点
- `site:update`: 更新站点
- `site:delete`: 删除站点

权限已分配给:
- 超级管理员: 所有权限
- 站点管理员: 所有权限

## 使用说明

### 创建站点

1. 登录系统
2. 进入"站点管理"页面
3. 点击"新增站点"按钮
4. 填写站点信息:
   - 站点名称(必填)
   - 站点代码(必填,英文小写、数字、下划线、连字符)
   - 站点域名(必填)
   - 其他可选信息
5. 点击"确定"保存

### 编辑站点

1. 在站点卡片上点击"编辑"按钮
2. 修改站点信息
3. 点击"确定"保存

**注意**: 站点代码创建后不能修改

### 配置站点

1. 在站点卡片上点击"配置"按钮
2. 在配置对话框中:
   - **基本配置**: 上传Logo和Favicon
   - **主题配置**: 选择主题色和辅助色(开发中)
   - **高级配置**: 添加自定义CSS和JS(开发中)
3. 点击"保存配置"

### 删除站点

1. 在站点卡片上点击"删除"按钮
2. 确认删除

**注意**: 
- 删除是软删除,数据不会真正删除
- 删除站点前应先删除该站点下的所有内容和分类

### 设置默认站点

1. 编辑站点
2. 勾选"默认站点"选项
3. 保存

**注意**: 系统只能有一个默认站点

## 技术特点

1. **多站点隔离**: 每个站点的数据完全隔离
2. **软删除**: 删除的站点可以恢复
3. **权限控制**: 基于Spring Security的细粒度权限控制
4. **数据验证**: 前后端双重验证
5. **乐观锁**: 使用version字段防止并发更新冲突
6. **审计功能**: 自动记录创建人、修改人、创建时间、修改时间
7. **文件上传**: 支持Logo和Favicon上传
8. **响应式设计**: 适配不同屏幕尺寸

## 后续优化建议

### 功能增强
1. ✅ 添加站点统计信息(内容数、分类数、访问量等)
2. 添加站点复制功能
3. 添加站点导入/导出功能
4. 添加站点模板功能
5. 添加站点备份/恢复功能
6. 实现主题配置功能
7. 实现自定义CSS/JS功能
8. 添加站点访问日志
9. 添加站点性能监控

### 用户体验
1. 添加站点预览功能
2. 添加站点搜索功能
3. 添加批量操作(批量删除、批量修改状态)
4. 添加站点排序功能
5. 优化卡片布局(支持列表视图/卡片视图切换)

### 技术优化
1. 添加Redis缓存站点配置
2. 优化站点查询性能
3. 添加站点配置版本控制
4. 实现站点配置的热更新

