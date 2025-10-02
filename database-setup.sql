-- =============================================
-- 数据库初始化脚本
-- 使用方法：mysql -uroot -p < database-setup.sql
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS multi_site_cms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 显示创建结果
SHOW DATABASES LIKE 'multi_site_cms';

SELECT '数据库创建成功！请运行Spring Boot应用以执行Flyway迁移。' AS message;

