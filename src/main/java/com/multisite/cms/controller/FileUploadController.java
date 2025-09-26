package com.multisite.cms.controller;

import com.multisite.cms.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "文件上传", description = "文件上传相关API")
public class FileUploadController {

    @Value("${app.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${app.upload.max-size:10485760}")
    private long maxFileSize;

    @Value("${app.upload.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar}")
    private String allowedTypes;

    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif");

    /**
     * 上传文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件", description = "上传单个文件")
    public ApiResponse<Map<String, String>> uploadFile(
            @Parameter(description = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("Upload file request: {}", file.getOriginalFilename());
        
        try {
            // 验证文件
            validateFile(file);
            
            // 创建上传目录
            createUploadDirectory();
            
            // 生成文件名
            String fileName = generateFileName(file);
            
            // 保存文件
            Path filePath = Paths.get(uploadPath, fileName);
            file.transferTo(filePath.toFile());
            
            // 构建返回URL
            String fileUrl = "/uploads/" + fileName;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);
            result.put("originalName", file.getOriginalFilename());
            result.put("size", String.valueOf(file.getSize()));
            
            log.info("File uploaded successfully: {}", fileName);
            return ApiResponse.success("文件上传成功", result);
            
        } catch (Exception e) {
            log.error("File upload failed: {}", file.getOriginalFilename(), e);
            return ApiResponse.badRequest(e.getMessage());
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("文件大小不能超过 " + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new RuntimeException("文件名不能为空");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!isAllowedType(extension)) {
            throw new RuntimeException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 创建上传目录
     */
    private void createUploadDirectory() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        return timestamp + "_" + uuid + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedType(String extension) {
        if (!StringUtils.hasText(extension)) {
            return false;
        }
        
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        return allowedTypeList.contains(extension.toLowerCase());
    }
}
