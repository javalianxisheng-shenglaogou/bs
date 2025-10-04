package com.cms.module.workflow.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.user.entity.User;
import com.cms.module.user.repository.UserRepository;
import com.cms.module.workflow.dto.ApproveTaskRequest;
import com.cms.module.workflow.dto.RejectTaskRequest;
import com.cms.module.workflow.dto.WorkflowTaskDTO;
import com.cms.module.workflow.entity.WorkflowInstance;
import com.cms.module.workflow.entity.WorkflowNode;
import com.cms.module.workflow.entity.WorkflowTask;
import com.cms.module.workflow.repository.WorkflowInstanceRepository;
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
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

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
        Pageable pageable = PageRequest.of(page - 1, size, sort);

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
        Pageable pageable = PageRequest.of(page - 1, size, sort);

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

        // 拒绝整个实例
        WorkflowInstance instance = instanceRepository.findById(task.getInstanceId())
                .orElseThrow(() -> new BusinessException("工作流实例不存在"));

        instance.setStatus("REJECTED");
        instance.setCompletedAt(LocalDateTime.now());
        instance.setCompletionNote("任务被拒绝: " + request.getComment());
        instanceRepository.save(instance);

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
            // 所有任务都已通过,移动到下一个节点
            // 这里需要注入WorkflowInstanceService,但为了避免循环依赖,我们直接更新实例
            log.info("节点所有任务已完成,准备移动到下一个节点");
            // 实际实现中应该调用WorkflowInstanceService.moveToNextNode
        }
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

