package com.cms.module.log;

import org.junit.jupiter.api.Test;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogPatternTest {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+\\[([^\\]]+)\\]\\s+(INFO|WARN|ERROR)\\s+([^-]+)\\s*-\\s*(.+)"
    );

    @Test
    public void testLogPattern() {
        String logLine = "2025-10-05 00:00:11.329 [http-nio-8080-exec-6] INFO  com.cms.module.auth.service.AuthService - 用户登录: admin";
        
        Matcher matcher = LOG_PATTERN.matcher(logLine);
        if (matcher.find()) {
            System.out.println("✅ 匹配成功!");
            System.out.println("时间: " + matcher.group(1));
            System.out.println("线程: " + matcher.group(2));
            System.out.println("级别: " + matcher.group(3));
            System.out.println("模块: " + matcher.group(4));
            System.out.println("描述: " + matcher.group(5));
        } else {
            System.out.println("❌ 匹配失败!");
        }
    }
}

