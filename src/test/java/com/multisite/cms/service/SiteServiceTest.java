package com.multisite.cms.service;

import com.multisite.cms.BaseTestConfig;
import com.multisite.cms.common.PageResult;
import com.multisite.cms.entity.Site;
import com.multisite.cms.entity.User;
import com.multisite.cms.enums.SiteStatus;
import com.multisite.cms.enums.UserStatus;
import com.multisite.cms.repository.SiteRepository;
import com.multisite.cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 站点服务测试类
 * 
 * @author 姚奇奇
 * @version 1.0.0
 * @since 2024-01-01
 */
public class SiteServiceTest extends BaseTestConfig {

    @Autowired
    private SiteService siteService;
    
    @Autowired
    private SiteRepository siteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Site testSite;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        // 清理测试数据
        siteRepository.deleteAll();
        userRepository.deleteAll();
        
        // 创建测试用户
        testUser = User.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .nickname("测试用户")
                .status(UserStatus.ACTIVE)
                .build();
        testUser.setCreatedBy("system");
        testUser.setUpdatedBy("system");
        testUser = userRepository.save(testUser);
        
        // 创建测试站点
        testSite = Site.builder()
                .name(TEST_SITE_NAME)
                .code(TEST_SITE_CODE)
                .domain(TEST_SITE_DOMAIN)
                .description("这是一个测试站点")
                .status(SiteStatus.ACTIVE)
                .owner(testUser)
                .build();
        testSite.setCreatedBy("admin");
        testSite.setUpdatedBy("admin");
    }
    
    @Test
    void testCreateSite() {
        // 执行创建站点
        Site createdSite = siteService.createSite(testSite, "admin");
        
        // 验证结果
        assertNotNull(createdSite);
        assertNotNull(createdSite.getId());
        assertEquals(TEST_SITE_NAME, createdSite.getName());
        assertEquals(TEST_SITE_CODE, createdSite.getCode());
        assertEquals(TEST_SITE_DOMAIN, createdSite.getDomain());
        assertEquals(SiteStatus.ACTIVE, createdSite.getStatus());
        assertEquals(testUser.getId(), createdSite.getOwner().getId());
        assertEquals("admin", createdSite.getCreatedBy());
    }
    
    @Test
    void testGetSiteById() {
        // 先创建站点
        Site savedSite = siteRepository.save(testSite);
        
        // 执行查询
        Optional<Site> foundSite = siteService.getSiteById(savedSite.getId());
        
        // 验证结果
        assertTrue(foundSite.isPresent());
        assertEquals(savedSite.getId(), foundSite.get().getId());
        assertEquals(TEST_SITE_NAME, foundSite.get().getName());
        assertEquals(TEST_SITE_CODE, foundSite.get().getCode());
    }
    
    @Test
    void testGetSiteByCode() {
        // 先创建站点
        siteRepository.save(testSite);
        
        // 执行查询
        Optional<Site> foundSite = siteService.getSiteByCode(TEST_SITE_CODE);
        
        // 验证结果
        assertTrue(foundSite.isPresent());
        assertEquals(TEST_SITE_CODE, foundSite.get().getCode());
        assertEquals(TEST_SITE_NAME, foundSite.get().getName());
    }
    
    @Test
    void testGetSiteByDomain() {
        // 先创建站点
        siteRepository.save(testSite);
        
        // 执行查询
        Optional<Site> foundSite = siteService.getSiteByDomain(TEST_SITE_DOMAIN);
        
        // 验证结果
        assertTrue(foundSite.isPresent());
        assertEquals(TEST_SITE_DOMAIN, foundSite.get().getDomain());
        assertEquals(TEST_SITE_NAME, foundSite.get().getName());
    }
    
    @Test
    void testGetSites() {
        // 创建多个测试站点
        siteRepository.save(testSite);
        
        Site site2 = Site.builder()
                .name("测试站点2")
                .code("test-site-2")
                .domain("test2.example.com")
                .status(SiteStatus.ACTIVE)
                .owner(testUser)
                .build();
        site2.setCreatedBy("admin");
        site2.setUpdatedBy("admin");
        siteRepository.save(site2);
        
        // 执行分页查询
        PageResult<Site> result = siteService.getSites(1, 10, "createdAt", "desc", null, null);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
    }
    
    @Test
    void testUpdateSite() {
        // 先创建站点
        Site savedSite = siteRepository.save(testSite);
        
        // 准备更新数据
        Site updateSite = Site.builder()
                .name("更新后的站点名称")
                .description("更新后的站点描述")
                .build();
        
        // 执行更新
        Site updatedSite = siteService.updateSite(savedSite.getId(), updateSite, "admin");
        
        // 验证结果
        assertNotNull(updatedSite);
        assertEquals("更新后的站点名称", updatedSite.getName());
        assertEquals("更新后的站点描述", updatedSite.getDescription());
        assertEquals("admin", updatedSite.getUpdatedBy());
        // 代码不应该被更新
        assertEquals(TEST_SITE_CODE, updatedSite.getCode());
    }
    
    @Test
    void testUpdateSiteStatus() {
        // 先创建站点
        Site savedSite = siteRepository.save(testSite);
        
        // 执行状态更新
        Site updatedSite = siteService.updateSiteStatus(savedSite.getId(), SiteStatus.MAINTENANCE, "admin");
        
        // 验证结果
        assertNotNull(updatedSite);
        assertEquals(SiteStatus.MAINTENANCE, updatedSite.getStatus());
        assertEquals("admin", updatedSite.getUpdatedBy());
    }
    
    @Test
    void testDeleteSite() {
        // 先创建站点
        Site savedSite = siteRepository.save(testSite);
        
        // 执行删除
        siteService.deleteSite(savedSite.getId(), "admin");
        
        // 验证结果 - 应该是软删除
        Optional<Site> deletedSite = siteRepository.findById(savedSite.getId());
        assertTrue(deletedSite.isPresent());
        assertTrue(deletedSite.get().isDeleted());
        assertEquals("admin", deletedSite.get().getUpdatedBy());
    }
    
    @Test
    void testExistsByCode() {
        // 先创建站点
        siteRepository.save(testSite);
        
        // 测试存在的站点代码
        assertTrue(siteService.existsByCode(TEST_SITE_CODE, null));
        
        // 测试不存在的站点代码
        assertFalse(siteService.existsByCode("nonexistent", null));
    }
    
    @Test
    void testExistsByDomain() {
        // 先创建站点
        siteRepository.save(testSite);
        
        // 测试存在的域名
        assertTrue(siteService.existsByDomain(TEST_SITE_DOMAIN, null));
        
        // 测试不存在的域名
        assertFalse(siteService.existsByDomain("nonexistent.com", null));
    }
}
