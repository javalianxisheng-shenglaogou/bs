-- =============================================
-- 修复用户密码哈希
-- 将所有用户密码更新为正确的BCrypt哈希
-- 密码：admin123
-- BCrypt哈希：$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
-- 注意：这个哈希实际上是'password'的哈希，用于测试
-- =============================================

UPDATE users 
SET password_hash = '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'
WHERE password_hash = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi';

