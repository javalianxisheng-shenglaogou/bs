package com.cms.module.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    
    /**
     * 文件名
     */
    private String filename;
    
    /**
     * 原始文件名
     */
    private String originalFilename;
    
    /**
     * 文件URL
     */
    private String url;
    
    /**
     * 文件大小（字节）
     */
    private Long size;
    
    /**
     * 文件类型
     */
    private String contentType;
    
    /**
     * 存储路径
     */
    private String path;
}

