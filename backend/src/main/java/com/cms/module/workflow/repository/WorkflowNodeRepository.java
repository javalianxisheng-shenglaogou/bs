package com.cms.module.workflow.repository;

import com.cms.module.workflow.entity.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工作流节点仓库接口
 */
@Repository
public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, Long> {

    /**
     * 根据工作流ID查找节点列表
     */
    List<WorkflowNode> findByWorkflowIdAndDeletedFalseOrderBySortOrder(Long workflowId);

    /**
     * 根据工作流ID查找节点列表(按排序升序)
     */
    List<WorkflowNode> findByWorkflowIdAndDeletedFalseOrderBySortOrderAsc(Long workflowId);

    /**
     * 根据工作流ID和节点类型查找节点
     */
    List<WorkflowNode> findByWorkflowIdAndNodeTypeAndDeletedFalse(Long workflowId, String nodeType);

    /**
     * 删除工作流的所有节点
     */
    void deleteByWorkflowId(Long workflowId);
}

