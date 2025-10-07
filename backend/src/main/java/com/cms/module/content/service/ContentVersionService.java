package com.cms.module.content.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.content.dto.ContentVersionDTO;
import com.cms.module.content.entity.Content;
import com.cms.module.content.entity.ContentVersion;
import com.cms.module.content.entity.ContentVersionLog;
import com.cms.module.content.repository.ContentRepository;
import com.cms.module.content.repository.ContentVersionLogRepository;
import com.cms.module.content.repository.ContentVersionRepository;
import com.cms.security.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 内容版本控制服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentVersionService {

    private final ContentVersionRepository versionRepository;
    private final ContentVersionLogRepository versionLogRepository;
    private final ContentRepository contentRepository;
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    /**
     * 创建内容版本
     */
    @Transactional
    public ContentVersion createVersion(Long contentId, String changeSummary) {
        // 将无修改类型的创建版本委托给重载方法，默认修改类型为 UPDATE
        // 这样既保持向后兼容，又能支持不同的修改类型（如 PUBLISH、UNPUBLISH、RESTORE 等）
        return createVersion(contentId, "UPDATE", changeSummary);
    }

    /**
     * 创建内容版本（带修改类型）
     *
     * @param contentId     内容ID
     * @param changeType    修改类型：CREATE/UPDATE/PUBLISH/UNPUBLISH/RESTORE 等
     * @param changeSummary 修改摘要（用于记录本次版本变更的说明）
     * @return 新创建的版本实体
     */
    @Transactional
    public ContentVersion createVersion(Long contentId, String changeType, String changeSummary) {
        log.info("创建内容版本: contentId={}, changeType={}, changeSummary={}", contentId, changeType, changeSummary);

        // 获取内容，如果不存在则抛出业务异常
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException("内容不存在"));

        // 获取当前最大版本号，新的版本号在其基础上加1
        Integer maxVersion = versionRepository.findMaxVersionNumberByContentId(contentId);
        int newVersionNumber = (maxVersion == null ? 0 : maxVersion) + 1;

        // 构建版本快照，将当前内容的关键字段复制到版本中
        ContentVersion version = new ContentVersion();
        version.setContentId(contentId);
        version.setVersionNumber(newVersionNumber);
        version.setTitle(content.getTitle());
        version.setContent(content.getContent());
        version.setChangeSummary(changeSummary);
        version.setChangeType(changeType); // 设置本次变更的类型
        version.setCreatedBy(getCurrentUserId());
        version.setCreatedAt(LocalDateTime.now());

        // 复制其他相关字段，确保版本快照尽可能完整
        version.setSlug(content.getSlug());
        version.setSummary(content.getSummary());
        version.setCoverImage(content.getCoverImage());
        version.setContentType(content.getContentType());
        version.setStatus(content.getStatus());
        version.setIsTop(content.getIsTop());
        version.setIsFeatured(content.getIsFeatured());
        version.setIsOriginal(content.getIsOriginal());

        // 持久化版本快照
        version = versionRepository.save(version);

        // 记录操作日志，标记为 CREATE（创建了一个新的版本记录）
        createVersionLog(contentId, version.getId(), "CREATE", "创建版本 v" + newVersionNumber + " (" + changeType + ")");

        log.info("版本创建成功: versionId={}, versionNumber={}, changeType={}", version.getId(), newVersionNumber, changeType);
        return version;
    }

    /**
     * 获取内容的版本列表
     */
    public Page<ContentVersionDTO> getVersionList(Long contentId, int page, int size) {
        log.info("获取版本列表: contentId={}, page={}, size={}", contentId, page, size);

        Sort sort = Sort.by(Sort.Direction.DESC, "versionNumber");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ContentVersion> versionPage = versionRepository.findByContentIdOrderByVersionNumberDesc(contentId, pageable);
        return versionPage.map(this::convertToDTO);
    }

    /**
     * 获取指定版本
     */
    public ContentVersionDTO getVersion(Long contentId, Integer versionNumber) {
        log.info("获取版本详情: contentId={}, versionNumber={}", contentId, versionNumber);

        ContentVersion version = versionRepository.findByContentIdAndVersionNumber(contentId, versionNumber)
                .orElseThrow(() -> new BusinessException("版本不存在"));

        // 记录查看日志
        createVersionLog(contentId, version.getId(), "VIEW", "查看版本 v" + versionNumber);

        return convertToDTO(version);
    }

    /**
     * 恢复到指定版本
     */
    @Transactional
    public void restoreVersion(Long contentId, Integer versionNumber) {
        log.info("恢复版本: contentId={}, versionNumber={}", contentId, versionNumber);

        // 获取目标版本
        ContentVersion targetVersion = versionRepository.findByContentIdAndVersionNumber(contentId, versionNumber)
                .orElseThrow(() -> new BusinessException("版本不存在"));

        // 获取内容
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException("内容不存在"));

        // 检查内容状态
        if ("PUBLISHED".equals(content.getStatus())) {
            throw new BusinessException("已发布的内容不能直接恢复版本，请先取消发布");
        }

        // 恢复内容
        content.setTitle(targetVersion.getTitle());
        content.setContent(targetVersion.getContent());
        content.setUpdatedAt(LocalDateTime.now());
        content.setUpdatedBy(getCurrentUserId());
        contentRepository.save(content);

        // 创建新版本（恢复操作也会创建新版本，类型为 RESTORE）
        createVersion(contentId, "RESTORE", "恢复到版本 v" + versionNumber);

        // 记录恢复日志
        createVersionLog(contentId, targetVersion.getId(), "RESTORE", 
                "恢复到版本 v" + versionNumber);

        log.info("版本恢复成功: contentId={}, versionNumber={}", contentId, versionNumber);
    }

    /**
     * 比较两个版本
     */
    public Map<String, Object> compareVersions(Long contentId, Integer version1, Integer version2) {
        log.info("比较版本: contentId={}, version1={}, version2={}", contentId, version1, version2);

        ContentVersion v1 = versionRepository.findByContentIdAndVersionNumber(contentId, version1)
                .orElseThrow(() -> new BusinessException("版本 v" + version1 + " 不存在"));

        ContentVersion v2 = versionRepository.findByContentIdAndVersionNumber(contentId, version2)
                .orElseThrow(() -> new BusinessException("版本 v" + version2 + " 不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("version1", convertToDTO(v1));
        result.put("version2", convertToDTO(v2));

        // 简单的差异标记
        Map<String, Boolean> differences = new HashMap<>();
        differences.put("title", !v1.getTitle().equals(v2.getTitle()));
        differences.put("content", !v1.getContent().equals(v2.getContent()));
        result.put("differences", differences);

        // 记录比较日志
        createVersionLog(contentId, null, "COMPARE", 
                "比较版本 v" + version1 + " 和 v" + version2);

        return result;
    }

    /**
     * 获取版本统计信息
     */
    public Map<String, Object> getVersionStatistics(Long contentId) {
        log.info("获取版本统计: contentId={}", contentId);

        long totalVersions = versionRepository.countByContentId(contentId);
        Integer maxVersion = versionRepository.findMaxVersionNumberByContentId(contentId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVersions", totalVersions);
        stats.put("currentVersion", maxVersion != null ? maxVersion : 0);

        return stats;
    }

    /**
     * 创建版本操作日志
     */
    private void createVersionLog(Long contentId, Long versionId, String operationType, String detail) {
        try {
            ContentVersionLog log = new ContentVersionLog();
            log.setContentId(contentId);
            log.setVersionId(versionId);
            log.setOperationType(operationType);
            log.setOperatorId(getCurrentUserId());
            log.setOperatorName(getCurrentUsername());
            log.setCreatedAt(LocalDateTime.now());

            // 记录IP和User-Agent
            if (request != null) {
                log.setIpAddress(getClientIp());
                log.setUserAgent(request.getHeader("User-Agent"));
            }

            // 记录操作详情
            Map<String, String> detailMap = new HashMap<>();
            detailMap.put("detail", detail);
            log.setOperationDetail(objectMapper.writeValueAsString(detailMap));

            versionLogRepository.save(log);
        } catch (Exception e) {
            this.log.error("创建版本日志失败", e);
        }
    }

    /**
     * 转换为DTO
     */
    /**
     * 为指定版本打标签（标记为重要）
     *
     * @param contentId     内容ID
     * @param versionNumber 版本号
     * @param tagName       标签名称（例如："里程碑"、"发布前"）
     * @return 更新后的版本DTO
     */
    @Transactional
    public ContentVersionDTO tagVersion(Long contentId, Integer versionNumber, String tagName) {
        log.info("标记版本: contentId={}, versionNumber={}, tagName={}", contentId, versionNumber, tagName);

        // 查找指定版本
        ContentVersion version = versionRepository.findByContentIdAndVersionNumber(contentId, versionNumber)
                .orElseThrow(() -> new BusinessException("版本不存在"));

        // 更新标签字段
        version.setIsTagged(true);
        version.setTagName(tagName);
        versionRepository.save(version);

        // 记录标记日志
        createVersionLog(contentId, version.getId(), "TAG", "为版本 v" + versionNumber + " 打标签: " + tagName);

        return convertToDTO(version);
    }

    /**
     * 删除指定版本
     *
     * @param contentId     内容ID
     * @param versionNumber 版本号
     */
    @Transactional
    public void deleteVersion(Long contentId, Integer versionNumber) {
        log.info("删除版本: contentId={}, versionNumber={}", contentId, versionNumber);

        // 查找指定版本
        ContentVersion version = versionRepository.findByContentIdAndVersionNumber(contentId, versionNumber)
                .orElseThrow(() -> new BusinessException("版本不存在"));

        // 执行删除操作
        versionRepository.delete(version);

        // 记录删除日志
        createVersionLog(contentId, version.getId(), "DELETE", "删除版本 v" + versionNumber);

        log.info("版本删除成功: contentId={}, versionNumber={}", contentId, versionNumber);
    }

    private ContentVersionDTO convertToDTO(ContentVersion version) {
        ContentVersionDTO dto = new ContentVersionDTO();
        // 基本标识信息
        dto.setId(version.getId());
        dto.setContentId(version.getContentId());
        dto.setVersionNumber(version.getVersionNumber());
        
        // 内容快照字段
        dto.setTitle(version.getTitle());
        dto.setSlug(version.getSlug());
        dto.setSummary(version.getSummary());
        dto.setContent(version.getContent());
        dto.setCoverImage(version.getCoverImage());
        dto.setContentType(version.getContentType());
        dto.setStatus(version.getStatus());
        dto.setIsTop(version.getIsTop());
        dto.setIsFeatured(version.getIsFeatured());
        dto.setIsOriginal(version.getIsOriginal());
        dto.setTags(version.getTags());
        dto.setMetadata(version.getMetadata());
        
        // 版本变更信息
        dto.setChangeSummary(version.getChangeSummary());
        dto.setChangeType(version.getChangeType());
        
        // 创建者与时间信息（与DTO字段一致）
        dto.setCreatedBy(version.getCreatedBy());
        dto.setCreatedByName(version.getCreatedByName());
        dto.setCreatedAt(version.getCreatedAt());
        
        // 标签与扩展信息
        dto.setIsTagged(version.getIsTagged());
        dto.setTagName(version.getTagName());
        dto.setFileSize(version.getFileSize());
        return dto;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * 获取当前用户名
     */
    private String getCurrentUsername() {
        return SecurityUtils.getCurrentUsername();
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

