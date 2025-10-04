package com.cms.module.workflow.repository;

import com.cms.module.workflow.entity.WorkflowTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工作流任务仓库接口
 */
@Repository
public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long>, JpaSpecificationExecutor<WorkflowTask> {

    /**
     * 根据实例ID查找任务列表
     */
    List<WorkflowTask> findByInstanceIdAndDeletedFalseOrderByCreatedAtDesc(Long instanceId);

    /**
     * 根据处理人ID和状态查找任务列表(分页)
     */
    Page<WorkflowTask> findByAssigneeIdAndStatusAndDeletedFalse(Long assigneeId, String status, Pageable pageable);

    /**
     * 根据处理人ID查找待办任务
     */
    Page<WorkflowTask> findByAssigneeIdAndStatusInAndDeletedFalse(Long assigneeId, List<String> statuses, Pageable pageable);

    /**
     * 根据处理人ID查找已办任务
     */
    Page<WorkflowTask> findByAssigneeIdAndStatusNotInAndDeletedFalse(Long assigneeId, List<String> statuses, Pageable pageable);

    /**
     * 统计待办任务数量
     */
    long countByAssigneeIdAndStatusAndDeletedFalse(Long assigneeId, String status);
}

