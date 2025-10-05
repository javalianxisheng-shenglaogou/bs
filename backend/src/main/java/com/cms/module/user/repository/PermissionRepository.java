package com.cms.module.user.repository;

import com.cms.module.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限Repository
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    /**
     * 根据权限代码查询权限
     */
    Optional<Permission> findByCode(String code);

    /**
     * 根据模块查询权限列表
     */
    List<Permission> findByModule(String module);

    /**
     * 检查权限代码是否存在
     */
    boolean existsByCode(String code);
}

