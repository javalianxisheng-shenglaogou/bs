package com.multisite.cms.service;

import com.multisite.cms.BaseTestConfig;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.UserStatus;
import com.multisite.cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public class UserServiceTest extends BaseTestConfig {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // 清理测试数据
        userRepository.deleteAll();

        // 创建测试用户 - 注意：这里使用原始密码，让createUser方法来加密
        testUser = User.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .passwordHash(TEST_PASSWORD)  // 使用原始密码，不预先加密
                .nickname("测试用户")
                .status(UserStatus.ACTIVE)
                .build();
        testUser.setCreatedBy("system");
        testUser.setUpdatedBy("system");
    }
    
    @Test
    void testCreateUser() {
        // 执行创建用户
        User createdUser = userService.createUser(testUser, "admin");
        
        // 验证结果
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals(TEST_USERNAME, createdUser.getUsername());
        assertEquals(TEST_EMAIL, createdUser.getEmail());
        assertEquals(UserStatus.ACTIVE, createdUser.getStatus());
        assertEquals("admin", createdUser.getCreatedBy());
        
        // 验证密码已加密
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, createdUser.getPasswordHash()));
    }
    
    @Test
    void testGetUserById() {
        // 先创建用户
        User savedUser = userRepository.save(testUser);
        
        // 执行查询
        Optional<User> foundUser = userService.getUserById(savedUser.getId());
        
        // 验证结果
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(TEST_USERNAME, foundUser.get().getUsername());
    }
    
    @Test
    void testGetUserByUsername() {
        // 先创建用户
        userRepository.save(testUser);
        
        // 执行查询
        Optional<User> foundUser = userService.getUserByUsername(TEST_USERNAME);
        
        // 验证结果
        assertTrue(foundUser.isPresent());
        assertEquals(TEST_USERNAME, foundUser.get().getUsername());
        assertEquals(TEST_EMAIL, foundUser.get().getEmail());
    }
    
    @Test
    void testGetUserByEmail() {
        // 先创建用户
        userRepository.save(testUser);
        
        // 执行查询
        Optional<User> foundUser = userService.getUserByEmail(TEST_EMAIL);
        
        // 验证结果
        assertTrue(foundUser.isPresent());
        assertEquals(TEST_EMAIL, foundUser.get().getEmail());
        assertEquals(TEST_USERNAME, foundUser.get().getUsername());
    }
    
    @Test
    void testUpdateUser() {
        // 先创建用户
        User savedUser = userRepository.save(testUser);
        
        // 准备更新数据
        User updateUser = User.builder()
                .nickname("更新后的真实姓名")
                .email("updated@example.com")
                .build();
        
        // 执行更新
        User updatedUser = userService.updateUser(savedUser.getId(), updateUser, "admin");
        
        // 验证结果
        assertNotNull(updatedUser);
        assertEquals("更新后的真实姓名", updatedUser.getNickname());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("admin", updatedUser.getUpdatedBy());
        // 用户名不应该被更新
        assertEquals(TEST_USERNAME, updatedUser.getUsername());
    }
    
    @Test
    void testUpdateUserStatus() {
        // 先创建用户
        User savedUser = userRepository.save(testUser);
        
        // 执行状态更新
        User updatedUser = userService.updateUserStatus(savedUser.getId(), UserStatus.INACTIVE, "admin");
        
        // 验证结果
        assertNotNull(updatedUser);
        assertEquals(UserStatus.INACTIVE, updatedUser.getStatus());
        assertEquals("admin", updatedUser.getUpdatedBy());
    }
    
    @Test
    void testDeleteUser() {
        // 先创建用户
        User savedUser = userRepository.save(testUser);
        
        // 执行删除
        userService.deleteUser(savedUser.getId(), "admin");
        
        // 验证结果 - 应该是软删除
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertTrue(deletedUser.isPresent());
        assertTrue(deletedUser.get().isDeleted());
        assertEquals("admin", deletedUser.get().getUpdatedBy());
    }
    
    @Test
    void testExistsByUsername() {
        // 先创建用户
        userRepository.save(testUser);
        
        // 测试存在的用户名
        assertTrue(userService.existsByUsername(TEST_USERNAME));
        
        // 测试不存在的用户名
        assertFalse(userService.existsByUsername("nonexistent"));
    }
    
    @Test
    void testExistsByEmail() {
        // 先创建用户
        userRepository.save(testUser);
        
        // 测试存在的邮箱
        assertTrue(userService.existsByEmail(TEST_EMAIL));
        
        // 测试不存在的邮箱
        assertFalse(userService.existsByEmail("nonexistent@example.com"));
    }
}
