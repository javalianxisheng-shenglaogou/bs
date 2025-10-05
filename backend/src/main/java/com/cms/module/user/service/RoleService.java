package com.cms.module.user.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.user.dto.PermissionDTO;
import com.cms.module.user.dto.RoleDTO;
import com.cms.module.user.entity.Permission;
import com.cms.module.user.entity.Role;
import com.cms.module.user.repository.PermissionRepository;
import com.cms.module.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * 获取所有角色
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "level"));
        return roles.stream()
                .filter(r -> !r.getDeleted())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取角色列表
     */
    @Transactional(readOnly = true)
    public Page<RoleDTO> getRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "level"));
        Specification<Role> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
        Page<Role> rolePage = roleRepository.findAll(spec, pageable);
        return rolePage.map(this::convertToDTO);
    }

    /**
     * 根据ID获取角色
     */
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        
        if (role.getDeleted()) {
            throw new BusinessException("角色已删除");
        }
        
        // 初始化懒加载的权限
        role.getPermissions().size();
        
        return convertToDTO(role);
    }

    /**
     * 创建角色
     */
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO createRole(RoleDTO dto) {
        // 检查角色代码是否已存在
        if (roleRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("角色代码已存在");
        }

        Role role = new Role();
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setDescription(dto.getDescription());
        role.setLevel(dto.getLevel() != null ? dto.getLevel() : 0);
        role.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        role.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        // 设置权限
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : dto.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new BusinessException("权限不存在: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        role = roleRepository.save(role);
        log.info("创建角色成功: {}", role.getCode());
        
        return convertToDTO(role);
    }

    /**
     * 更新角色
     */
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO updateRole(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        if (role.getDeleted()) {
            throw new BusinessException("角色已删除");
        }

        // 如果是系统角色，不允许修改关键字段
        if (role.getIsSystem()) {
            throw new BusinessException("系统角色不允许修改");
        }

        // 检查角色代码是否重复
        if (!role.getCode().equals(dto.getCode()) && roleRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("角色代码已存在");
        }

        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setDescription(dto.getDescription());
        role.setLevel(dto.getLevel() != null ? dto.getLevel() : 0);
        role.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);

        // 更新权限
        if (dto.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : dto.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new BusinessException("权限不存在: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        role = roleRepository.save(role);
        log.info("更新角色成功: {}", role.getCode());
        
        return convertToDTO(role);
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        if (role.getIsSystem()) {
            throw new BusinessException("系统角色不允许删除");
        }

        role.setDeleted(true);
        roleRepository.save(role);
        log.info("删除角色成功: {}", role.getCode());
    }

    /**
     * 为角色分配权限
     */
    @Transactional(rollbackFor = Exception.class)
    public RoleDTO assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        if (role.getIsSystem()) {
            throw new BusinessException("系统角色不允许修改权限");
        }

        Set<Permission> permissions = new HashSet<>();
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new BusinessException("权限不存在: " + permissionId));
            permissions.add(permission);
        }

        role.setPermissions(permissions);
        role = roleRepository.save(role);
        log.info("为角色 {} 分配权限成功", role.getCode());
        
        return convertToDTO(role);
    }

    /**
     * 转换为DTO
     */
    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCode(role.getCode());
        dto.setDescription(role.getDescription());
        dto.setLevel(role.getLevel());
        dto.setIsSystem(role.getIsSystem());
        dto.setIsDefault(role.getIsDefault());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());

        // 转换权限列表
        if (role.getPermissions() != null) {
            List<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                    .filter(p -> !p.getDeleted())
                    .map(this::convertPermissionToDTO)
                    .collect(Collectors.toList());
            dto.setPermissions(permissionDTOs);
            
            List<Long> permissionIds = role.getPermissions().stream()
                    .filter(p -> !p.getDeleted())
                    .map(Permission::getId)
                    .collect(Collectors.toList());
            dto.setPermissionIds(permissionIds);
        }

        return dto;
    }

    /**
     * 转换权限为DTO
     */
    private PermissionDTO convertPermissionToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setCode(permission.getCode());
        dto.setDescription(permission.getDescription());
        dto.setModule(permission.getModule());
        dto.setResource(permission.getResource());
        dto.setAction(permission.getAction());
        dto.setIsSystem(permission.getIsSystem());
        dto.setSortOrder(permission.getSortOrder());
        return dto;
    }
}

