package com.cms.module.file.service;

import com.cms.common.exception.BusinessException;
import com.cms.module.file.dto.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务
 */
@Slf4j
@Service
public class FileService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.base-url:http://localhost:8080/api/files}")
    private String baseUrl;

    private String absoluteUploadPath;

    @PostConstruct
    public void init() {
        // 获取绝对路径
        File uploadDir = new File(uploadPath);
        if (!uploadDir.isAbsolute()) {
            // 如果是相对路径,转换为绝对路径
            uploadDir = new File(System.getProperty("user.dir"), uploadPath);
        }
        absoluteUploadPath = uploadDir.getAbsolutePath();
        log.info("文件上传目录: {}", absoluteUploadPath);

        // 确保目录存在
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            log.info("创建上传目录: {}, 结果: {}", absoluteUploadPath, created);
        }
    }

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 上传头像
     */
    public FileUploadResponse uploadAvatar(MultipartFile file) {
        // 验证文件
        validateImageFile(file);

        // 保存文件
        String relativePath = "avatars/" + getDatePath();
        return saveFile(file, relativePath);
    }

    /**
     * 上传图片
     */
    public FileUploadResponse uploadImage(MultipartFile file) {
        // 验证文件
        validateImageFile(file);

        // 保存文件
        String relativePath = "images/" + getDatePath();
        return saveFile(file, relativePath);
    }

    /**
     * 上传文件
     */
    public FileUploadResponse uploadFile(MultipartFile file) {
        // 验证文件
        validateFile(file);

        // 保存文件
        String relativePath = "files/" + getDatePath();
        return saveFile(file, relativePath);
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(400, "图片大小不能超过5MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(400, "只支持JPG、PNG、GIF、WEBP格式的图片");
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(400, "文件大小不能超过10MB");
        }
    }

    /**
     * 保存文件
     */
    private FileUploadResponse saveFile(MultipartFile file, String relativePath) {
        try {
            // 使用绝对路径创建目录
            Path dirPath = Paths.get(absoluteUploadPath, relativePath);
            Files.createDirectories(dirPath);

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 构建URL
            String fileUrl = baseUrl + "/" + relativePath + "/" + filename;

            log.info("文件上传成功: 原文件名={}, 保存路径={}", originalFilename, filePath.toAbsolutePath());

            return FileUploadResponse.builder()
                    .filename(filename)
                    .originalFilename(originalFilename)
                    .url(fileUrl)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .path(relativePath + "/" + filename)
                    .build();

        } catch (IOException e) {
            log.error("文件上传失败: 原文件名={}, 错误信息={}", file.getOriginalFilename(), e.getMessage(), e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取日期路径（yyyy/MM/dd）
     */
    private String getDatePath() {
        LocalDate now = LocalDate.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}

