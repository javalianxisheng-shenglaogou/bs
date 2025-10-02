package com.cms.module.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;

/**
 * 更新用户请求
 */
@Data
public class UserUpdateRequest {
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String mobile;
    
    private String nickname;
    
    private String realName;
    
    private String gender;
    
    private LocalDate birthday;
    
    private String bio;
    
    private String status;
    
    private List<Long> roleIds;
}

