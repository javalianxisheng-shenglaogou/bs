package com.cms.module.workflow.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.workflow.dto.WorkflowDTO;
import com.cms.module.workflow.dto.WorkflowNodeDTO;
import com.cms.module.workflow.entity.Workflow;
import com.cms.module.workflow.entity.WorkflowNode;
import com.cms.module.workflow.repository.WorkflowNodeRepository;
import com.cms.module.workflow.repository.WorkflowRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowNodeRepository workflowNodeRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建工作流
     */
    @Transactional
    public WorkflowDTO createWorkflow(WorkflowDTO dto) {
        log.info("创建工作流: {}", dto.getName());

        // 检查代码是否已存在
        if (workflowRepository.existsByCodeAndDeletedFalse(dto.getCode())) {
            throw new BusinessException("工作流代码已存在: " + dto.getCode());
        }

        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(dto, workflow, "id", "nodes");
        
        if (workflow.getStatus() == null) {
            workflow.setStatus("DRAFT");
        }
        if (workflow.getVersion() == null) {
            workflow.setVersion(1);
        }

        Workflow saved = workflowRepository.save(workflow);

        // 保存节点
        if (dto.getNodes() != null && !dto.getNodes().isEmpty()) {
            saveWorkflowNodes(saved.getId(), dto.getNodes());
        }

        return convertToDTO(saved);
    }

    /**
     * 更新工作流
     */
    @Transactional
    public WorkflowDTO updateWorkflow(Long id, WorkflowDTO dto) {
        log.info("更新工作流: id={}", id);

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流不存在: " + id));

        if (workflow.getDeleted()) {
            throw new BusinessException("工作流已删除: " + id);
        }

        // 检查代码是否被其他工作流使用
        if (!workflow.getCode().equals(dto.getCode()) &&
                workflowRepository.existsByCodeAndDeletedFalse(dto.getCode())) {
            throw new BusinessException("工作流代码已存在: " + dto.getCode());
        }

        BeanUtils.copyProperties(dto, workflow, "id", "version", "createdAt", "createdBy", "nodes");

        Workflow saved = workflowRepository.save(workflow);

        // 更新节点
        if (dto.getNodes() != null) {
            // 删除旧节点
            workflowNodeRepository.deleteByWorkflowId(id);
            // 保存新节点
            saveWorkflowNodes(id, dto.getNodes());
        }

        return convertToDTO(saved);
    }

    /**
     * 删除工作流
     */
    @Transactional
    public void deleteWorkflow(Long id) {
        log.info("删除工作流: id={}", id);

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流不存在: " + id));

        workflow.setDeleted(true);
        workflowRepository.save(workflow);
    }

    /**
     * 获取工作流详情
     */
    public WorkflowDTO getWorkflowById(Long id) {
        log.debug("获取工作流详情: id={}", id);

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流不存在: " + id));

        if (workflow.getDeleted()) {
            throw new BusinessException("工作流已删除: " + id);
        }

        return convertToDTO(workflow);
    }

    /**
     * 根据代码获取工作流
     */
    public WorkflowDTO getWorkflowByCode(String code) {
        log.debug("根据代码获取工作流: code={}", code);

        Workflow workflow = workflowRepository.findByCodeAndDeletedFalse(code)
                .orElseThrow(() -> new BusinessException("工作流不存在: " + code));

        return convertToDTO(workflow);
    }

    /**
     * 获取所有工作流
     */
    public List<WorkflowDTO> getAllWorkflows() {
        log.debug("获取所有工作流");

        List<Workflow> workflows = workflowRepository.findByDeletedFalse();
        return workflows.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询工作流
     */
    public Page<WorkflowDTO> getWorkflows(int page, int size, String sortBy, String sortDir) {
        log.debug("分页查询工作流: page={}, size={}", page, size);

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Workflow> workflowPage = workflowRepository.findAll(pageable);

        return workflowPage.map(this::convertToDTO);
    }

    /**
     * 更新工作流状态
     */
    @Transactional
    public void updateWorkflowStatus(Long id, String status) {
        log.info("更新工作流状态: id={}, status={}", id, status);

        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工作流不存在: " + id));

        workflow.setStatus(status);
        workflowRepository.save(workflow);
    }

    /**
     * 保存工作流节点
     */
    private void saveWorkflowNodes(Long workflowId, List<WorkflowNodeDTO> nodeDTOs) {
        for (WorkflowNodeDTO nodeDTO : nodeDTOs) {
            WorkflowNode node = new WorkflowNode();
            BeanUtils.copyProperties(nodeDTO, node, "id", "approverIds");
            node.setWorkflowId(workflowId);

            // 转换approverIds为JSON
            if (nodeDTO.getApproverIds() != null) {
                try {
                    node.setApproverIds(objectMapper.writeValueAsString(nodeDTO.getApproverIds()));
                } catch (JsonProcessingException e) {
                    log.error("转换approverIds失败", e);
                }
            }

            workflowNodeRepository.save(node);
        }
    }

    /**
     * 转换为DTO
     */
    private WorkflowDTO convertToDTO(Workflow workflow) {
        WorkflowDTO dto = new WorkflowDTO();
        BeanUtils.copyProperties(workflow, dto);

        // 加载节点
        List<WorkflowNode> nodes = workflowNodeRepository.findByWorkflowIdAndDeletedFalseOrderBySortOrder(workflow.getId());
        dto.setNodes(nodes.stream().map(this::convertNodeToDTO).collect(Collectors.toList()));

        return dto;
    }

    /**
     * 转换节点为DTO
     */
    private WorkflowNodeDTO convertNodeToDTO(WorkflowNode node) {
        WorkflowNodeDTO dto = new WorkflowNodeDTO();
        BeanUtils.copyProperties(node, dto, "approverIds");

        // 解析approverIds
        if (node.getApproverIds() != null) {
            try {
                List<Long> approverIds = objectMapper.readValue(node.getApproverIds(), new TypeReference<List<Long>>() {});
                dto.setApproverIds(approverIds);
            } catch (JsonProcessingException e) {
                log.error("解析approverIds失败", e);
                dto.setApproverIds(new ArrayList<>());
            }
        }

        return dto;
    }
}

