package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.AdminLoginDTO;
import com.recruitment.entity.Admin;
import com.recruitment.mapper.AdminMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// ============================================================
// [编写顺序 IT-1] 集成测试 — 真连 H2 数据库
// [和单元测试的区别]
//   单元测试: @ExtendWith(MockitoExtension.class) → Mapper 是假的
//   集成测试: @SpringBootTest → 启动完整 Spring 容器 → Mapper 连 H2 真库
// [思维] 集成测试验证的是"配置是否正确"
//        MyBatis XML 路径、resultMap 映射、SQL 语法、表是否建对
// ============================================================
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 每个测试完自动回滚，测试数据不残留
@Sql(scripts = "/schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("AdminService 集成测试")
class AdminServiceIntegrationTest {

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private AdminMapper adminMapper;

    @BeforeEach
    void setUp() {
        // 插入测试管理员（BCrypt 加密）
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        admin.setRealName("管理员");
        adminMapper.insert(admin);
    }

    @Test
    @DisplayName("集成测试：正确用户名密码登录成功")
    void shouldLoginSuccessfully() {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        String result = adminService.login(dto);

        assertEquals("admin", result);
    }

    @Test
    @DisplayName("集成测试：密码错误抛异常")
    void shouldThrowWhenPasswordWrong() {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrong");

        assertThrows(BusinessException.class, () -> adminService.login(dto));
    }

    @Test
    @DisplayName("集成测试：用户不存在抛异常")
    void shouldThrowWhenUserNotFound() {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("nobody");
        dto.setPassword("admin123");

        assertThrows(BusinessException.class, () -> adminService.login(dto));
    }
}
