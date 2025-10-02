package com.cms.module.user.repository;

import com.cms.module.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 角色Repository
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色代码查询角色
     */
    Optional<Role> findByCode(String code);

    /**
     * 检查角色代码是否存在
     */
    boolean existsByCode(String code);
}

