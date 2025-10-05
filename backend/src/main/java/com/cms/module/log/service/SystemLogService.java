package com.cms.module.log.service;

import com.cms.module.log.dto.SystemLogDTO;
import com.cms.module.log.dto.SystemLogQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 系统日志服务 - 读取本地日志文件
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class SystemLogService {

    private static final String LOG_DIR = "logs";
    private static final String INFO_LOG_FILE = "multi-site-cms-info.log";
    private static final String ERROR_LOG_FILE = "multi-site-cms-error.log";
    // 修改正则表达式以匹配实际日志格式: 2025-10-05 00:00:11.329 [http-nio-8080-exec-6] INFO  com.cms.module.auth.service.AuthService - 用户登录: admin
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+\\[([^\\]]+)\\]\\s+(INFO|WARN|ERROR)\\s+([^-]+)\\s*-\\s*(.+)"
    );

    /**
     * 分页查询日志 - 从本地文件读取
     */
    public Page<SystemLogDTO> getLogs(SystemLogQueryDTO queryDTO) {
        log.info("分页查询日志: {}", queryDTO);

        try {
            // 读取所有日志
            List<SystemLogDTO> allLogs = readLogsFromFile();

            // 过滤日志
            List<SystemLogDTO> filteredLogs = filterLogs(allLogs, queryDTO);

            // 排序
            if ("desc".equalsIgnoreCase(queryDTO.getSortDir())) {
                Collections.reverse(filteredLogs);
            }

            // 分页（前端传递的page从0开始）
            int start = queryDTO.getPage() * queryDTO.getSize();
            int end = Math.min(start + queryDTO.getSize(), filteredLogs.size());

            List<SystemLogDTO> pageContent = start < filteredLogs.size()
                ? filteredLogs.subList(start, end)
                : Collections.emptyList();

            Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize());
            return new PageImpl<>(pageContent, pageable, filteredLogs.size());

        } catch (Exception e) {
            log.error("读取日志文件失败", e);
            return Page.empty();
        }
    }

    /**
     * 从日志文件读取日志
     */
    private List<SystemLogDTO> readLogsFromFile() throws IOException {
        List<SystemLogDTO> logs = new ArrayList<>();
        File logDir = new File(LOG_DIR);

        log.info("📂 日志目录: {}, 绝对路径: {}", LOG_DIR, logDir.getAbsolutePath());
        log.info("📂 日志目录是否存在: {}", logDir.exists());

        if (!logDir.exists()) {
            log.warn("日志目录不存在: {}", LOG_DIR);
            return logs;
        }

        // 读取INFO日志
        File infoLogFile = new File(logDir, INFO_LOG_FILE);
        log.info("📄 INFO日志文件: {}, 存在: {}, 大小: {} bytes",
            infoLogFile.getAbsolutePath(), infoLogFile.exists(), infoLogFile.length());

        if (infoLogFile.exists()) {
            List<SystemLogDTO> infoLogs = parseLogFile(infoLogFile, "INFO");
            log.info("✅ 解析INFO日志: {} 条", infoLogs.size());
            logs.addAll(infoLogs);
        }

        // 读取ERROR日志
        File errorLogFile = new File(logDir, ERROR_LOG_FILE);
        log.info("📄 ERROR日志文件: {}, 存在: {}, 大小: {} bytes",
            errorLogFile.getAbsolutePath(), errorLogFile.exists(), errorLogFile.length());

        if (errorLogFile.exists()) {
            List<SystemLogDTO> errorLogs = parseLogFile(errorLogFile, "ERROR");
            log.info("✅ 解析ERROR日志: {} 条", errorLogs.size());
            logs.addAll(errorLogs);
        }

        log.info("📊 总共解析日志: {} 条", logs.size());
        return logs;
    }

    /**
     * 解析日志文件
     */
    private List<SystemLogDTO> parseLogFile(File file, String defaultLevel) throws IOException {
        List<SystemLogDTO> logs = new ArrayList<>();
        long id = 1;
        int totalLines = 0;
        int matchedLines = 0;

        // 使用UTF-8编码读取日志文件
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                SystemLogDTO logDTO = parseLogLine(line, defaultLevel);
                if (logDTO != null) {
                    logDTO.setId(id++);
                    logs.add(logDTO);
                    matchedLines++;

                    // 打印前3条匹配的日志
                    if (matchedLines <= 3) {
                        log.info("✅ 匹配日志 #{}: {}", matchedLines, line);
                    }
                } else if (totalLines <= 3) {
                    // 打印前3条不匹配的日志
                    log.warn("❌ 不匹配日志 #{}: {}", totalLines, line);
                }
            }
        }

        log.info("📊 文件 {} 解析完成: 总行数={}, 匹配行数={}", file.getName(), totalLines, matchedLines);
        return logs;
    }

    /**
     * 解析单行日志
     * 日志格式: 2025-10-05 00:00:11.329 [http-nio-8080-exec-6] INFO  com.cms.module.auth.service.AuthService - 用户登录: admin
     * group(1) = 时间
     * group(2) = 线程
     * group(3) = 级别
     * group(4) = 模块
     * group(5) = 描述
     */
    private SystemLogDTO parseLogLine(String line, String defaultLevel) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.find()) {
            return null;
        }

        SystemLogDTO dto = new SystemLogDTO();
        dto.setCreatedAt(matcher.group(1));        // 时间
        dto.setLevel(matcher.group(3));            // 级别
        dto.setModule(matcher.group(4).trim());    // 模块（去除空格）
        dto.setDescription(matcher.group(5));      // 描述
        dto.setIsSuccess(!dto.getLevel().equals("ERROR"));

        return dto;
    }

    /**
     * 过滤日志
     */
    private List<SystemLogDTO> filterLogs(List<SystemLogDTO> logs, SystemLogQueryDTO queryDTO) {
        return logs.stream()
            .filter(log -> {
                if (StringUtils.hasText(queryDTO.getLevel()) && !queryDTO.getLevel().equals(log.getLevel())) {
                    return false;
                }
                if (StringUtils.hasText(queryDTO.getModule()) && !log.getModule().contains(queryDTO.getModule())) {
                    return false;
                }
                if (queryDTO.getIsSuccess() != null && !queryDTO.getIsSuccess().equals(log.getIsSuccess())) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取日志详情
     */
    public SystemLogDTO getLogById(Long id) {
        log.info("获取日志详情: id={}", id);
        try {
            List<SystemLogDTO> allLogs = readLogsFromFile();
            return allLogs.stream()
                .filter(logDTO -> logDTO.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("日志不存在: " + id));
        } catch (IOException e) {
            log.error("读取日志文件失败", e);
            throw new RuntimeException("读取日志失败", e);
        }
    }

    /**
     * 批量删除日志 - 不支持(日志文件不可删除)
     */
    public void deleteLogs(List<Long> ids) {
        log.warn("日志文件不支持删除操作: ids={}", ids);
        throw new UnsupportedOperationException("日志文件不支持删除操作");
    }

    /**
     * 清空日志 - 不支持(日志文件不可删除)
     */
    public void clearLogs(String level, String startTime, String endTime) {
        log.warn("日志文件不支持清空操作: level={}, startTime={}, endTime={}", level, startTime, endTime);
        throw new UnsupportedOperationException("日志文件不支持清空操作");
    }

    /**
     * 获取日志统计
     */
    public List<Object[]> getLogStatistics() {
        try {
            List<SystemLogDTO> allLogs = readLogsFromFile();
            // 简单统计
            long infoCount = allLogs.stream().filter(l -> "INFO".equals(l.getLevel())).count();
            long warnCount = allLogs.stream().filter(l -> "WARN".equals(l.getLevel())).count();
            long errorCount = allLogs.stream().filter(l -> "ERROR".equals(l.getLevel())).count();

            List<Object[]> stats = new ArrayList<>();
            stats.add(new Object[]{"INFO", infoCount});
            stats.add(new Object[]{"WARN", warnCount});
            stats.add(new Object[]{"ERROR", errorCount});
            return stats;
        } catch (IOException e) {
            log.error("读取日志文件失败", e);
            return Collections.emptyList();
        }
    }
}

