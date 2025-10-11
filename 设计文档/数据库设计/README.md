# 数据库设计目录

本目录存放多站点CMS系统的数据库设计文档、ER图和数据字典。

## 📋 目录说明

本目录包含数据库的完整设计文档，包括逻辑设计、物理设计、ER图和数据字典。

## 📁 文件组织

### 推荐的文件结构

```
数据库设计/
├── README.md                        # 本文件
├── 数据字典-v1.0.xlsx               # Excel格式数据字典
├── 数据字典-v1.0.md                 # Markdown格式数据字典
├── ER图-v1.0.png                    # 实体关系图（导出图片）
├── ER图-v1.0.drawio                 # ER图源文件
├── 逻辑结构设计-v1.0.md             # 逻辑结构设计文档
├── 物理结构设计-v1.0.md             # 物理结构设计文档
├── 索引设计-v1.0.md                 # 索引设计文档
├── 数据库优化方案-v1.0.md           # 性能优化方案
└── SQL脚本/                         # SQL脚本文件
    ├── 建表脚本.sql
    ├── 初始化数据.sql
    └── 索引脚本.sql
```

## 📝 文件命名规范

### 命名格式
```
文档类型-版本号.扩展名
```

### 命名示例
- `数据字典-v1.0.xlsx`
- `ER图-v2.0.png`
- `逻辑结构设计-v1.1.md`

## 📊 文件说明

### 1. 数据字典
**用途**：详细记录所有数据库表和字段的定义

**内容包括**：
- 表名和表说明
- 字段名和字段说明
- 数据类型和长度
- 是否为空
- 默认值
- 主键/外键标识
- 索引信息
- 备注说明

**格式**：
- Excel格式（`.xlsx`）- 便于编辑和查看
- Markdown格式（`.md`）- 便于版本控制

### 2. ER图（实体关系图）
**用途**：展示数据库实体及其关系

**内容包括**：
- 所有数据表（实体）
- 表之间的关系（一对一、一对多、多对多）
- 主键和外键
- 关键字段

**格式**：
- 源文件：`.drawio`, `.vsdx`, `.mwb`（MySQL Workbench）
- 导出图片：`.png`, `.pdf`

### 3. 逻辑结构设计
**用途**：描述数据库的逻辑结构设计

**内容包括**：
- 数据库设计原则
- 表的逻辑设计
- 关系设计
- 约束设计
- 规范化分析

### 4. 物理结构设计
**用途**：描述数据库的物理实现

**内容包括**：
- 存储引擎选择（InnoDB、MyISAM等）
- 字符集和排序规则
- 分区设计
- 表空间设计
- 文件组织

### 5. 索引设计
**用途**：描述数据库索引的设计和优化

**内容包括**：
- 主键索引
- 唯一索引
- 普通索引
- 全文索引
- 组合索引
- 索引优化建议

### 6. 数据库优化方案
**用途**：描述数据库性能优化方案

**内容包括**：
- 查询优化
- 索引优化
- 表结构优化
- 缓存策略
- 分库分表方案

## 🗂️ 数据库表清单

### 核心业务表

#### 用户管理
- [ ] `users` - 用户表
- [ ] `roles` - 角色表
- [ ] `permissions` - 权限表
- [ ] `user_roles` - 用户角色关联表
- [ ] `role_permissions` - 角色权限关联表

#### 站点管理
- [ ] `sites` - 站点表
- [ ] `site_configs` - 站点配置表

#### 内容管理
- [ ] `contents` - 内容表
- [ ] `content_versions` - 内容版本表
- [ ] `categories` - 分类表
- [ ] `tags` - 标签表
- [ ] `content_tags` - 内容标签关联表

#### 工作流管理
- [ ] `workflows` - 工作流表
- [ ] `workflow_nodes` - 工作流节点表
- [ ] `workflow_instances` - 工作流实例表
- [ ] `workflow_tasks` - 工作流任务表

#### 系统管理
- [ ] `system_logs` - 系统日志表
- [ ] `operation_logs` - 操作日志表
- [ ] `files` - 文件表

## 📐 设计规范

### 命名规范

#### 表名
- 使用小写字母
- 使用下划线分隔单词
- 使用复数形式
- 示例：`users`, `content_versions`, `workflow_tasks`

#### 字段名
- 使用小写字母
- 使用下划线分隔单词
- 避免使用保留字
- 示例：`user_id`, `created_at`, `is_active`

#### 索引名
- 格式：`idx_表名_字段名`
- 示例：`idx_users_username`, `idx_contents_site_id`

#### 外键名
- 格式：`fk_表名_关联表名`
- 示例：`fk_contents_users`, `fk_user_roles_users`

### 字段规范

#### 主键
- 统一使用 `id` 作为主键名
- 类型：`BIGINT` 或 `INT`
- 自增：`AUTO_INCREMENT`

#### 外键
- 命名格式：`关联表名_id`
- 示例：`user_id`, `site_id`, `category_id`

#### 时间字段
- 创建时间：`created_at` (DATETIME)
- 更新时间：`updated_at` (DATETIME)
- 删除时间：`deleted_at` (DATETIME, 可为空)

#### 状态字段
- 使用 `TINYINT` 类型
- 0表示否，1表示是
- 示例：`is_active`, `is_deleted`, `is_published`

#### 文本字段
- 短文本：`VARCHAR(255)`
- 中等文本：`VARCHAR(1000)` 或 `TEXT`
- 长文本：`TEXT` 或 `LONGTEXT`

### 数据类型选择

| 数据类型 | 使用场景 | 示例 |
|---------|---------|------|
| `BIGINT` | 主键、大数值 | `id`, `user_id` |
| `INT` | 普通数值 | `view_count`, `sort_order` |
| `TINYINT` | 状态、布尔值 | `is_active`, `status` |
| `VARCHAR` | 短文本 | `username`, `title` |
| `TEXT` | 长文本 | `content`, `description` |
| `DATETIME` | 日期时间 | `created_at`, `updated_at` |
| `DECIMAL` | 金额、精确数值 | `price`, `amount` |

## 🔄 版本管理

### 版本号规则
- **v1.0** - 初始版本
- **v1.1** - 小的修改（如添加字段、修改注释）
- **v2.0** - 重大变更（如重构表结构、修改关系）

### 版本记录

| 版本号 | 更新日期 | 更新内容 | 更新人 |
|--------|----------|----------|--------|
| v1.0 | 2025-10-07 | 创建数据库设计目录 | 开发团队 |

## 📋 数据字典模板

### Excel格式模板

| 表名 | 字段名 | 数据类型 | 长度 | 允许空 | 默认值 | 主键 | 外键 | 索引 | 说明 |
|------|--------|----------|------|--------|--------|------|------|------|------|
| users | id | BIGINT | - | NO | - | PK | - | - | 用户ID |
| users | username | VARCHAR | 50 | NO | - | - | - | UNI | 用户名 |
| users | password | VARCHAR | 255 | NO | - | - | - | - | 密码（加密） |
| users | email | VARCHAR | 100 | YES | NULL | - | - | IDX | 邮箱 |
| users | created_at | DATETIME | - | NO | CURRENT_TIMESTAMP | - | - | - | 创建时间 |

### Markdown格式模板

```markdown
## users（用户表）

**表说明**：存储系统用户信息

| 字段名 | 类型 | 长度 | 允许空 | 默认值 | 说明 |
|--------|------|------|--------|--------|------|
| id | BIGINT | - | NO | AUTO_INCREMENT | 用户ID（主键） |
| username | VARCHAR | 50 | NO | - | 用户名（唯一） |
| password | VARCHAR | 255 | NO | - | 密码（BCrypt加密） |
| email | VARCHAR | 100 | YES | NULL | 邮箱 |
| created_at | DATETIME | - | NO | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | - | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_username` (`username`)
- KEY `idx_email` (`email`)

**外键**：无
```

## 🛠️ 推荐工具

### 数据库设计工具
- **MySQL Workbench** - MySQL官方工具，支持ER图设计
- **Navicat** - 强大的数据库管理工具
- **DBeaver** - 免费开源的数据库工具
- **Draw.io** - 在线绘制ER图

### 文档工具
- **Microsoft Excel** - 编辑数据字典
- **Markdown编辑器** - 编辑文档
- **PDManer** - 国产数据库建模工具

## 📌 注意事项

1. **规范性**：
   - 严格遵循命名规范
   - 保持一致的设计风格
   - 添加完整的注释说明

2. **完整性**：
   - 记录所有表和字段
   - 标注所有关系和约束
   - 包含索引和外键信息

3. **可维护性**：
   - 保持文档与实际数据库同步
   - 及时更新版本号
   - 记录变更历史

4. **安全性**：
   - 不在文档中包含真实数据
   - 不包含敏感配置信息
   - 注意数据隐私保护

## 🔗 相关文档

- [数据库初始化脚本](../../database/init_database.sql)
- [Flyway迁移脚本](../../backend/src/main/resources/db/migration/)
- [技术实现文档](../../docs/技术实现文档.md)

## 📞 联系方式

如有问题或建议，请联系项目负责人。

---

**最后更新时间**：2025-10-07

