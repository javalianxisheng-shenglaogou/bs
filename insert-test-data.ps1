# 插入测试数据的PowerShell脚本

# 数据库连接信息
$server = "localhost"
$port = "3306"
$database = "multi_site_cms"
$username = "root"
$password = "123456"

# 构建连接字符串
$connectionString = "Server=$server;Port=$port;Database=$database;Uid=$username;Pwd=$password;"

Write-Host "正在连接数据库..."

try {
    # 加载MySQL .NET连接器（如果已安装）
    Add-Type -Path "C:\Program Files (x86)\MySQL\MySQL Connector Net 8.0.33\Assemblies\v4.5.2\MySql.Data.dll" -ErrorAction SilentlyContinue
    
    # 创建连接
    $connection = New-Object MySql.Data.MySqlClient.MySqlConnection($connectionString)
    $connection.Open()
    
    Write-Host "数据库连接成功！"
    
    # 插入分类数据
    $categorySQL = @"
INSERT IGNORE INTO content_categories (id, name, slug, description, site_id, parent_category_id, sort_order, status, created_at, updated_at, created_by, updated_by, deleted) VALUES
(1, '技术文章', 'tech', '技术相关的文章分类', 1, NULL, 1, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(2, '产品介绍', 'products', '产品介绍相关内容', 1, NULL, 2, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(3, '新闻动态', 'news', '公司新闻和行业动态', 1, NULL, 3, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(4, 'Java开发', 'java', 'Java开发技术文章', 1, 1, 1, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0),
(5, '前端开发', 'frontend', '前端开发技术文章', 1, 1, 2, 'ACTIVE', NOW(), NOW(), 'superadmin', 'superadmin', 0);
"@

    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($categorySQL, $connection)
    $result = $command.ExecuteNonQuery()
    Write-Host "插入分类数据：$result 行"
    
    # 插入内容数据
    $contentSQL1 = @"
INSERT IGNORE INTO contents (id, title, slug, content, excerpt, type, status, site_id, category_id, author_id, view_count, like_count, comment_count, published_at, created_at, updated_at, created_by, updated_by, deleted) VALUES
(1, 'Spring Boot 入门指南', 'spring-boot-guide', 
'# Spring Boot 入门指南\n\nSpring Boot 是一个基于 Spring 框架的快速开发框架。\n\n## 主要特性\n\n1. 自动配置\n2. 起步依赖\n3. 内嵌服务器\n4. 生产就绪',
'Spring Boot 是一个基于 Spring 框架的快速开发框架，本文介绍了 Spring Boot 的主要特性。',
'ARTICLE', 'PUBLISHED', 1, 4, 7, 156, 23, 8, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0);
"@

    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($contentSQL1, $connection)
    $result = $command.ExecuteNonQuery()
    Write-Host "插入内容1：$result 行"
    
    $contentSQL2 = @"
INSERT IGNORE INTO contents (id, title, slug, content, excerpt, type, status, site_id, category_id, author_id, view_count, like_count, comment_count, published_at, created_at, updated_at, created_by, updated_by, deleted) VALUES
(2, 'Vue.js 3.0 新特性详解', 'vue3-new-features',
'# Vue.js 3.0 新特性详解\n\nVue.js 3.0 带来了许多令人兴奋的新特性。\n\n## Composition API\n\nComposition API 是 Vue 3 最重要的新特性之一。\n\n## 性能提升\n\n- 更小的包体积\n- 更快的渲染速度',
'Vue.js 3.0 带来了 Composition API、性能提升和更好的 TypeScript 支持等新特性。',
'ARTICLE', 'PUBLISHED', 1, 5, 7, 89, 15, 5, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0);
"@

    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($contentSQL2, $connection)
    $result = $command.ExecuteNonQuery()
    Write-Host "插入内容2：$result 行"
    
    $contentSQL3 = @"
INSERT IGNORE INTO contents (id, title, slug, content, excerpt, type, status, site_id, category_id, author_id, view_count, like_count, comment_count, published_at, created_at, updated_at, created_by, updated_by, deleted) VALUES
(3, '多站点CMS系统架构设计', 'multi-site-cms-architecture',
'# 多站点CMS系统架构设计\n\n本文介绍了多站点内容管理系统的架构设计思路。\n\n## 系统架构\n\n### 后端架构\n- Spring Boot\n- Spring Security\n- MySQL',
'介绍多站点CMS系统的架构设计，包括技术选型、核心功能和技术亮点。',
'ARTICLE', 'PUBLISHED', 1, 1, 7, 234, 45, 12, NOW(), NOW(), NOW(), 'superadmin', 'superadmin', 0);
"@

    $command = New-Object MySql.Data.MySqlClient.MySqlCommand($contentSQL3, $connection)
    $result = $command.ExecuteNonQuery()
    Write-Host "插入内容3：$result 行"
    
    Write-Host "测试数据插入完成！"
    
} catch {
    Write-Host "错误: $($_.Exception.Message)"
    Write-Host "可能需要安装 MySQL .NET Connector 或使用其他方法插入数据"
} finally {
    if ($connection) {
        $connection.Close()
    }
}
