package com.multisite.cms.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.multisite.cms.entity.User;
import com.multisite.cms.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户主体类，实现Spring Security的UserDetails接口
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;
    
    @JsonIgnore
    private String password;
    
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    /**
     * 从User实体创建UserPrincipal
     * 
     * @param user 用户实体
     * @return UserPrincipal实例
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getUserRoles().stream()
                .filter(userRole -> userRole.isValid()) // 只包含有效的角色
                .map(userRole -> {
                    String authority = "ROLE_" + userRole.getRole().getCode();
                    // 如果是站点级角色，添加站点信息
                    if (userRole.isSiteRole()) {
                        authority += "_SITE_" + userRole.getSite().getCode();
                    }
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getPasswordHash(),
                authorities,
                true, // accountNonExpired
                user.isActive(), // accountNonLocked (基于用户状态)
                true, // credentialsNonExpired
                user.isActive() // enabled (基于用户状态)
        );
    }

    /**
     * 从User实体和指定站点创建UserPrincipal
     * 
     * @param user 用户实体
     * @param siteId 站点ID（可为null，表示全局权限）
     * @return UserPrincipal实例
     */
    public static UserPrincipal create(User user, Long siteId) {
        List<GrantedAuthority> authorities = user.getUserRoles().stream()
                .filter(userRole -> userRole.isValid()) // 只包含有效的角色
                .filter(userRole -> {
                    // 包含全局角色和指定站点的角色
                    return userRole.isGlobalRole() || 
                           (siteId != null && userRole.isSiteRole() && 
                            siteId.equals(userRole.getSite().getId()));
                })
                .map(userRole -> {
                    String authority = "ROLE_" + userRole.getRole().getCode();
                    // 如果是站点级角色，添加站点信息
                    if (userRole.isSiteRole()) {
                        authority += "_SITE_" + userRole.getSite().getCode();
                    }
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getPasswordHash(),
                authorities,
                true, // accountNonExpired
                user.isActive(), // accountNonLocked
                true, // credentialsNonExpired
                user.isActive() // enabled
        );
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
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return nickname != null && !nickname.trim().isEmpty() ? nickname : username;
    }

    /**
     * 检查是否具有指定权限
     * 
     * @param authority 权限名称
     * @return 是否具有权限
     */
    public boolean hasAuthority(String authority) {
        return authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }

    /**
     * 检查是否具有指定角色
     * 
     * @param role 角色名称（不需要ROLE_前缀）
     * @return 是否具有角色
     */
    public boolean hasRole(String role) {
        return hasAuthority("ROLE_" + role);
    }

    /**
     * 检查是否具有指定站点的角色
     * 
     * @param role 角色名称
     * @param siteCode 站点代码
     * @return 是否具有站点角色
     */
    public boolean hasSiteRole(String role, String siteCode) {
        return hasAuthority("ROLE_" + role + "_SITE_" + siteCode);
    }

    /**
     * 检查是否为超级管理员
     * 
     * @return 是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }

    /**
     * 检查是否为站点管理员
     * 
     * @param siteCode 站点代码（可为null，检查是否为任意站点的管理员）
     * @return 是否为站点管理员
     */
    public boolean isSiteAdmin(String siteCode) {
        if (isSuperAdmin()) {
            return true; // 超级管理员拥有所有站点的管理权限
        }
        
        if (siteCode == null) {
            // 检查是否为任意站点的管理员
            return authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().contains("ROLE_SITE_ADMIN"));
        } else {
            return hasSiteRole("SITE_ADMIN", siteCode);
        }
    }

    /**
     * 获取用户的所有角色代码
     * 
     * @return 角色代码列表
     */
    public List<String> getRoleCodes() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5)) // 移除"ROLE_"前缀
                .collect(Collectors.toList());
    }

    /**
     * 获取用户管理的站点代码列表
     * 
     * @return 站点代码列表
     */
    public List<String> getManagedSiteCodes() {
        if (isSuperAdmin()) {
            // 超级管理员管理所有站点，这里返回空列表表示全部
            return List.of();
        }
        
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.contains("_SITE_"))
                .map(authority -> {
                    int siteIndex = authority.lastIndexOf("_SITE_");
                    return authority.substring(siteIndex + 6);
                })
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserPrincipal that = (UserPrincipal) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("UserPrincipal{id=%d, username='%s', email='%s', authorities=%s}", 
                id, username, email, authorities);
    }
}
