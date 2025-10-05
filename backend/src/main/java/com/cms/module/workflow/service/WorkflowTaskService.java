package com.cms.module.workflow.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.user.entity.User;
import com.cms.module.user.repository.UserRepository;
import com.cms.module.workflow.dto.ApproveTaskRequest;
import com.cms.module.workflow.dto.RejectTaskRequest;
import com.cms.module.workflow.dto.WorkflowTaskDTO;
import com.cms.module.workflow.entity.WorkflowApprovalHistory;
import com.cms.module.workflow.entity.WorkflowInstance;
import com.cms.module.workflow.entity.WorkflowNode;
import com.cms.module.workflow.entity.WorkflowTask;
import com.cms.module.workflow.repository.WorkflowApprovalHistoryRepository;
import com.cms.module.workflow.repository.WorkflowInstanceRepository;
import com.cms.module.workflow.repository.WorkflowNodeRepository;
import com.cms.module.workflow.repository.WorkflowTaskRepository;
import com.cms.security.service.CustomUserDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流任务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowTaskService {

    private final WorkflowTaskRepository taskRepository;
    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final WorkflowApprovalHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final org.springframework.context.ApplicationContext applicationContext;

    /**
     * 为节点创建任务
     */
    @Transactional
    public void createTasksForNode(Long instanceId, WorkflowNode node) {
        log.info("为节点创建任务: instanceId={}, nodeId={}", instanceId, node.getId());

        WorkflowInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException("工作流实例不存在: " + instanceId));

        // 解析审批人ID列表
        List<Long> approverIds;
        try {
            approverIds = objectMapper.readValue(node.getApproverIds(), new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.error("解析审批人ID失败", e);
            throw new BusinessException("解析审批人ID失败");
        }

        if (approverIds == null || approverIds.isEmpty()) {
            throw new BusinessException("节点没有配置审批人");
        }

        // 为每个审批人创建任务
        for (Long approverId : approverIds) {
            User user = userRepository.findById(approverId).orElse(null);
            
            WorkflowTask task = new WorkflowTask();
            task.setInstanceId(instanceId);
            task.setNodeId(node.getId());
            task.setTaskName(node.getName());
            task.setTaskType("APPROVAL");
            task.setAssigneeId(approverId);
            task.setAssigneeName(user != null ? user.getNickname() : "");
            task.setStatus("PENDING");

            taskRepository.save(task);
        }
    }

    /**
     * 获取我的待办任务
     */
    public Page<WorkflowTaskDTO> getMyPendingTasks(int page, int size) {
        log.debug("获取我的待办任务: page={}, size={}", page, size);

        Long currentUserId = getCurrentUserId();
        
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        List<String> pendingStatuses = Arrays.asList("PENDING", "IN_PROGRESS");
        Page<WorkflowTask> taskPage = taskRepository.findByAssigneeIdAndStatusInAndDeletedFalse(
                currentUserId, pendingStatuses, pageable);

        return taskPage.map(this::convertToDTO);
    }

    /**
     * 获取我的已办任务
     */
    public Page<WorkflowTaskDTO> getMyCompletedTasks(int page, int size) {
        log.debug("获取我的已办任务: page={}, size={}", page, size);

        Long currentUserId = getCurrentUserId();
        
        Sort sort = Sort.by(Sort.Direction.DESC, "processedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        List<String> pendingStatuses = Arrays.asList("PENDING", "IN_PROGRESS");
        Page<WorkflowTask> taskPage = taskRepository.findByAssigneeIdAndStatusNotInAndDeletedFalse(
                currentUserId, pendingStatuses, pageable);

        return taskPage.map(this::convertToDTO);
    }

    /**
     * 获取任务详情
     */
    public WorkflowTaskDTO getTaskById(Long id) {
        log.debug("获取任务详情: id={}", id);

        WorkflowTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在: " + id));

        if (task.getDeleted()) {
            throw new BusinessException("任务已删除: " + id);
        }

        return convertToDTO(task);
    }

    /**
     * 通过任务
     */
    @Transactional
    public void approveTask(Long id, ApproveTaskRequest request) {
        log.info("通过任务: id={}", id);

        Long currentUserId = getCurrentUserId();

        WorkflowTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在: " + id));

        // 检查任务状态
        if (!"PENDING".equals(task.getStatus()) && !"IN_PROGRESS".equals(task.getStatus())) {
            throw new BusinessException("任务已处理");
        }

        // 检查处理人
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new BusinessException("无权处理此任务");
        }

        // 更新任务
        task.setStatus("APPROVED");
        task.setAction("APPROVE");
        task.setComment(request.getComment());
        task.setProcessedAt(LocalDateTime.now());
        taskRepository.save(task);

        // 记录审批历史
        recordApprovalHistory(task, "APPROVE", request.getComment());

        // 检查节点的所有任务是否都已完成
        checkNodeCompletion(task.getInstanceId(), task.getNodeId());
    }

    /**
     * 拒绝任务
     */
    @Transactional
    public void rejectTask(Long id, RejectTaskRequest request) {
        log.info("拒绝任务: id={}", id);

        Long currentUserId = getCurrentUserId();

        WorkflowTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在: " + id));

        // 检查任务状态
        if (!"PENDING".equals(task.getStatus()) && !"IN_PROGRESS".equals(task.getStatus())) {
            throw new BusinessException("任务已处理");
        }

        // 检查处理人
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new BusinessException("无权处理此任务");
        }

        // 更新任务
        task.setStatus("REJECTED");
        task.setAction("REJECT");
        task.setComment(request.getComment());
        task.setProcessedAt(LocalDateTime.now());
        taskRepository.save(task);

        // 记录审批历史
        recordApprovalHistory(task, "REJECT", request.getComment());

        // 拒绝整个实例
        WorkflowInstance instance = instanceRepository.findById(task.getInstanceId())
                .orElseThrow(() -> new BusinessException("工作流实例不存在"));

        instance.setStatus("REJECTED");
        instance.setCompletedAt(LocalDateTime.now());
        instance.setCompletionNote("任务被拒绝: " + request.getComment());
        instanceRepository.save(instance);

        // 业务回调 - 通知业务模块审批被拒绝
        notifyBusinessRejected(instance, request.getComment());

        // 取消其他待办任务
        cancelTasksByInstanceId(task.getInstanceId());
    }

    /**
     * 取消实例的所有待办任务
     */
    @Transactional
    public void cancelTasksByInstanceId(Long instanceId) {
        log.info("取消实例的所有待办任务: instanceId={}", instanceId);

        List<WorkflowTask> tasks = taskRepository.findByInstanceIdAndDeletedFalseOrderByCreatedAtDesc(instanceId);
        
        for (WorkflowTask task : tasks) {
            if ("PENDING".equals(task.getStatus()) || "IN_PROGRESS".equals(task.getStatus())) {
                task.setStatus("CANCELLED");
                taskRepository.save(task);
            }
        }
    }

    /**
     * 检查节点完成情况
     */
    private void checkNodeCompletion(Long instanceId, Long nodeId) {
        log.debug("检查节点完成情况: instanceId={}, nodeId={}", instanceId, nodeId);

        List<WorkflowTask> tasks = taskRepository.findByInstanceIdAndDeletedFalseOrderByCreatedAtDesc(instanceId);

        // 过滤出当前节点的任务
        List<WorkflowTask> nodeTasks = tasks.stream()
                .filter(t -> t.getNodeId().equals(nodeId))
                .collect(Collectors.toList());

        // 检查是否所有任务都已通过
        boolean allApproved = nodeTasks.stream()
                .allMatch(t -> "APPROVED".equals(t.getStatus()));

        if (allApproved && !nodeTasks.isEmpty()) {
            log.info("节点所有任务已完成,准备移动到下一个节点");

            // 获取当前节点
            WorkflowNode currentNode = nodeRepository.findById(nodeId)
                    .orElseThrow(() -> new BusinessException("节点不存在"));

            // 获取下一个节点
            List<WorkflowNode> allNodes = nodeRepository.findByWorkflowIdAndDeletedFalseOrderBySortOrderAsc(currentNode.getWorkflowId());
            WorkflowNode nextNode = null;
            for (int i = 0; i < allNodes.size(); i++) {
                if (allNodes.get(i).getId().equals(nodeId) && i < allNodes.size() - 1) {
                    nextNode = allNodes.get(i + 1);
                    break;
                }
            }

            if (nextNode != null && "END".equals(nextNode.getNodeType())) {
                // 到达结束节点,完成工作流
                completeWorkflow(instanceId);
            } else if (nextNode != null) {
                // 移动到下一个节点
                WorkflowInstance instance = instanceRepository.findById(instanceId)
                        .orElseThrow(() -> new BusinessException("工作流实例不存在"));
                instance.setCurrentNodeId(nextNode.getId());
                instanceRepository.save(instance);

                // 为下一个节点创建任务
                createTasksForNode(instanceId, nextNode);
            }
        }
    }

    /**
     * 完成工作流
     */
    private void completeWorkflow(Long instanceId) {
        log.info("完成工作流: instanceId={}", instanceId);

        WorkflowInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException("工作流实例不存在"));

        instance.setStatus("APPROVED");
        instance.setCompletedAt(LocalDateTime.now());
        instance.setCompletionNote("审批通过");
        instanceRepository.save(instance);

        // 业务回调 - 通知业务模块审批通过
        notifyBusinessApproved(instance);
    }

    /**
     * 通知业务模块审批通过
     */
    private void notifyBusinessApproved(WorkflowInstance instance) {
        log.info("通知业务模块审批通过: businessType={}, businessId={}",
                instance.getBusinessType(), instance.getBusinessId());

        try {
            if ("CONTENT".equals(instance.getBusinessType())) {
                // 通知内容服务
                com.cms.module.content.service.ContentService contentService =
                    applicationContext.getBean(com.cms.module.content.service.ContentService.class);
                contentService.onApprovalApproved(instance.getBusinessId(), getCurrentUserId());
            }
            // 可以添加其他业务类型的处理
        } catch (Exception e) {
            log.error("通知业务模块失败", e);
        }
    }

    /**
     * 通知业务模块审批拒绝
     */
    private void notifyBusinessRejected(WorkflowInstance instance, String reason) {
        log.info("通知业务模块审批拒绝: businessType={}, businessId={}",
                instance.getBusinessType(), instance.getBusinessId());

        try {
            if ("CONTENT".equals(instance.getBusinessType())) {
                // 通知内容服务
                com.cms.module.content.service.ContentService contentService =
                    applicationContext.getBean(com.cms.module.content.service.ContentService.class);
                contentService.onApprovalRejected(instance.getBusinessId(), reason);
            }
            // 可以添加其他业务类型的处理
        } catch (Exception e) {
            log.error("通知业务模块失败", e);
        }
    }

    /**
     * 记录审批历史
     */
    private void recordApprovalHistory(WorkflowTask task, String action, String comment) {
        log.debug("记录审批历史: taskId={}, action={}", task.getId(), action);

        WorkflowNode node = nodeRepository.findById(task.getNodeId()).orElse(null);

        WorkflowApprovalHistory history = new WorkflowApprovalHistory();
        history.setInstanceId(task.getInstanceId());
        history.setTaskId(task.getId());
        history.setNodeId(task.getNodeId());
        history.setNodeName(node != null ? node.getName() : "未知节点");
        history.setApproverId(task.getAssigneeId());
        history.setApproverName(task.getAssigneeName());
        history.setAction(action);
        history.setComment(comment);
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        throw new BusinessException("未登录");
    }

    /**
     * 转换为DTO
     */
    private WorkflowTaskDTO convertToDTO(WorkflowTask task) {
        WorkflowTaskDTO dto = new WorkflowTaskDTO();
        BeanUtils.copyProperties(task, dto);

        // 加载实例信息
        WorkflowInstance instance = instanceRepository.findById(task.getInstanceId()).orElse(null);
        if (instance != null) {
            dto.setBusinessType(instance.getBusinessType());
            dto.setBusinessId(instance.getBusinessId());
            dto.setBusinessTitle(instance.getBusinessTitle());
            dto.setInstanceStatus(instance.getStatus());
        }

        return dto;
    }
}

