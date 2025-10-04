# 分类管理模块

## 功能概述

分类管理模块提供了完整的内容分类管理功能,支持树形结构的分类组织,每个站点可以独立管理自己的分类体系。

## 已完成功能

### 1. 后端实现

#### 1.1 数据模型 (Category Entity)
- **文件**: `backend/src/main/java/com/cms/module/category/entity/Category.java`
- **字段**:
  - `id`: 分类ID
  - `siteId`: 所属站点ID
  - `parentId`: 父分类ID
  - `name`: 分类名称
  - `code`: 分类编码(唯一)
  - `description`: 分类描述
  - `iconUrl`: 图标URL
  - `coverUrl`: 封面图URL
  - `sortOrder`: 排序号
  - `level`: 层级(1为顶级)
  - `path`: 分类路径(如: /1/2/3)
  - `isVisible`: 是否可见
  - `seoTitle`: SEO标题
  - `seoKeywords`: SEO关键词
  - `seoDescription`: SEO描述
  - 继承自BaseEntity的审计字段

#### 1.2 数据访问层 (CategoryRepository)
- **文件**: `backend/src/main/java/com/cms/module/category/repository/CategoryRepository.java`
- **方法**:
  - `findBySiteIdAndDeletedFalseOrderBySortOrderAsc`: 查询站点所有分类
  - `findBySiteIdAndParentIdAndDeletedFalseOrderBySortOrderAsc`: 查询子分类
  - `findBySiteIdAndCodeAndDeletedFalse`: 根据编码查询
  - `findBySiteIdAndIsVisibleAndDeletedFalseOrderBySortOrderAsc`: 查询可见分类
  - `findAllChildren`: 递归查询所有子分类
  - `existsByCodeExcludingId`: 检查编码是否重复(排除自己)
  - `existsBySiteIdAndCodeAndDeletedFalse`: 检查编码是否存在

#### 1.3 DTO对象
- **CategoryDTO**: 分类数据传输对象
  - 包含所有分类字段
  - 支持树形结构(children字段)
  - 包含验证注解

- **CategoryQueryDTO**: 分类查询条件对象
  - `siteId`: 站点ID
  - `parentId`: 父分类ID
  - `name`: 分类名称(模糊查询)
  - `code`: 分类编码
  - `isVisible`: 是否可见
  - `level`: 层级

#### 1.4 业务逻辑层 (CategoryService)
- **文件**: `backend/src/main/java/com/cms/module/category/service/CategoryService.java`
- **方法**:
  - `getCategoryTree(siteId)`: 获取分类树
  - `getVisibleCategoryTree(siteId)`: 获取可见分类树
  - `getCategories(queryDTO, pageable)`: 分页查询分类
  - `getCategoryById(id)`: 获取分类详情
  - `createCategory(dto)`: 创建分类
  - `updateCategory(id, dto)`: 更新分类
  - `deleteCategory(id)`: 删除分类(软删除)
  - `updateVisibility(id, isVisible)`: 更新可见性

**业务规则**:
- 创建时自动计算层级和路径
- 编码在同一站点内唯一
- 删除前检查是否有子分类
- 不允许修改站点ID和父分类ID(避免破坏树形结构)

#### 1.5 控制器层 (CategoryController)
- **文件**: `backend/src/main/java/com/cms/module/category/controller/CategoryController.java`
- **接口**:
  - `GET /categories/tree`: 获取分类树
  - `GET /categories/tree/visible`: 获取可见分类树
  - `GET /categories`: 分页查询分类
  - `GET /categories/{id}`: 获取分类详情
  - `POST /categories`: 创建分类
  - `PUT /categories/{id}`: 更新分类
  - `DELETE /categories/{id}`: 删除分类
  - `PATCH /categories/{id}/visibility`: 更新可见性

**权限控制**:
- `category:list`: 查看分类
- `category:create`: 创建分类
- `category:update`: 更新分类
- `category:delete`: 删除分类

### 2. 前端实现

#### 2.1 API接口
- **文件**: `frontend/src/api/category.ts`
- **接口**:
  - `getCategoryTreeApi`: 获取分类树
  - `getVisibleCategoryTreeApi`: 获取可见分类树
  - `getCategoriesApi`: 分页查询
  - `getCategoryByIdApi`: 获取详情
  - `createCategoryApi`: 创建分类
  - `updateCategoryApi`: 更新分类
  - `deleteCategoryApi`: 删除分类
  - `updateCategoryVisibilityApi`: 更新可见性

#### 2.2 分类管理页面
- **文件**: `frontend/src/views/Categories.vue`
- **功能**:
  - 站点选择器
  - 树形表格展示分类
  - 新增顶级分类
  - 添加子分类
  - 编辑分类
  - 删除分类
  - 切换可见性
  - 表单验证

**UI组件**:
- 使用Element Plus的Table组件展示树形数据
- 使用TreeSelect组件选择父分类
- 使用Dialog组件进行新增/编辑操作

#### 2.3 路由配置
- **路径**: `/categories`
- **名称**: Categories
- **图标**: FolderOpened
- **标题**: 分类管理

### 3. 数据库

#### 3.1 表结构
- **表名**: `categories`
- **索引**:
  - 主键: `id`
  - 唯一索引: `site_id + code`
  - 普通索引: `site_id`, `parent_id`, `level`, `is_visible`

#### 3.2 测试数据
已插入3条测试分类:
- 新闻资讯 (code: news)
- 产品中心 (code: products)
- 关于我们 (code: about)

### 4. 权限配置

已在数据库中配置以下权限:
- `category:list`: 查看分类列表
- `category:create`: 创建分类
- `category:update`: 编辑分类
- `category:delete`: 删除分类

权限已分配给:
- 超级管理员: 所有权限
- 站点管理员: 所有权限
- 编辑: 查看和创建权限

## 使用说明

### 创建分类

1. 登录系统
2. 进入"分类管理"页面
3. 选择站点
4. 点击"新增分类"按钮
5. 填写分类信息:
   - 分类名称(必填)
   - 分类编码(必填,英文字母、数字、下划线)
   - 父分类(可选,不选则为顶级分类)
   - 描述(可选)
   - 排序(可选)
   - 是否可见(默认可见)
6. 点击"确定"保存

### 添加子分类

1. 在分类列表中找到父分类
2. 点击"添加子分类"按钮
3. 填写子分类信息
4. 点击"确定"保存

### 编辑分类

1. 在分类列表中找到要编辑的分类
2. 点击"编辑"按钮
3. 修改分类信息
4. 点击"确定"保存

**注意**: 不能修改站点ID和父分类ID

### 删除分类

1. 在分类列表中找到要删除的分类
2. 点击"删除"按钮
3. 确认删除

**注意**: 
- 删除是软删除,数据不会真正删除
- 如果分类下有子分类,无法删除

### 切换可见性

1. 在分类列表中找到要切换的分类
2. 点击"显示"或"隐藏"按钮
3. 分类可见性立即更新

## 技术特点

1. **树形结构**: 支持无限层级的分类树
2. **软删除**: 删除的分类可以恢复
3. **权限控制**: 基于Spring Security的细粒度权限控制
4. **数据验证**: 前后端双重验证
5. **乐观锁**: 使用version字段防止并发更新冲突
6. **审计功能**: 自动记录创建人、修改人、创建时间、修改时间

## 后续优化建议

1. 添加分类排序功能(拖拽排序)
2. 添加批量操作(批量删除、批量修改可见性)
3. 添加分类导入/导出功能
4. 添加分类图标和封面图上传
5. 添加SEO信息编辑
6. 添加分类关联内容统计
7. 添加分类移动功能(修改父分类)
8. 添加分类复制功能

