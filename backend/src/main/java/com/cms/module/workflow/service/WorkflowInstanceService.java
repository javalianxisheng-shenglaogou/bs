package com.cms.module.workflow.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.workflow.dto.StartWorkflowRequest;
import com.cms.module.workflow.dto.WorkflowInstanceDTO;
import com.cms.module.workflow.entity.Workflow;
import com.cms.module.workflow.entity.WorkflowInstance;
import com.cms.module.workflow.entity.WorkflowNode;
import com.cms.module.workflow.repository.WorkflowInstanceRepository;
import com.cms.module.workflow.repository.WorkflowNodeRepository;
import com.cms.module.workflow.repository.WorkflowRepository;
import com.cms.security.service.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工作流实例服务
 */
@Slf4j
@Service
public class WorkflowInstanceService {

    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository nodeRepository;
    private final ApplicationContext applicationContext;

    public WorkflowInstanceService(
            WorkflowInstanceRepository instanceRepository,
            WorkflowRepository workflowRepository,
            WorkflowNodeRepository nodeRepository,
            ApplicationContext applicationContext) {
        this.instanceRepository = instanceRepository;
        this.workflowRepository = workflowRepository;
        this.nodeRepository = nodeRepository;
        this.applicationContext = applicationContext;
    }

    /**
     * 获取WorkflowTaskService (延迟获取以避免循环依赖)
     */
    private WorkflowTaskService getTaskService() {
        return applicationContext.getBean(WorkflowTaskService.class);
    }

    /**
     * 启动工作流
     */
    @Transactional
    public WorkflowInstanceDTO startWorkflow(StartWorkflowRequest request) {
        log.info("启动工作流: workflowCode={}, businessType={}, businessId={}",
                request.getWorkflowCode(), request.getBusinessType(), request.getBusinessId());

        // 获取工作流
        Workflow workflow = workflowRepository.findByCodeAndDeletedFalse(request.getWorkflowCode())
                .orElseThrow(() -> new BusinessException("工作流不存在: " + request.getWorkflowCode()));

        if (!"ACTIVE".equals(workflow.getStatus())) {
            throw new BusinessException("工作流未激活: " + request.getWorkflowCode());
        }

        // 检查是否已有运行中的实例
        instanceRepository.findByBusinessTypeAndBusinessIdAndDeletedFalse(
                request.getBusinessType(), request.getBusinessId()
        ).ifPresent(instance -> {
            if ("RUNNING".equals(instance.getStatus())) {
                throw new BusinessException("该业务已有运行中的工作流实例");
            }
        });

        // 获取当前用户
        Long currentUserId = getCurrentUserId();

        // 创建实例
        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowId(workflow.getId());
        instance.setBusinessType(request.getBusinessType());
        instance.setBusinessId(request.getBusinessId());
        instance.setBusinessTitle(request.getBusinessTitle());
        instance.setStatus("RUNNING");
        instance.setInitiatorId(currentUserId);
        instance.setInitiatedAt(LocalDateTime.now());

        // 获取开始节点
        List<WorkflowNode> startNodes = nodeRepository.findByWorkflowIdAndNodeTypeAndDeletedFalse(
                workflow.getId(), "START");
        
        if (startNodes.isEmpty()) {
            throw new BusinessException("工作流没有开始节点");
        }

        WorkflowNode startNode = startNodes.get(0);
        instance.setCurrentNodeId(startNode.getId());

        WorkflowInstance saved = instanceRepository.save(instance);

        // 查找第一个审批节点并创建任务
        List<WorkflowNode> approvalNodes = nodeRepository.findByWorkflowIdAndNodeTypeAndDeletedFalse(
                workflow.getId(), "APPROVAL");
        
        if (!approvalNodes.isEmpty()) {
            WorkflowNode firstApprovalNode = approvalNodes.get(0);
            instance.setCurrentNodeId(firstApprovalNode.getId());
            instanceRepository.save(instance);

            // 创建审批任务
            getTaskService().createTasksForNode(saved.getId(), firstApprovalNode);
        }

        return convertToDTO(saved, workflow.getName());
    }

    /**
     * 获取实例详情
     */
    public WorkflowInstanceDTO getInstanceById(Long id) {
        log.debug("获取实例详情: id={}", id);

        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流实例不存在: " + id));

        if (instance.getDeleted()) {
            throw new BusinessException("工作流实例已删除: " + id);
        }

        Workflow workflow = workflowRepository.findById(instance.getWorkflowId())
                .orElseThrow(() -> new BusinessException("工作流不存在"));

        return convertToDTO(instance, workflow.getName());
    }

    /**
     * 分页查询实例
     */
    public Page<WorkflowInstanceDTO> getInstances(int page, int size, String sortBy, String sortDir) {
        log.debug("分页查询实例: page={}, size={}", page, size);

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<WorkflowInstance> instancePage = instanceRepository.findAll(pageable);

        return instancePage.map(instance -> {
            Workflow workflow = workflowRepository.findById(instance.getWorkflowId()).orElse(null);
            return convertToDTO(instance, workflow != null ? workflow.getName() : "");
        });
    }

    /**
     * 取消实例
     */
    @Transactional
    public void cancelInstance(Long id) {
        log.info("取消实例: id={}", id);

        WorkflowInstance instance = instanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流实例不存在: " + id));

        if (!"RUNNING".equals(instance.getStatus())) {
            throw new BusinessException("只能取消运行中的实例");
        }

        instance.setStatus("CANCELLED");
        instance.setCompletedAt(LocalDateTime.now());
        instanceRepository.save(instance);

        // 取消所有待办任务
        getTaskService().cancelTasksByInstanceId(id);
    }

    /**
     * 完成实例
     */
    @Transactional
    public void completeInstance(Long instanceId, String status, String note) {
        log.info("完成实例: instanceId={}, status={}", instanceId, status);

        WorkflowInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException("工作流实例不存在: " + instanceId));

        instance.setStatus(status);
        instance.setCompletedAt(LocalDateTime.now());
        instance.setCompletionNote(note);
        instanceRepository.save(instance);
    }

    /**
     * 移动到下一个节点
     */
    @Transactional
    public void moveToNextNode(Long instanceId, Long currentNodeId) {
        log.info("移动到下一个节点: instanceId={}, currentNodeId={}", instanceId, currentNodeId);

        WorkflowInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new BusinessException("工作流实例不存在: " + instanceId));

        // 获取当前节点
        WorkflowNode currentNode = nodeRepository.findById(currentNodeId)
                .orElseThrow(() -> new BusinessException("节点不存在: " + currentNodeId));

        // 查找下一个节点(简化版:按sortOrder查找)
        List<WorkflowNode> allNodes = nodeRepository.findByWorkflowIdAndDeletedFalseOrderBySortOrder(
                instance.getWorkflowId());

        int currentIndex = -1;
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).getId().equals(currentNodeId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex >= 0 && currentIndex < allNodes.size() - 1) {
            WorkflowNode nextNode = allNodes.get(currentIndex + 1);
            
            if ("END".equals(nextNode.getNodeType())) {
                // 到达结束节点,完成实例
                completeInstance(instanceId, "APPROVED", "工作流正常完成");
            } else if ("APPROVAL".equals(nextNode.getNodeType())) {
                // 到达审批节点,创建任务
                instance.setCurrentNodeId(nextNode.getId());
                instanceRepository.save(instance);
                getTaskService().createTasksForNode(instanceId, nextNode);
            }
        } else {
            // 没有下一个节点,完成实例
            completeInstance(instanceId, "APPROVED", "工作流正常完成");
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
    private WorkflowInstanceDTO convertToDTO(WorkflowInstance instance, String workflowName) {
        WorkflowInstanceDTO dto = new WorkflowInstanceDTO();
        BeanUtils.copyProperties(instance, dto);
        dto.setWorkflowName(workflowName);
        return dto;
    }
}

