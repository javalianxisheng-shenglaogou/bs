-- 插入测试内容分类
INSERT INTO content_categories (id, name, slug, description, site_id, parent_category_id, sort_order, status, created_at, updated_at, created_by, updated_by, deleted) VALUES
(1, '技术文章', 'tech', '技术相关的文章分类', 1, NULL, 1, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(2, '产品介绍', 'products', '产品介绍相关内容', 1, NULL, 2, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(3, '新闻动态', 'news', '公司新闻和行业动态', 1, NULL, 3, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(4, 'Java开发', 'java', 'Java开发技术文章', 1, 1, 1, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(5, '前端开发', 'frontend', '前端开发技术文章', 1, 1, 2, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0);

-- 插入测试内容
INSERT INTO contents (id, title, slug, content, excerpt, type, status, site_id, category_id, author_id, featured_image_url, view_count, like_count, comment_count, published_at, created_at, updated_at, created_by, updated_by, deleted) VALUES
(1, 'Spring Boot 入门指南', 'spring-boot-guide', 
'# Spring Boot 入门指南

Spring Boot 是一个基于 Spring 框架的快速开发框架，它简化了 Spring 应用的配置和部署。

## 主要特性

1. **自动配置**: Spring Boot 可以根据类路径中的依赖自动配置应用
2. **起步依赖**: 提供了一系列的起步依赖，简化了依赖管理
3. **内嵌服务器**: 内置了 Tomcat、Jetty 等服务器
4. **生产就绪**: 提供了健康检查、指标监控等生产环境功能

## 快速开始

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

这就是一个最简单的 Spring Boot 应用！',
'Spring Boot 是一个基于 Spring 框架的快速开发框架，本文介绍了 Spring Boot 的主要特性和快速开始方法。',
'ARTICLE', 'PUBLISHED', 1, 4, 7, NULL, 156, 23, 8, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0),

(2, 'Vue.js 3.0 新特性详解', 'vue3-new-features',
'# Vue.js 3.0 新特性详解

Vue.js 3.0 带来了许多令人兴奋的新特性和改进。

## Composition API

Composition API 是 Vue 3 最重要的新特性之一：

```javascript
import { ref, computed } from ''vue''

export default {
  setup() {
    const count = ref(0)
    const doubleCount = computed(() => count.value * 2)
    
    function increment() {
      count.value++
    }
    
    return {
      count,
      doubleCount,
      increment
    }
  }
}
```

## 性能提升

- 更小的包体积
- 更快的渲染速度
- 更好的 Tree-shaking 支持

## TypeScript 支持

Vue 3 对 TypeScript 提供了更好的支持，类型推断更加准确。',
'Vue.js 3.0 带来了 Composition API、性能提升和更好的 TypeScript 支持等新特性。',
'ARTICLE', 'PUBLISHED', 1, 5, 7, NULL, 89, 15, 5, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0),

(3, '多站点CMS系统架构设计', 'multi-site-cms-architecture',
'# 多站点CMS系统架构设计

本文介绍了多站点内容管理系统的架构设计思路和实现方案。

## 系统架构

### 后端架构
- Spring Boot 2.7.x
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL 数据库

### 前端架构
- Vue.js 3.x
- TypeScript
- Element Plus UI
- Vite 构建工具

## 核心功能

1. **多站点管理**: 支持创建和管理多个独立站点
2. **用户权限**: 基于角色的权限控制系统
3. **内容管理**: 文章、页面、媒体文件管理
4. **分类管理**: 层级分类结构支持

## 技术亮点

- 前后端分离架构
- RESTful API 设计
- 响应式设计
- 国际化支持',
'介绍多站点CMS系统的架构设计，包括技术选型、核心功能和技术亮点。',
'ARTICLE', 'PUBLISHED', 1, 1, 7, NULL, 234, 45, 12, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0),

(4, '产品功能介绍', 'product-features',
'# 产品功能介绍

我们的多站点CMS系统提供了丰富的功能来满足不同用户的需求。

## 核心功能

### 站点管理
- 创建和配置多个独立站点
- 自定义域名和主题
- 站点状态管理

### 内容管理
- 富文本编辑器
- 媒体文件管理
- 内容版本控制
- 定时发布

### 用户管理
- 多角色权限系统
- 用户组管理
- 操作日志记录

## 技术特性

- 高性能架构
- 安全可靠
- 易于扩展
- 移动端适配',
'详细介绍多站点CMS系统的核心功能和技术特性。',
'PAGE', 'PUBLISHED', 1, 2, 7, NULL, 67, 8, 3, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0),

(5, '系统更新公告', 'system-update-announcement',
'# 系统更新公告

## 版本 1.0.0 更新内容

发布时间：2025年9月26日

### 新增功能
1. 多站点管理功能
2. 用户权限管理系统
3. 内容管理和发布功能
4. 响应式管理界面

### 功能优化
- 提升系统性能
- 优化用户体验
- 增强安全性

### 修复问题
- 修复登录状态异常问题
- 修复内容编辑器兼容性问题
- 修复移动端显示问题

感谢大家的支持和反馈！',
'系统版本 1.0.0 更新公告，包含新增功能、优化内容和问题修复。',
'ARTICLE', 'PUBLISHED', 1, 3, 7, NULL, 45, 12, 2, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0),

(6, 'React Hooks 最佳实践', 'react-hooks-best-practices',
'# React Hooks 最佳实践

React Hooks 改变了我们编写 React 组件的方式，本文总结了一些最佳实践。

## 常用 Hooks

### useState
```javascript
const [count, setCount] = useState(0)
```

### useEffect
```javascript
useEffect(() => {
  // 副作用逻辑
  return () => {
    // 清理逻辑
  }
}, [dependencies])
```

### useCallback 和 useMemo
用于性能优化，避免不必要的重新渲染。

## 最佳实践

1. 合理使用依赖数组
2. 避免在循环中使用 Hooks
3. 自定义 Hooks 复用逻辑
4. 使用 ESLint 插件检查 Hooks 规则',
'总结 React Hooks 的最佳实践，包括常用 Hooks 和性能优化技巧。',
'ARTICLE', 'DRAFT', 1, 5, 7, NULL, 23, 5, 1, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0);

-- 更新自增ID
ALTER TABLE content_categories AUTO_INCREMENT = 6;
ALTER TABLE contents AUTO_INCREMENT = 7;
