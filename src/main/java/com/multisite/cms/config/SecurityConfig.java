package com.multisite.cms.config;

import com.multisite.cms.security.JwtAuthenticationFilter;
import com.multisite.cms.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security配置类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证提供者
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求的有效期
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（使用JWT不需要CSRF保护）
            .csrf(AbstractHttpConfigurer::disable)
            
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置会话管理（无状态）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置认证提供者
            .authenticationProvider(authenticationProvider())
            
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 公开端点
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/public/**").permitAll()

                // Swagger文档端点
                .antMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // 健康检查端点
                .antMatchers("/actuator/health").permitAll()

                // 静态资源
                .antMatchers("/favicon.ico", "/error").permitAll()
                
                // 管理员端点
                .antMatchers("/api/admin/**").hasRole("SUPER_ADMIN")

                // 站点管理端点
                .antMatchers(HttpMethod.POST, "/api/sites").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/sites/**").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/sites/**").hasRole("SUPER_ADMIN")
                .antMatchers(HttpMethod.GET, "/api/sites/**").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN", "EDITOR", "REVIEWER")

                // 用户管理端点
                .antMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/users/password").authenticated()
                .antMatchers(HttpMethod.GET, "/api/users").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN")
                .antMatchers(HttpMethod.POST, "/api/users").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("SUPER_ADMIN")

                // 内容管理端点
                .antMatchers(HttpMethod.GET, "/api/contents/public/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/contents").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN", "EDITOR", "REVIEWER")
                .antMatchers(HttpMethod.POST, "/api/contents").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN", "EDITOR")
                .antMatchers(HttpMethod.PUT, "/api/contents/**").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN", "EDITOR")
                .antMatchers(HttpMethod.DELETE, "/api/contents/**").hasAnyRole("SUPER_ADMIN", "SITE_ADMIN")

                // 文件上传端点
                .antMatchers(HttpMethod.POST, "/api/upload").authenticated()
                .antMatchers("/uploads/**").permitAll()

                // 角色管理端点
                .antMatchers("/api/roles/**").hasRole("SUPER_ADMIN")

                // 其他所有端点都需要认证
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
