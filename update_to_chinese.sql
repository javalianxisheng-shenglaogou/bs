-- 将英文内容更新为中文
USE multi_site_cms;

-- 更新 "Contact Us" 为 "联系我们"
UPDATE contents 
SET 
    title = '联系我们',
    summary = '联系方式和地址信息',
    content = '如果您有任何问题、建议或合作意向，请通过以下方式联系我们：邮箱：contact@example.com 电话：+86 138-0000-0000',
    seo_title = '联系我们 - 获取技术支持',
    seo_description = '通过多种方式联系我们获取技术支持',
    seo_keywords = '联系我们,技术支持,合作'
WHERE title = 'Contact Us';

-- 更新 "About Us" 为 "关于我们"
UPDATE contents 
SET 
    title = '关于我们',
    summary = '了解我们的团队和使命',
    content = '我们是一个专注于技术分享和知识传播的团队。我们致力于为开发者提供高质量的技术文章、教程和最佳实践。',
    seo_title = '关于我们 - 技术分享团队',
    seo_description = '了解我们的团队背景、使命和价值观',
    seo_keywords = '关于我们,团队,技术分享'
WHERE title = 'About Us';

-- 更新 "React Hooks Deep Dive" 为 "React Hooks 深度解析"
UPDATE contents 
SET 
    title = 'React Hooks 深度解析',
    summary = '学习 React Hooks 的使用方法和最佳实践',
    content = 'React Hooks 是 React 16.8 中引入的新特性。本文深入探讨 useState、useEffect、useContext 等常用 Hooks 的使用方法和最佳实践。',
    seo_title = 'React Hooks 深度解析 - 现代 React 开发',
    seo_description = '学习 React Hooks 核心概念，掌握 useState、useEffect 使用方法',
    seo_keywords = 'React,Hooks,useState,useEffect,前端'
WHERE title = 'React Hooks Deep Dive';

-- 更新 "Node.js Performance Optimization" 为 "Node.js 性能优化"
UPDATE contents 
SET 
    title = 'Node.js 性能优化',
    summary = '全面的 Node.js 性能优化策略',
    content = 'Node.js 性能优化对于高并发场景至关重要。本文涵盖内存管理、事件循环优化、数据库连接池和缓存策略等内容。',
    seo_title = 'Node.js 性能优化指南',
    seo_description = '掌握 Node.js 性能优化技术',
    seo_keywords = 'Node.js,性能,优化,内存,缓存'
WHERE title = 'Node.js Performance Optimization';

-- 更新 "Docker Containerization Guide" 为 "Docker 容器化指南"
UPDATE contents 
SET 
    title = 'Docker 容器化指南',
    summary = '完整的 Docker 容器化部署教程',
    content = 'Docker 容器化已成为现代应用部署的标准。本文涵盖 Dockerfile 编写、镜像构建、容器编排和生产环境最佳实践。',
    seo_title = 'Docker 容器化实战指南',
    seo_description = '学习 Docker 容器化技术和部署最佳实践',
    seo_keywords = 'Docker,容器化,部署,DevOps,微服务'
WHERE title = 'Docker Containerization Guide';

-- 更新 "TypeScript Advanced Type System" 为 "TypeScript 高级类型系统"
UPDATE contents 
SET 
    title = 'TypeScript 高级类型系统',
    summary = '掌握 TypeScript 高级类型特性',
    content = 'TypeScript 的类型系统是其最强大的特性之一。本文探讨泛型、条件类型、映射类型、模板字面量类型等高级特性。',
    seo_title = 'TypeScript 高级类型系统指南',
    seo_description = '学习 TypeScript 高级类型特性，包括泛型和条件类型',
    seo_keywords = 'TypeScript,类型系统,泛型,前端'
WHERE title = 'TypeScript Advanced Type System';

-- 查看更新结果
SELECT id, title, summary, content_type 
FROM contents 
WHERE title IN (
    '联系我们', 
    '关于我们', 
    'React Hooks 深度解析', 
    'Node.js 性能优化', 
    'Docker 容器化指南', 
    'TypeScript 高级类型系统'
)
ORDER BY id;