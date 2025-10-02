package com.cms.module.file.controller;

import com.cms.common.base.ApiResponse;
import com.cms.module.file.dto.FileUploadResponse;
import com.cms.module.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Tag(name = "文件管理", description = "文件上传和管理相关接口")
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    public ApiResponse<FileUploadResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("上传头像: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        FileUploadResponse response = fileService.uploadAvatar(file);
        return ApiResponse.success(response);
    }

    /**
     * 上传图片
     */
    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "上传图片", description = "上传内容图片")
    public ApiResponse<FileUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        FileUploadResponse response = fileService.uploadImage(file);
        return ApiResponse.success(response);
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "上传文件", description = "上传任意类型文件")
    public ApiResponse<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("上传文件: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        FileUploadResponse response = fileService.uploadFile(file);
        return ApiResponse.success(response);
    }
}

