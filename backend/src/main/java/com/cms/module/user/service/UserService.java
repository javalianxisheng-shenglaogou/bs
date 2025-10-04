package com.cms.module.user.service;

import com.cms.common.exception.BusinessException;
import com.cms.common.exception.ErrorCode;
import com.cms.module.user.dto.UserCreateRequest;
import com.cms.module.user.dto.UserDTO;
import com.cms.module.user.dto.UserUpdateRequest;
import com.cms.module.user.entity.Role;
import com.cms.module.user.entity.User;
import com.cms.module.user.repository.RoleRepository;
import com.cms.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 获取所有用户（不分页）
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByDeletedFalse();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户列表
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getUserList(String keyword, String status, Integer page, Integer size, String sortBy, String sortOrder) {
        // 创建分页对象
        Sort sort = Sort.by("DESC".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC, 
                           StringUtils.hasText(sortBy) ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        // 创建查询条件
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                Predicate usernamePredicate = cb.like(root.get("username"), "%" + keyword + "%");
                Predicate emailPredicate = cb.like(root.get("email"), "%" + keyword + "%");
                Predicate nicknamePredicate = cb.like(root.get("nickname"), "%" + keyword + "%");
                predicates.add(cb.or(usernamePredicate, emailPredicate, nicknamePredicate));
            }
            
            // 状态筛选
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            // 排除已删除的用户
            predicates.add(cb.equal(root.get("deleted"), false));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 查询并转换
        Page<User> userPage = userRepository.findAll(spec, pageable);
        return userPage.map(this::convertToDTO);
    }
    
    /**
     * 根据ID获取用户
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 初始化懒加载的角色
        user.getRoles().size();
        
        return convertToDTO(user);
    }
    
    /**
     * 创建用户
     */
    @Transactional
    public UserDTO createUser(UserCreateRequest request) {
        log.info("创建用户: {}", request.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 检查手机号是否已存在
        if (StringUtils.hasText(request.getMobile()) && userRepository.existsByMobile(request.getMobile())) {
            throw new BusinessException(ErrorCode.MOBILE_ALREADY_EXISTS);
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRealName(request.getRealName());
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        user.setBio(request.getBio());
        user.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ACTIVE");
        
        // 设置角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        // 保存用户
        user = userRepository.save(user);
        
        // 初始化懒加载的角色
        user.getRoles().size();
        
        return convertToDTO(user);
    }
    
    /**
     * 更新用户
     */
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        log.info("更新用户: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        
        // 检查手机号是否已被其他用户使用
        if (StringUtils.hasText(request.getMobile()) && !request.getMobile().equals(user.getMobile())) {
            if (userRepository.existsByMobile(request.getMobile())) {
                throw new BusinessException(ErrorCode.MOBILE_ALREADY_EXISTS);
            }
            user.setMobile(request.getMobile());
        }
        
        // 更新其他字段
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getGender())) {
            user.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (StringUtils.hasText(request.getBio())) {
            user.setBio(request.getBio());
        }
        if (StringUtils.hasText(request.getStatus())) {
            user.setStatus(request.getStatus());
        }
        
        // 更新角色
        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>();
            for (Long roleId : request.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        // 保存用户
        user = userRepository.save(user);
        
        // 初始化懒加载的角色
        user.getRoles().size();
        
        return convertToDTO(user);
    }
    
    /**
     * 删除用户（软删除）
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 软删除
        user.setDeleted(true);
        userRepository.save(user);
    }

    /**
     * 更新用户头像
     */
    @Transactional
    public UserDTO updateAvatar(Long id, String avatarUrl) {
        log.info("更新用户头像: id={}, avatarUrl={}", id, avatarUrl);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeleted()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setAvatarUrl(avatarUrl);
        user = userRepository.save(user);

        // 初始化懒加载的角色
        user.getRoles().size();

        return convertToDTO(user);
    }
    
    /**
     * 转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setNickname(user.getNickname());
        dto.setRealName(user.getRealName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setGender(user.getGender());
        dto.setBirthday(user.getBirthday());
        dto.setBio(user.getBio());
        dto.setStatus(user.getStatus());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setMobileVerified(user.getMobileVerified());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setLastLoginIp(user.getLastLoginIp());
        dto.setLoginCount(user.getLoginCount());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // 转换角色
        List<String> roleCodes = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
        dto.setRoles(roleCodes);
        
        return dto;
    }
}

