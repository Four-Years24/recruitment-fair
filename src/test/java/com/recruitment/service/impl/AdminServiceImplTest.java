package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.AdminLoginDTO;
import com.recruitment.entity.Admin;
import com.recruitment.mapper.AdminMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl 单元测试")
class AdminServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    // ---------- 测试数据 ----------
    private AdminLoginDTO loginDTO;
    private Admin dbAdmin;

    @BeforeEach
    void setUp() {
        loginDTO = new AdminLoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        dbAdmin = new Admin();
        dbAdmin.setId(1L);
        dbAdmin.setUsername("admin");
        dbAdmin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        dbAdmin.setRealName("管理员");
    }

    // ==================== 正常场景 ====================

    @Test
    @DisplayName("登录成功：正确用户名+密码返回用户名")
    void shouldLoginSuccessfully() {
        when(adminMapper.findByUsername("admin")).thenReturn(dbAdmin);

        String username = adminService.login(loginDTO);

        assertEquals("admin", username);
        verify(adminMapper).findByUsername("admin");
    }

    // ==================== 异常场景 ====================

    @Test
    @DisplayName("登录失败：用户不存在抛异常")
    void shouldThrowWhenUserNotFound() {
        when(adminMapper.findByUsername("admin")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> adminService.login(loginDTO));

        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    @DisplayName("登录失败：密码错误抛异常")
    void shouldThrowWhenPasswordWrong() {
        loginDTO.setPassword("wrong_pwd");
        when(adminMapper.findByUsername("admin")).thenReturn(dbAdmin);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> adminService.login(loginDTO));

        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    @DisplayName("登录失败：用户名空串，Mapper返回null，抛异常")
    void shouldThrowWhenUsernameEmpty() {
        loginDTO.setUsername("");
        when(adminMapper.findByUsername("")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> adminService.login(loginDTO));

        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    @DisplayName("边界：数据库中存储的密码哈希每次不同，同一份密码依然能验证通过")
    void bcryptSamePasswordDifferentHashShouldStillPass() {
        // 用另一份 salt 生成不同的哈希
        String differentHash = BCrypt.hashpw("admin123", BCrypt.gensalt());
        dbAdmin.setPassword(differentHash);

        when(adminMapper.findByUsername("admin")).thenReturn(dbAdmin);

        String username = adminService.login(loginDTO);
        assertEquals("admin", username);
    }
}
