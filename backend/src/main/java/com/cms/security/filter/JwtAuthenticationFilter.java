package com.cms.security.filter;

import com.cms.security.config.JwtConfig;
import com.cms.security.service.CustomUserDetailsService;
import com.cms.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取token
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                try {
                    // 从token中获取用户名
                    String username = jwtUtil.getUsernameFromToken(token);

                    // 如果用户名不为空且当前没有认证信息
                    if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 加载用户详情
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 验证token
                        if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // 设置认证信息到安全上下文
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.debug("用户 {} 认证成功", username);
                        } else {
                            log.warn("Token验证失败: {}", username);
                            sendUnauthorizedResponse(response, "Token验证失败");
                            return;
                        }
                    }
                } catch (io.jsonwebtoken.ExpiredJwtException e) {
                    log.warn("Token已过期");
                    sendUnauthorizedResponse(response, "Token已过期，请重新登录");
                    return;
                } catch (io.jsonwebtoken.security.SignatureException e) {
                    log.warn("Token签名无效");
                    sendUnauthorizedResponse(response, "Token签名无效，请重新登录");
                    return;
                } catch (Exception e) {
                    log.error("JWT认证失败", e);
                    sendUnauthorizedResponse(response, "认证失败，请重新登录");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器异常", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 发送401未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /**
     * 从请求头中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getTokenPrefix())) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length());
        }
        return null;
    }
}

