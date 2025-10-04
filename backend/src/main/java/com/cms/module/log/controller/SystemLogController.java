package com.cms.module.log.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.log.dto.SystemLogDTO;
import com.cms.module.log.dto.SystemLogQueryDTO;
import com.cms.module.log.service.SystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统日志控制器
 *
 * @author CMS Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "系统日志", description = "系统日志管理相关接口")
public class SystemLogController {

    private final SystemLogService systemLogService;

    /**
     * 分页查询日志
     */
    @GetMapping
    @PreAuthorize("hasAuthority('log:list')")
    @Operation(summary = "分页查询日志", description = "根据条件分页查询系统日志")
    public ApiResponse<Page<SystemLogDTO>> getLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) Boolean isSuccess,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("分页查询日志请求: userId={}, username={}, module={}, level={}, page={}, size={}",
                userId, username, module, level, page, size);

        SystemLogQueryDTO queryDTO = new SystemLogQueryDTO();
        queryDTO.setUserId(userId);
        queryDTO.setUsername(username);
        queryDTO.setModule(module);
        queryDTO.setAction(action);
        queryDTO.setLevel(level);
        queryDTO.setIpAddress(ipAddress);
        queryDTO.setIsSuccess(isSuccess);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setSortBy(sortBy);
        queryDTO.setSortDir(sortDir);

        Page<SystemLogDTO> result = systemLogService.getLogs(queryDTO);
        return ApiResponse.success(result);
    }

    /**
     * 获取日志详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('log:view')")
    @Operation(summary = "获取日志详情", description = "根据ID获取日志详细信息")
    public ApiResponse<SystemLogDTO> getLog(@PathVariable Long id) {
        log.info("获取日志详情请求: id={}", id);
        SystemLogDTO result = systemLogService.getLogById(id);
        return ApiResponse.success(result);
    }

    /**
     * 批量删除日志
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('log:delete')")
    @Operation(summary = "批量删除日志", description = "批量删除系统日志")
    public ApiResponse<Void> deleteLogs(@RequestBody List<Long> ids) {
        log.info("批量删除日志请求: ids={}", ids);
        systemLogService.deleteLogs(ids);
        return ApiResponse.success();
    }

    /**
     * 清空日志
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('log:delete')")
    @Operation(summary = "清空日志", description = "根据条件清空日志")
    public ApiResponse<Void> clearLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        log.info("清空日志请求: level={}, startTime={}, endTime={}", level, startTime, endTime);
        systemLogService.clearLogs(level, startTime, endTime);
        return ApiResponse.success();
    }

    /**
     * 获取日志统计
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('log:list')")
    @Operation(summary = "获取日志统计", description = "获取日志统计信息")
    public ApiResponse<List<Object[]>> getStatistics() {
        log.info("获取日志统计请求");
        List<Object[]> result = systemLogService.getLogStatistics();
        return ApiResponse.success(result);
    }
}

