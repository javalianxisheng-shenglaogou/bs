# 系统功能结构图 - Mermaid代码

本文件包含可以生成系统功能结构图的Mermaid代码。

## 免费生成图片的网站

1. **Mermaid Live Editor**（推荐）
   - 网址：https://mermaid.live/
   - 使用：复制代码 → 粘贴 → 导出PNG/SVG

2. **Draw.io**
   - 网址：https://app.diagrams.net/
   - 使用：Arrange → Insert → Advanced → Mermaid

3. **Markdown编辑器**
   - Typora、VS Code、Obsidian等

---

## 系统功能结构图（思维导图）

```mermaid
mindmap
  root((多站点CMS系统))
    用户管理
      用户管理
        用户列表
        新增用户
        编辑用户
        删除用户
        重置密码
      角色管理
        角色列表
        新增角色
        编辑角色
        删除角色
        分配权限
      权限管理
        权限列表
        新增权限
        编辑权限
        删除权限
    站点管理
      站点列表
        查看站点
        搜索站点
        筛选站点
      站点配置
        基本信息
        SEO配置
        联系信息
      站点操作
        新增站点
        编辑站点
        删除站点
        启用禁用
    内容管理
      内容列表
        查看内容
        搜索内容
        筛选内容
      内容编辑
        创建内容
        编辑内容
        删除内容
        发布内容
      版本控制
        版本历史
        版本比较
        版本恢复
        版本标记
        版本删除
      分类管理
        分类树
        新增分类
        编辑分类
        删除分类
    工作流管理
      工作流配置
        工作流列表
        新增工作流
        编辑工作流
        删除工作流
      节点配置
        审批节点
        条件节点
        开始结束
      审批任务
        待办任务
        已办任务
        审批操作
        转办任务
    系统管理
      操作日志
        日志列表
        日志详情
        日志导出
      系统配置
        配置列表
        配置编辑
      文件管理
        文件上传
        文件列表
        文件删除
      个人中心
        个人信息
        修改密码
        语言切换
```

---

## 系统架构图

```mermaid
graph TB
    subgraph "前端层 Frontend"
        A[Vue 3 + TypeScript]
        B[Element Plus UI]
        C[Vue Router]
        D[Pinia 状态管理]
        E[Vue I18n 国际化]
        F[Axios HTTP客户端]
    end
    
    subgraph "后端层 Backend"
        G[Spring Boot 2.7]
        H[Spring Security + JWT]
        I[Spring Data JPA]
        J[Hibernate ORM]
        K[Flyway 数据库迁移]
        L[Logback 日志]
    end
    
    subgraph "数据层 Database"
        M[(MySQL 8.0)]
    end
    
    subgraph "文件存储 Storage"
        N[本地文件系统]
        O[OSS/S3 可选]
    end
    
    A --> F
    F --> G
    G --> H
    G --> I
    I --> J
    J --> M
    G --> K
    K --> M
    G --> N
    G --> O
    
    style A fill:#42b983
    style G fill:#6db33f
    style M fill:#4479a1
```

---

## 技术架构图（分层）

```mermaid
graph LR
    subgraph "表现层 Presentation"
        A1[用户界面]
        A2[路由管理]
        A3[状态管理]
    end
    
    subgraph "业务层 Business"
        B1[用户服务]
        B2[站点服务]
        B3[内容服务]
        B4[工作流服务]
    end
    
    subgraph "持久层 Persistence"
        C1[用户Repository]
        C2[站点Repository]
        C3[内容Repository]
        C4[工作流Repository]
    end
    
    subgraph "数据层 Data"
        D1[(数据库)]
    end
    
    A1 --> B1
    A1 --> B2
    A1 --> B3
    A1 --> B4
    
    B1 --> C1
    B2 --> C2
    B3 --> C3
    B4 --> C4
    
    C1 --> D1
    C2 --> D1
    C3 --> D1
    C4 --> D1
```

---

## 用户权限模型图

```mermaid
graph TD
    A[用户 User] -->|拥有| B[角色 Role]
    B -->|拥有| C[权限 Permission]
    C -->|控制| D[资源 Resource]
    
    A1[用户1: admin] -.->|分配| B1[角色: 管理员]
    A2[用户2: editor] -.->|分配| B2[角色: 编辑]
    A3[用户3: viewer] -.->|分配| B3[角色: 访客]
    
    B1 -.->|拥有| C1[权限: user:*]
    B1 -.->|拥有| C2[权限: content:*]
    B2 -.->|拥有| C3[权限: content:create]
    B2 -.->|拥有| C4[权限: content:update]
    B3 -.->|拥有| C5[权限: content:view]
    
    style A fill:#e1f5ff
    style B fill:#fff4e6
    style C fill:#f3e5f5
    style D fill:#e8f5e9
```

---

## 内容发布流程图

```mermaid
flowchart TD
    Start([开始]) --> Create[创建内容]
    Create --> Draft{保存为草稿?}
    
    Draft -->|是| SaveDraft[保存草稿]
    Draft -->|否| Submit[提交审核]
    
    SaveDraft --> Edit[继续编辑]
    Edit --> Submit
    
    Submit --> CreateVersion[创建版本v1]
    CreateVersion --> StartWorkflow[启动工作流]
    
    StartWorkflow --> Approve{审批通过?}
    
    Approve -->|通过| Publish[发布内容]
    Approve -->|拒绝| Reject[退回修改]
    
    Reject --> Edit
    
    Publish --> CreatePublishVersion[创建发布版本]
    CreatePublishVersion --> End([结束])
    
    style Start fill:#e8f5e9
    style End fill:#ffebee
    style Publish fill:#c8e6c9
    style Reject fill:#ffcdd2
```

---

## 版本控制流程图

```mermaid
sequenceDiagram
    participant U as 用户
    participant F as 前端
    participant B as 后端
    participant D as 数据库
    
    U->>F: 编辑内容
    F->>B: 提交保存请求
    B->>B: 检查权限
    B->>D: 保存内容
    B->>D: 创建新版本快照
    D-->>B: 返回版本号
    B-->>F: 返回成功
    F-->>U: 显示版本号
    
    U->>F: 查看版本历史
    F->>B: 请求版本列表
    B->>D: 查询版本
    D-->>B: 返回版本列表
    B-->>F: 返回数据
    F-->>U: 显示版本历史
    
    U->>F: 恢复到v3
    F->>B: 请求恢复版本
    B->>D: 读取v3快照
    B->>D: 创建新版本v5
    B->>D: 更新内容
    D-->>B: 返回成功
    B-->>F: 返回成功
    F-->>U: 显示恢复成功
```

---

## 工作流执行流程图

```mermaid
stateDiagram-v2
    [*] --> 草稿: 创建内容
    草稿 --> 待审核: 提交审核
    
    待审核 --> 审核中: 分配审核人
    审核中 --> 审核通过: 批准
    审核中 --> 审核拒绝: 拒绝
    审核中 --> 转办: 转办
    
    转办 --> 审核中: 重新分配
    
    审核拒绝 --> 草稿: 退回修改
    审核通过 --> 已发布: 发布
    
    已发布 --> 已下线: 下线
    已下线 --> 已发布: 重新发布
    
    已发布 --> [*]
    已下线 --> [*]
```

---

## 数据流图

```mermaid
graph LR
    A[用户请求] --> B{路由}
    B --> C[Controller层]
    C --> D[Service层]
    D --> E[Repository层]
    E --> F[(数据库)]
    
    F --> E
    E --> D
    D --> G[DTO转换]
    G --> C
    C --> H[JSON响应]
    H --> I[前端渲染]
    
    style A fill:#e3f2fd
    style F fill:#fff3e0
    style I fill:#f3e5f5
```

---

## 部署架构图

```mermaid
graph TB
    subgraph "客户端 Client"
        A[浏览器]
    end
    
    subgraph "Web服务器 Web Server"
        B[Nginx]
        C[静态资源]
    end
    
    subgraph "应用服务器 Application Server"
        D[Spring Boot]
        E[Tomcat]
    end
    
    subgraph "数据库服务器 Database Server"
        F[(MySQL)]
        G[Redis缓存]
    end
    
    subgraph "文件服务器 File Server"
        H[文件存储]
    end
    
    A -->|HTTPS| B
    B --> C
    B -->|反向代理| D
    D --> E
    D --> F
    D --> G
    D --> H
    
    style A fill:#e1f5ff
    style B fill:#fff4e6
    style D fill:#e8f5e9
    style F fill:#f3e5f5
```

---

## 模块依赖关系图

```mermaid
graph TD
    A[前端应用] --> B[API网关]
    B --> C[用户模块]
    B --> D[站点模块]
    B --> E[内容模块]
    B --> F[工作流模块]
    B --> G[系统模块]
    
    E --> C
    E --> D
    F --> C
    F --> E
    G --> C
    
    C --> H[数据库]
    D --> H
    E --> H
    F --> H
    G --> H
    
    style A fill:#42b983
    style B fill:#ffd700
    style H fill:#4479a1
```

---

## 使用步骤

### 1. 在线生成（最简单）

1. 访问：https://mermaid.live/
2. 复制上面任意一个代码块
3. 粘贴到左侧编辑器
4. 右侧自动显示图表
5. 点击右上角"Actions" → "Download PNG"

### 2. 使用Draw.io

1. 访问：https://app.diagrams.net/
2. 点击菜单：Arrange → Insert → Advanced → Mermaid
3. 粘贴代码
4. 点击"Insert"
5. 可以进一步编辑和美化
6. 导出为PNG/PDF

### 3. 使用Typora（本地）

1. 打开Typora
2. 创建新文档
3. 插入代码块，语言选择`mermaid`
4. 粘贴代码
5. 自动渲染
6. 右键导出为图片

---

## 图表说明

| 图表类型 | 用途 | 推荐场景 |
|---------|------|---------|
| mindmap | 思维导图 | 功能结构展示 |
| graph | 流程图/架构图 | 系统架构、数据流 |
| flowchart | 流程图 | 业务流程 |
| sequenceDiagram | 时序图 | 交互流程 |
| stateDiagram | 状态图 | 状态转换 |

---

**创建日期**：2025-10-07  
**工具版本**：Mermaid 10.x

