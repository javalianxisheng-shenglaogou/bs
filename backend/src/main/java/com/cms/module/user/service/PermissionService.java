package com.cms.module.user.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.user.dto.PermissionDTO;
import com.cms.module.user.entity.Permission;
import com.cms.module.user.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    /**
     * 获取所有权限
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "module", "sortOrder"));
        return permissions.stream()
                .filter(p -> !p.getDeleted())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页获取权限列表
     */
    @Transactional(readOnly = true)
    public Page<PermissionDTO> getPermissions(String module, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "module", "sortOrder"));
        Page<Permission> permissionPage;

        if (module != null && !module.isEmpty()) {
            Specification<Permission> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("module"), module),
                cb.equal(root.get("deleted"), false)
            );
            permissionPage = permissionRepository.findAll(spec, pageable);
        } else {
            Specification<Permission> spec = (root, query, cb) -> cb.equal(root.get("deleted"), false);
            permissionPage = permissionRepository.findAll(spec, pageable);
        }

        return permissionPage.map(this::convertToDTO);
    }

    /**
     * 根据ID获取权限
     */
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));
        
        if (permission.getDeleted()) {
            throw new BusinessException("权限已删除");
        }
        
        return convertToDTO(permission);
    }

    /**
     * 根据模块获取权限列表
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByModule(String module) {
        List<Permission> permissions = permissionRepository.findByModule(module);
        return permissions.stream()
                .filter(p -> !p.getDeleted())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建权限
     */
    @Transactional(rollbackFor = Exception.class)
    public PermissionDTO createPermission(PermissionDTO dto) {
        // 检查权限代码是否已存在
        if (permissionRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("权限代码已存在");
        }

        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setDescription(dto.getDescription());
        permission.setModule(dto.getModule());
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        permission.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        permission = permissionRepository.save(permission);
        log.info("创建权限成功: {}", permission.getCode());
        
        return convertToDTO(permission);
    }

    /**
     * 更新权限
     */
    @Transactional(rollbackFor = Exception.class)
    public PermissionDTO updatePermission(Long id, PermissionDTO dto) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));

        if (permission.getDeleted()) {
            throw new BusinessException("权限已删除");
        }

        // 如果是系统权限，不允许修改关键字段
        if (permission.getIsSystem()) {
            throw new BusinessException("系统权限不允许修改");
        }

        // 检查权限代码是否重复
        if (!permission.getCode().equals(dto.getCode()) && permissionRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("权限代码已存在");
        }

        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setDescription(dto.getDescription());
        permission.setModule(dto.getModule());
        permission.setResource(dto.getResource());
        permission.setAction(dto.getAction());
        permission.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        permission = permissionRepository.save(permission);
        log.info("更新权限成功: {}", permission.getCode());
        
        return convertToDTO(permission);
    }

    /**
     * 删除权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("权限不存在"));

        if (permission.getIsSystem()) {
            throw new BusinessException("系统权限不允许删除");
        }

        permission.setDeleted(true);
        permissionRepository.save(permission);
        log.info("删除权限成功: {}", permission.getCode());
    }

    /**
     * 转换为DTO
     */
    private PermissionDTO convertToDTO(Permission permission) {
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
        dto.setCreatedAt(permission.getCreatedAt());
        dto.setUpdatedAt(permission.getUpdatedAt());
        return dto;
    }
}

