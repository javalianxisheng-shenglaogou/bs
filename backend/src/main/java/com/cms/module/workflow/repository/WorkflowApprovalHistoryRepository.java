package com.cms.module.workflow.repository;

import com.cms.module.workflow.entity.WorkflowApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 审批历史Repository
 */
@Repository
public interface WorkflowApprovalHistoryRepository extends JpaRepository<WorkflowApprovalHistory, Long> {

    /**
     * 根据实例ID查询审批历史
     */
    List<WorkflowApprovalHistory> findByInstanceIdOrderByCreatedAtAsc(Long instanceId);

    /**
     * 根据任务ID查询审批历史
     */
    List<WorkflowApprovalHistory> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    /**
     * 根据审批人ID查询审批历史
     */
    List<WorkflowApprovalHistory> findByApproverIdOrderByCreatedAtDesc(Long approverId);
}

