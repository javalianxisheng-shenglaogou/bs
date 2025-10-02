package com.cms.module.test;

import com.cms.common.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Tag(name = "测试接口", description = "用于测试系统是否正常运行")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("message", "多站点CMS系统运行正常");
        data.put("version", "1.0.0");
        return ApiResponse.success(data);
    }

    @Operation(summary = "Hello World", description = "简单的测试接口")
    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.success("Hello, Multi-Site CMS!");
    }
}

