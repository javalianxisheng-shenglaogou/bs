package com.multisite.cms;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基础配置类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest(classes = MultiSiteCmsApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.show-sql=true",
    "logging.level.org.springframework.security=DEBUG"
})
@Transactional
public abstract class BaseTestConfig {
    
    // 测试用户数据
    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_EMAIL = "test@example.com";
    protected static final String TEST_PASSWORD = "password123";
    
    // 测试站点数据
    protected static final String TEST_SITE_NAME = "测试站点";
    protected static final String TEST_SITE_CODE = "test-site";
    protected static final String TEST_SITE_DOMAIN = "test.example.com";
    
    // 测试内容数据
    protected static final String TEST_CONTENT_TITLE = "测试内容标题";
    protected static final String TEST_CONTENT_SLUG = "test-content";
    protected static final String TEST_CONTENT_BODY = "这是测试内容的正文部分";
    
    // 测试分类数据
    protected static final String TEST_CATEGORY_NAME = "测试分类";
    protected static final String TEST_CATEGORY_CODE = "test-category";
}
