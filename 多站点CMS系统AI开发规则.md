# 基于Spring Boot的多站点内容管理系统的设计与实现 - AI开发规则文档

## 项目基本信息
- **项目名称**：基于Spring Boot的多站点内容管理系统的设计与实现
- **开发模式**：前后端分离
- **前端技术栈**：Vue.js 3 + Element Plus + Pinia + Vue Router
- **后端技术栈**：Spring Boot 3 + Spring Security + Spring Data JPA + MySQL + Swagger

---

## 开发优先级和顺序规则

### 阶段1：后端基础架构（优先级：最高）
**原因**：后端提供数据接口，前端依赖后端API，必须先建立稳定的后端基础

#### 1.1 数据库设计与搭建（第1周）
```sql
-- 优先级顺序：
1. 用户和权限表（users, roles, user_roles）
2. 站点管理表（sites, site_configs）
3. 内容管理表（contents, content_versions）
4. 工作流表（workflows, workflow_steps, workflow_instances）
5. 多语言表（languages, content_translations）
```

#### 1.2 后端核心模块开发（第2-4周）
```java
// 开发顺序：
1. 用户认证模块（JWT + Spring Security）
2. 基础CRUD操作（BaseEntity, BaseService, BaseController）
3. 站点管理模块
4. 内容管理模块
5. 工作流引擎
6. 版本控制系统
7. 多语言支持
```

### 阶段2：前端基础框架（优先级：高）
**原因**：在后端API稳定后，搭建前端框架进行联调测试

#### 2.1 前端项目搭建（第3周）
```javascript
// 搭建顺序：
1. Vue.js项目初始化
2. 路由配置（Vue Router）
3. 状态管理（Pinia）
4. UI组件库集成（Element Plus）
5. HTTP请求封装（Axios）
6. 权限控制系统
```

#### 2.2 前端页面开发（第4-6周）
```javascript
// 开发顺序：
1. 登录/注册页面
2. 主框架布局（Header, Sidebar, Main）
3. 站点管理页面
4. 内容管理页面
5. 工作流管理页面
6. 多语言管理页面
```

### 阶段3：功能集成与优化（优先级：中）
#### 3.1 前后端联调（第5-7周）
#### 3.2 功能测试与优化（第8-9周）

---

## 核心功能开发规则

### 1. 多站点管理模块

#### 1.1 后端开发规则
```java
// 实体设计规则
@Entity
public class Site {
    // 必须字段：id, name, domain, status, created_at
    // 配置字段：theme, logo, favicon, seo_config
    // 关联字段：owner_id, parent_site_id
}

// API设计规则
@RestController
@RequestMapping("/api/v1/sites")
public class SiteController {
    // GET /api/v1/sites - 获取站点列表（支持分页、搜索）
    // POST /api/v1/sites - 创建站点
    // PUT /api/v1/sites/{id} - 更新站点
    // DELETE /api/v1/sites/{id} - 删除站点（软删除）
    // GET /api/v1/sites/{id}/config - 获取站点配置
    // PUT /api/v1/sites/{id}/config - 更新站点配置
}
```

#### 1.2 前端开发规则
```vue
<!-- 组件命名规则 -->
<template>
  <!-- SiteList.vue - 站点列表页面 -->
  <!-- SiteForm.vue - 站点表单组件 -->
  <!-- SiteConfig.vue - 站点配置页面 -->
</template>

<script>
// 状态管理规则
export const useSiteStore = defineStore('site', {
  state: () => ({
    sites: [],
    currentSite: null,
    loading: false
  }),
  actions: {
    async fetchSites() { /* 获取站点列表 */ },
    async createSite(siteData) { /* 创建站点 */ },
    async updateSite(id, siteData) { /* 更新站点 */ }
  }
})
</script>
```

### 2. 内容共享模块

#### 2.1 数据库设计规则
```sql
-- 内容引用表
CREATE TABLE content_references (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_content_id BIGINT NOT NULL,      -- 源内容ID
    target_site_id BIGINT NOT NULL,         -- 目标站点ID
    reference_type ENUM('copy', 'link'),    -- 引用类型：复制/链接
    sync_enabled BOOLEAN DEFAULT TRUE,      -- 是否启用同步
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 内容同步日志表
CREATE TABLE content_sync_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    action ENUM('create', 'update', 'delete'),
    sync_status ENUM('pending', 'success', 'failed'),
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2.2 同步机制规则
```java
// 数据库同步规则
@Component
public class ContentSyncService {

    // 规则1：内容更新时自动触发同步
    @EventListener
    @Transactional
    public void handleContentUpdate(ContentUpdateEvent event) {
        // 查找所有引用此内容的站点
        List<ContentReference> references = contentReferenceRepository
            .findBySourceContentId(event.getContentId());

        // 批量更新引用内容
        for (ContentReference ref : references) {
            if (ref.getSyncEnabled()) {
                syncContentToSite(event.getContentId(), ref.getTargetSiteId());
            }
        }
    }

    // 规则2：支持手动同步和批量同步
    @Transactional
    public void syncContent(Long contentId, List<Long> targetSiteIds) {
        // 实现数据库直接同步逻辑
        Content sourceContent = contentRepository.findById(contentId).orElseThrow();

        for (Long siteId : targetSiteIds) {
            syncContentToSite(sourceContent, siteId);
        }
    }

    // 规则3：同步冲突处理
    public void handleSyncConflict(ContentSyncConflict conflict) {
        // 提供冲突解决策略：覆盖、跳过、手动处理
        // 记录冲突日志到数据库
    }
}
```

### 3. 发布工作流模块

#### 3.1 工作流引擎规则
```java
// 工作流状态枚举
public enum WorkflowStatus {
    DRAFT("草稿"),
    PENDING_REVIEW("待审核"),
    APPROVED("已批准"),
    REJECTED("已拒绝"),
    PUBLISHED("已发布"),
    ARCHIVED("已归档");
}

// 工作流步骤配置
@Entity
public class WorkflowStep {
    private Long id;
    private String name;
    private Integer stepOrder;          // 步骤顺序
    private String assigneeType;        // 指派类型：user/role/group
    private String assigneeValue;       // 指派值
    private Boolean isRequired;         // 是否必须
    private Integer timeoutHours;       // 超时时间（小时）
}
```

#### 3.2 审批流程规则
```java
// 审批规则配置
public class WorkflowRules {
    
    // 规则1：支持串行和并行审批
    // 规则2：支持条件分支（基于内容类型、站点等）
    // 规则3：支持自动审批（基于规则引擎）
    // 规则4：支持审批委托和代理
    // 规则5：支持审批超时自动处理
    
    @Service
    public class WorkflowEngine {
        public void startWorkflow(Long contentId, String workflowType) {
            // 启动工作流实例
        }
        
        public void approveStep(Long instanceId, Long stepId, String comment) {
            // 审批步骤
        }
        
        public void rejectStep(Long instanceId, Long stepId, String reason) {
            // 拒绝步骤
        }
    }
}
```

### 4. 版本控制模块

#### 4.1 版本管理规则
```java
// 版本控制策略
@Entity
public class ContentVersion {
    private Long id;
    private Long contentId;
    private Integer versionNumber;      // 版本号（自增）
    private String versionType;         // 版本类型：major/minor/patch
    private String changeDescription;   // 变更描述
    private String contentSnapshot;     // 内容快照（JSON格式）
    private Long createdBy;
    private LocalDateTime createdAt;
}

// 版本操作规则
@Service
public class VersionControlService {
    
    // 规则1：每次内容保存自动创建版本
    public ContentVersion createVersion(Content content, String changeDesc) {
        // 创建版本快照
    }
    
    // 规则2：支持版本对比
    public VersionDiff compareVersions(Long version1Id, Long version2Id) {
        // 返回版本差异
    }
    
    // 规则3：支持版本回滚
    public void rollbackToVersion(Long contentId, Long versionId) {
        // 回滚到指定版本
    }
    
    // 规则4：版本清理策略（保留最近N个版本）
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupOldVersions() {
        // 清理旧版本
    }
}
```

### 5. 多语言支持模块

#### 5.1 多语言架构规则
```java
// 语言配置
@Entity
public class Language {
    private String code;        // 语言代码：zh-CN, en-US
    private String name;        // 语言名称
    private Boolean isDefault;  // 是否默认语言
    private Boolean isActive;   // 是否启用
}

// 内容翻译
@Entity
public class ContentTranslation {
    private Long id;
    private Long contentId;     // 原内容ID
    private String languageCode;// 语言代码
    private String title;       // 翻译标题
    private String content;     // 翻译内容
    private String status;      // 翻译状态：pending/completed/reviewed
    private Long translatorId;  // 翻译者ID
}
```

#### 5.2 翻译工作流规则
```java
@Service
public class TranslationService {
    
    // 规则1：支持翻译任务分配
    public void assignTranslationTask(Long contentId, String targetLang, Long translatorId) {
        // 分配翻译任务
    }
    
    // 规则2：支持翻译进度跟踪
    public TranslationProgress getTranslationProgress(Long contentId) {
        // 获取翻译进度
    }
    
    // 规则3：支持翻译质量审核
    public void reviewTranslation(Long translationId, Boolean approved, String feedback) {
        // 审核翻译质量
    }
    
    // 规则4：支持机器翻译辅助
    public String getMachineTranslation(String text, String sourceLang, String targetLang) {
        // 调用翻译API
    }
}
```

---

## 代码规范和约定

### 1. 命名规范
```java
// 后端命名规范
- 类名：PascalCase（SiteController, ContentService）
- 方法名：camelCase（createSite, updateContent）
- 变量名：camelCase（siteId, contentList）
- 常量名：UPPER_SNAKE_CASE（MAX_CONTENT_SIZE）
- 包名：小写+点分隔（com.example.cms.site）
```

```javascript
// 前端命名规范
- 组件名：PascalCase（SiteList.vue, ContentForm.vue）
- 方法名：camelCase（fetchSites, updateContent）
- 变量名：camelCase（siteList, currentUser）
- 常量名：UPPER_SNAKE_CASE（API_BASE_URL）
- 文件名：kebab-case（site-list.vue, content-form.vue）
```

### 2. API设计规范
```javascript
// RESTful API规范
GET    /api/v1/sites                    // 获取站点列表
POST   /api/v1/sites                    // 创建站点
GET    /api/v1/sites/{id}               // 获取站点详情
PUT    /api/v1/sites/{id}               // 更新站点
DELETE /api/v1/sites/{id}               // 删除站点

// 嵌套资源
GET    /api/v1/sites/{siteId}/contents  // 获取站点内容列表
POST   /api/v1/sites/{siteId}/contents  // 在站点中创建内容

// 操作接口
POST   /api/v1/contents/{id}/publish    // 发布内容
POST   /api/v1/contents/{id}/sync       // 同步内容
POST   /api/v1/contents/{id}/rollback   // 回滚内容
```

### 3. 错误处理规范
```java
// 统一异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, "参数验证失败：" + e.getMessage()));
    }
}
```

### 4. 数据库操作规范
```java
// 事务管理规范
@Service
@Transactional(readOnly = true)
public class ContentService {
    
    @Transactional(rollbackFor = Exception.class)
    public Content createContent(ContentCreateDTO dto) {
        // 创建内容
        // 创建版本记录
        // 触发同步事件
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void syncContent(Long contentId, List<Long> targetSiteIds) {
        // 批量同步操作
    }
}
```

---

## 开发工具和环境配置

### 1. 开发环境要求
- **JDK**: 17 LTS
- **Node.js**: 18+ LTS
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **IDE**: IntelliJ IDEA + VS Code

### 2. 项目结构规范
```
multi-site-cms/
├── backend/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/example/cms/
│   │       ├── site/          # 站点管理模块
│   │       ├── content/       # 内容管理模块
│   │       ├── workflow/      # 工作流模块
│   │       ├── version/       # 版本控制模块
│   │       └── i18n/          # 多语言模块
│   └── src/main/resources/
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── components/        # 通用组件
│   │   ├── stores/            # 状态管理
│   │   ├── api/               # API接口
│   │   └── utils/             # 工具函数
└── docs/                      # 项目文档
```

### 3. Git工作流规范
```bash
# 分支命名规范
main                           # 主分支
develop                        # 开发分支
feature/site-management        # 功能分支
feature/content-sharing        # 功能分支
hotfix/fix-sync-bug           # 修复分支

# 提交信息规范
feat(site): 添加站点创建功能
fix(content): 修复内容同步bug
docs(api): 更新API文档
style(ui): 调整页面样式
refactor(workflow): 重构工作流引擎
test(content): 添加内容管理单元测试
```

---

## 测试策略

### 1. 后端测试
```java
// 单元测试
@ExtendWith(MockitoExtension.class)
class SiteServiceTest {
    @Test
    void shouldCreateSiteSuccessfully() {
        // 测试站点创建功能
    }
}

// 集成测试
@SpringBootTest
@AutoConfigureTestDatabase
class SiteControllerIntegrationTest {
    @Test
    void shouldCreateSiteViaAPI() {
        // 测试API接口
    }
}
```

### 2. 前端测试
```javascript
// 组件测试
import { mount } from '@vue/test-utils'
import SiteList from '@/components/SiteList.vue'

describe('SiteList', () => {
  test('should render site list correctly', () => {
    // 测试组件渲染
  })
})

// E2E测试
describe('Site Management', () => {
  test('should create site successfully', () => {
    // 端到端测试
  })
})
```

---

## 部署和运维

### 1. Docker化部署
```dockerfile
# 后端Dockerfile
FROM openjdk:17-jre-slim
COPY target/multi-site-cms-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# 前端Dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

### 2. 监控和日志
```yaml
# 日志配置
logging:
  level:
    com.example.cms: DEBUG
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

这份AI开发规则文档为您的多站点CMS系统提供了详细的开发指导，确保项目按照正确的顺序和规范进行开发。
