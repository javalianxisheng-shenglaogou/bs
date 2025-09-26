-- 基于Spring Boot的多站点内容管理系统数据库初始化脚本
-- 创建时间: 2024-01-01
-- 数据库版本: MySQL 8.0

-- 1. 创建数据库
DROP DATABASE IF EXISTS multi_site_cms;
CREATE DATABASE multi_site_cms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. 使用数据库
USE multi_site_cms;

-- 3. 创建开发用户（可选）
-- CREATE USER 'cms_dev'@'localhost' IDENTIFIED BY 'cms_dev_password';
-- GRANT ALL PRIVILEGES ON multi_site_cms.* TO 'cms_dev'@'localhost';
-- FLUSH PRIVILEGES;

-- 4. 设置数据库参数
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 数据库创建完成
SELECT 'Database multi_site_cms created successfully!' as message;
