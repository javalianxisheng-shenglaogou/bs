package com.cms.module.test;

import com.cms.common.base.ApiResponse;
import com.cms.module.user.entity.User;
import com.cms.module.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 测试控制器
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Tag(name = "测试接口", description = "用于测试系统是否正常运行")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Operation(summary = "检查用户", description = "检查admin用户是否存在")
    @GetMapping("/check-user")
    public ApiResponse<Map<String, Object>> checkUser() {
        Map<String, Object> data = new HashMap<>();

        Optional<User> adminUser = userRepository.findByUsername("admin");
        data.put("adminExists", adminUser.isPresent());

        if (adminUser.isPresent()) {
            User user = adminUser.get();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("status", user.getStatus());
            data.put("passwordHashLength", user.getPasswordHash() != null ? user.getPasswordHash().length() : 0);
            // 暂时不检查roles，避免LazyInitializationException
            // data.put("rolesCount", user.getRoles() != null ? user.getRoles().size() : 0);
        }

        long totalUsers = userRepository.count();
        data.put("totalUsers", totalUsers);

        return ApiResponse.success(data);
    }

    @Operation(summary = "测试密码", description = "测试密码是否匹配")
    @GetMapping("/test-password")
    public ApiResponse<Map<String, Object>> testPassword(@RequestParam String password) {
        Map<String, Object> data = new HashMap<>();

        Optional<User> adminUser = userRepository.findByUsername("admin");
        if (adminUser.isPresent()) {
            User user = adminUser.get();
            boolean matches = passwordEncoder.matches(password, user.getPasswordHash());
            data.put("passwordMatches", matches);
            data.put("inputPassword", password);
            data.put("storedHashPrefix", user.getPasswordHash().substring(0, 20) + "...");
        } else {
            data.put("error", "Admin user not found");
        }

        return ApiResponse.success(data);
    }
}

