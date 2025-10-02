package com.cms.module.user.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户DTO
 */
@Data
public class UserDTO {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String mobile;
    
    private String nickname;
    
    private String realName;
    
    private String avatarUrl;
    
    private String gender;
    
    private LocalDate birthday;
    
    private String bio;
    
    private String status;
    
    private Boolean emailVerified;
    
    private Boolean mobileVerified;
    
    private LocalDateTime lastLoginAt;
    
    private String lastLoginIp;
    
    private Integer loginCount;
    
    private List<String> roles;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}

