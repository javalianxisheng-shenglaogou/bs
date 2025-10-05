package com.cms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成password的哈希
        String password = "password";
        String hash = encoder.encode(password);

        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);

        // 验证哈希
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + matches);

        // 测试SQL文件中的哈希
        String sqlHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        boolean sqlMatches = encoder.matches(password, sqlHash);
        System.out.println("\nSQL Hash matches 'password': " + sqlMatches);

        // 测试数据库中的哈希
        String dbHash = "$2a$10$AVhavgkZAWRvNaCNv9ZGyurMkTqJbS8SXqhDZm9XlepOSUU.IFk2i";
        boolean dbMatches = encoder.matches(password, dbHash);
        System.out.println("DB Hash matches 'password': " + dbMatches);
    }
}

