package com.cms.module.workflow.repository;

import com.cms.module.workflow.entity.WorkflowInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工作流实例仓库接口
 */
@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long>, JpaSpecificationExecutor<WorkflowInstance> {

    /**
     * 根据业务类型和业务ID查找实例
     */
    Optional<WorkflowInstance> findByBusinessTypeAndBusinessIdAndDeletedFalse(String businessType, Long businessId);

    /**
     * 根据工作流ID查找实例列表
     */
    List<WorkflowInstance> findByWorkflowIdAndDeletedFalse(Long workflowId);

    /**
     * 根据发起人ID查找实例列表
     */
    Page<WorkflowInstance> findByInitiatorIdAndDeletedFalse(Long initiatorId, Pageable pageable);

    /**
     * 根据状态查找实例列表
     */
    List<WorkflowInstance> findByStatusAndDeletedFalse(String status);
}

