package com.recruitment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// ============================================================
// [编写顺序 JWT-5] JWT 工具类测试
// [思维] 这个测试不 Mock，直接测真实 JwtUtil 对象
//        因为 JwtUtil 不依赖外部服务（数据库、网络）
//        这种叫"纯单元测试"，跑得最快
// ============================================================
@DisplayName("JwtUtil 单元测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 手动注入配置值（不走 Spring，测试更快）
        setField(jwtUtil, "secret", "test-secret-key-for-jwt-unit-testing-32bytes");
        setField(jwtUtil, "expiration", 3600000L); // 1小时
    }

    @Test
    @DisplayName("生成 token 后能解析出用户名")
    void shouldGenerateAndExtractUsername() {
        String token = jwtUtil.generateToken("admin");

        String username = jwtUtil.getUsernameFromToken(token);

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("有效 token 验证通过")
    void shouldValidateCorrectToken() {
        String token = jwtUtil.generateToken("admin");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("篡改过的 token 验证失败")
    void shouldRejectTamperedToken() {
        String token = jwtUtil.generateToken("admin");
        // 在中间位置插入一个字符，破坏签名
        int mid = token.length() / 2;
        String tampered = token.substring(0, mid) + "X" + token.substring(mid);

        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    @DisplayName("空字符串和 null 验证失败")
    void shouldRejectInvalidInput() {
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("不同用户名生成不同的 token")
    void differentUsersShouldHaveDifferentTokens() {
        String token1 = jwtUtil.generateToken("admin");
        String token2 = jwtUtil.generateToken("superadmin");

        assertNotEquals(token1, token2);
    }

    // 用反射设私有字段，测试不用 Spring 容器
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
