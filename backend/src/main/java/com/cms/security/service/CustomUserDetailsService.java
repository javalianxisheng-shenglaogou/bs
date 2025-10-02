package com.cms.security.service;

import com.cms.module.user.entity.User;
import com.cms.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义UserDetailsService实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        // 检查用户是否被删除
        if (user.getDeleted()) {
            throw new UsernameNotFoundException("用户已被删除: " + username);
        }
        
        // 初始化懒加载的角色和权限
        user.getRoles().size();
        user.getRoles().forEach(role -> role.getPermissions().size());
        
        return new CustomUserDetails(user);
    }
}

