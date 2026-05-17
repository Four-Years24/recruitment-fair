package com.recruitment.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// ============================================================
// [编写顺序 JWT-1] JWT 工具类 — 生成和验证 token
// [思维] JWT 由三段组成，用点号分隔：header.payload.signature
//        header:  算法类型 (HS256)
//        payload: 存放数据（用户名、过期时间）
//        signature: 用密钥对前两段签名，防止篡改
//        任何人改了 payload → 签名不匹配 → token 无效
// [思维] 为什么抽成工具类而不是写在 Service 里？
//        拦截器也需要验证 token，两个地方共用同一套逻辑
// ============================================================
@Component
public class JwtUtil {

    // [顺序 JWT-1.1] 从配置文件读取密钥和过期时间
    //              @Value 是 Spring 的注入，${} 从 application.yml 取值
    //              密钥至少 256 位（HS256 算法要求）
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // [顺序 JWT-1.2] 把字符串密钥转成 Java 密钥对象
    //              只转一次，存起来复用
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // [顺序 JWT-1.3] 生成 JWT
    //             登录成功时调用，把用户名写进 token
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)                     // 存用户名
                .issuedAt(now)                          // 签发时间
                .expiration(expiry)                     // 过期时间
                .signWith(getKey())                     // 用密钥签名
                .compact();                             // 生成最终字符串
    }

    // [顺序 JWT-1.4] 从 token 里提取用户名
    //             拦截器用这个方法拿用户名，判断是谁在请求
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    // [顺序 JWT-1.5] 验证 token 是否有效
    //             返回 true = 合法，false = 过期/伪造/格式不对
    // [思维] 为什么单独列出每个异常类型？
    //        方便调试——知道是哪种情况导致验证失败
    //        生产环境可以统一 catch(Exception)，不暴露细节
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // token 过期
            return false;
        } catch (SignatureException e) {
            // 签名不匹配（被篡改或密钥不同）
            return false;
        } catch (MalformedJwtException e) {
            // token 格式不对
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // [顺序 JWT-1.6] 内部方法：解析 token
    //             不管验证结果，只管解析
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

// [状态] JwtUtil 完成
// [下一步] 创建拦截器 AuthInterceptor
//         → 拦截所有 /api/admin/** 请求
//         → 从 Header 中取 token → 验证 → 放行或拒绝
