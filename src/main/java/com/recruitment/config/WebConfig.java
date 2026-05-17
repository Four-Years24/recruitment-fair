package com.recruitment.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// ============================================================
// [编写顺序 JWT-3] 注册拦截器
// [前置] AuthInterceptor 已完成 → 现在配置哪些路径受保护
// [思维] 路径设计：
//        /api/admin/**      → 需要登录（除了 /api/admin/login）
//        /api/company/**    → 无需登录（企业公开报名）
//        /api/job-fair/**   → 无需登录（学生公开浏览）
// ============================================================
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    // ==================== 跨域（已有） ====================
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // ==================== JWT 拦截器（新增） ====================
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/admin/**")       // 拦截所有管理端请求
                .excludePathPatterns("/api/admin/login"); // 登录接口不拦截
        // [思维] excludePathPatterns 放行登录接口
        //        如果登录也要 token → 死循环：拿不到 token 就不能登录
        //                             不登录就拿不到 token
    }
}
