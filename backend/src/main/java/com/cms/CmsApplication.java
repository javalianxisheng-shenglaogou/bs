package com.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 多站点CMS系统主启动类
 *
 * @author CMS Team
 * @since 1.0.0
 */
@SpringBootApplication
public class CmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("多站点CMS系统启动成功！");
        System.out.println("Swagger文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("========================================\n");
    }
}

