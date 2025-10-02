package com.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 多站点CMS系统主启动类
 * 
 * @author CMS Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class CmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("多站点CMS系统启动成功！");
        System.out.println("Swagger文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================\n");
    }
}

