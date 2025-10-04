package com.cms.module.workflow.repository;

import com.cms.module.workflow.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工作流仓库接口
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long>, JpaSpecificationExecutor<Workflow> {

    /**
     * 根据代码查找工作流
     */
    Optional<Workflow> findByCodeAndDeletedFalse(String code);

    /**
     * 根据站点ID查找工作流列表
     */
    List<Workflow> findBySiteIdAndDeletedFalse(Long siteId);

    /**
     * 查找所有未删除的工作流
     */
    List<Workflow> findByDeletedFalse();

    /**
     * 根据状态查找工作流
     */
    List<Workflow> findByStatusAndDeletedFalse(String status);

    /**
     * 检查代码是否存在
     */
    boolean existsByCodeAndDeletedFalse(String code);
}

