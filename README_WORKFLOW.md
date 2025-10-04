# 工作流功能说明

## 功能概述

多站点CMS系统现已完整实现工作流管理功能,支持灵活的审批流程配置和管理。

## 快速开始

### 1. 启动系统

```bash
# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm run dev
```

### 2. 访问系统

- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080
- 默认账号: admin / admin123

### 3. 访问工作流功能

登录后,在左侧菜单中可以看到:
- **工作流管理** - 管理工作流定义
- **工作流实例** - 查看工作流执行情况
- **我的任务** - 处理待办任务

## 核心功能

### 1. 工作流管理 (`/workflows`)

**功能:**
- 创建工作流定义
- 编辑工作流信息
- 设计工作流流程
- 启用/停用工作流
- 删除工作流

**操作步骤:**
1. 点击"新建工作流"按钮
2. 填写工作流信息:
   - 工作流名称: 如"内容审批流程"
   - 工作流代码: 如"content_approval"(唯一标识)
   - 工作流类型: 选择"内容审批"
   - 状态: 选择"草稿"
3. 点击"确定"保存
4. 点击"设计"按钮进入设计器

### 2. 工作流设计器 (`/workflows/:id/design`)

**功能:**
- 拖拽式节点设计
- 配置节点属性
- 选择审批人
- 保存工作流设计

**操作步骤:**
1. 从左侧工具栏拖拽节点到画布:
   - 开始节点
   - 审批节点
   - 条件节点
   - 结束节点
2. 点击节点配置属性:
   - 节点名称
   - 审批人类型(指定用户/指定角色/自定义)
   - 审批人列表
   - 审批模式(任意一人/所有人/按顺序)
3. 点击"保存"保存设计
4. 返回工作流列表,点击"激活"启用工作流

### 3. 工作流实例 (`/workflow-instances`)

**功能:**
- 查看所有工作流实例
- 查看实例详情
- 取消运行中的实例
- 按条件筛选实例

**实例状态:**
- 🔵 运行中 - 工作流正在执行
- ✅ 已通过 - 工作流审批通过
- ❌ 已拒绝 - 工作流被拒绝
- ⚪ 已取消 - 工作流被取消
- ⚠️ 错误 - 工作流执行出错

### 4. 我的任务 (`/workflow-tasks`)

**功能:**
- 查看待办任务
- 查看已办任务
- 审批通过/拒绝
- 查看任务详情

**操作步骤:**
1. 在"待办任务"标签页查看分配给自己的任务
2. 点击"通过"或"拒绝"按钮
3. 填写审批意见
4. 确认审批
5. 在"已办任务"标签页查看已处理的任务

## API接口

### 工作流定义API

```
POST   /api/workflows              创建工作流
GET    /api/workflows              分页查询工作流
GET    /api/workflows/{id}         获取工作流详情
GET    /api/workflows/code/{code}  根据代码获取工作流
GET    /api/workflows/all          获取所有工作流
PUT    /api/workflows/{id}         更新工作流
PUT    /api/workflows/{id}/status  更新工作流状态
DELETE /api/workflows/{id}         删除工作流
```

### 工作流实例API

```
POST   /api/workflow-instances                启动工作流
GET    /api/workflow-instances                分页查询实例
GET    /api/workflow-instances/{id}           获取实例详情
POST   /api/workflow-instances/{id}/cancel    取消实例
```

### 工作流任务API

```
GET    /api/workflow-tasks/pending            获取待办任务
GET    /api/workflow-tasks/completed          获取已办任务
GET    /api/workflow-tasks/{id}               获取任务详情
POST   /api/workflow-tasks/{id}/approve       通过任务
POST   /api/workflow-tasks/{id}/reject        拒绝任务
```

## 使用示例

### 示例1: 创建内容审批工作流

1. **创建工作流定义**
   - 访问 `/workflows`
   - 点击"新建工作流"
   - 填写信息并保存

2. **设计工作流**
   - 点击"设计"按钮
   - 拖拽节点:
     - 开始节点
     - 审批节点1: 部门主管审批
     - 审批节点2: 总编辑审批
     - 结束节点
   - 配置审批人
   - 保存设计

3. **激活工作流**
   - 返回列表
   - 点击"激活"

4. **启动工作流实例**
   ```bash
   POST /api/workflow-instances
   {
     "workflowCode": "content_approval",
     "businessType": "CONTENT",
     "businessId": 1,
     "businessTitle": "新闻标题"
   }
   ```

5. **审批任务**
   - 审批人访问 `/workflow-tasks`
   - 查看待办任务
   - 点击"通过"或"拒绝"
   - 填写审批意见
   - 确认

### 示例2: 查看工作流执行情况

1. 访问 `/workflow-instances`
2. 查看所有工作流实例
3. 点击"详情"查看实例信息
4. 查看当前节点和执行状态

## 测试数据

系统已预置测试工作流"内容审批流程":
- 工作流代码: `content_approval`
- 工作流类型: 内容审批
- 状态: 激活
- 节点:
  1. 开始节点
  2. 部门主管审批 (审批人: admin)
  3. 总编辑审批 (审批人: admin)
  4. 结束节点

可以直接使用此工作流进行测试。

## 权限说明

### 工作流管理权限

- `workflow:list` - 查看工作流列表
- `workflow:view` - 查看工作流详情
- `workflow:create` - 创建工作流
- `workflow:update` - 更新工作流
- `workflow:delete` - 删除工作流

### 任务审批权限

- 所有登录用户都可以查看和处理自己的任务
- 无需额外权限配置

### 默认权限

管理员角色(`ADMIN`)默认拥有所有工作流管理权限。

## 技术架构

### 后端技术栈

- Spring Boot 2.7.18
- Spring Data JPA
- MySQL 8.0
- Spring Security + JWT
- Flyway (数据库迁移)

### 前端技术栈

- Vue 3
- TypeScript
- Element Plus
- Pinia
- Vite

### 核心设计

- **循环依赖解决**: 使用ApplicationContext延迟获取bean
- **JSON存储**: 使用JSON字段存储审批人列表
- **拖拽设计器**: 基于HTML5 Drag & Drop API
- **响应式处理**: 正确处理Axios响应拦截器

## 文件结构

```
backend/
├── src/main/java/com/cms/module/workflow/
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── repository/      # 仓库接口
│   ├── service/         # 服务类
│   └── controller/      # 控制器
└── src/main/resources/db/migration/
    ├── V1.0.3__init_workflow_tables.sql
    ├── V1.1.8__Add_workflow_permissions.sql
    └── V1.1.9__Add_test_workflow_data.sql

frontend/
├── src/api/
│   └── workflow.ts      # 工作流API
└── src/views/
    ├── Workflows.vue           # 工作流管理
    ├── WorkflowDesigner.vue    # 工作流设计器
    ├── WorkflowInstances.vue   # 工作流实例
    └── WorkflowTasks.vue       # 我的任务

docs/
├── 工作流功能设计.md
├── 工作流功能使用指南.md
└── 工作流功能开发总结.md
```

## 常见问题

### Q1: 如何创建工作流?

A: 访问 `/workflows` 页面,点击"新建工作流",填写信息后保存。

### Q2: 如何设计工作流?

A: 在工作流列表点击"设计",进入设计器,拖拽节点到画布,配置属性后保存。

### Q3: 如何启动工作流?

A: 通过API调用 `POST /api/workflow-instances`,传入工作流代码和业务信息。

### Q4: 如何审批任务?

A: 访问 `/workflow-tasks`,在待办任务列表中找到任务,点击"通过"或"拒绝"。

### Q5: 如何查看工作流执行情况?

A: 访问 `/workflow-instances`,查看所有工作流实例的执行状态。

## 后续计划

- [ ] 节点连线可视化
- [ ] 条件分支逻辑
- [ ] 并行审批支持
- [ ] 任务转办功能
- [ ] 超时提醒
- [ ] 邮件/短信通知
- [ ] 统计报表

## 技术支持

如有问题,请查看:
- `docs/工作流功能设计.md` - 详细设计文档
- `docs/工作流功能使用指南.md` - 使用指南
- `docs/工作流功能开发总结.md` - 开发总结

## 许可证

本项目仅供学习和研究使用。

