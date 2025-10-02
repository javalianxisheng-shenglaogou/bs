package com.cms.security.service;

import com.cms.module.user.entity.Permission;
import com.cms.module.user.entity.Role;
import com.cms.module.user.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义UserDetails实现
 */
@Data
public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String status;
    private Set<Role> roles;
    private Set<Permission> permissions;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.status = user.getStatus();
        this.roles = user.getRoles();
        
        // 收集所有权限
        this.permissions = new HashSet<>();
        if (roles != null) {
            for (Role role : roles) {
                if (role.getPermissions() != null) {
                    permissions.addAll(role.getPermissions());
                }
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // 添加角色权限（以ROLE_开头）
        if (roles != null) {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode()))
                    .collect(Collectors.toSet()));
        }
        
        // 添加权限
        if (permissions != null) {
            authorities.addAll(permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                    .collect(Collectors.toSet()));
        }
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"LOCKED".equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(status);
    }

    /**
     * 获取角色代码列表
     */
    public Set<String> getRoleCodes() {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * 获取权限代码列表
     */
    public Set<String> getPermissionCodes() {
        if (permissions == null) {
            return new HashSet<>();
        }
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }
}

