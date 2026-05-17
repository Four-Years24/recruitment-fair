package com.recruitment.config;

import com.recruitment.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// ============================================================
// [编写顺序 JWT-2] 认证拦截器
// [前置] JwtUtil 已完成 → 现在用拦截器保护管理端接口
// [思维] 拦截器是 Spring MVC 的"安检门"
//        每个 HTTP 请求到达 Controller 之前，先过这道门
//        门里检查 token，合法的放行，非法的直接踢回去
// [思维] 为什么用拦截器而不是 Spring Security？
//        Spring Security 功能强大但配置复杂
//        对于 MVP 项目，手写拦截器更轻量、更透明
//        等你理解了拦截器的原理，再学 Spring Security 会容易很多
// ============================================================
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    // [顺序 JWT-2.1] preHandle = 在 Controller 执行前运行
    //             返回 true  → 放行，请求继续往后走
    //             返回 false → 拦截，请求到此为止
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // [顺序 JWT-2.2] 第一步：从 HTTP Header 里取 token
        //             前端发请求时在 Header 里带：
        //             Authorization: Bearer eyJhbGciOi...
        //             "Bearer " 是约定前缀，后面跟真实 token
        String authHeader = request.getHeader("Authorization");

        // [顺序 JWT-2.3] 第二步：没带 Authorization 头 → 拒绝
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        // [顺序 JWT-2.4] 第三步：抽出 token（去掉 "Bearer " 前缀）
        String token = authHeader.substring(7);

        // [顺序 JWT-2.5] 第四步：验证 token
        //             不合法（过期/伪造/篡改）→ 返回 401
        if (!jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期，请重新登录\"}");
            return false;
        }

        // [顺序 JWT-2.6] 第五步：token 合法 → 放行
        //             后续 Controller 可以通过 request.getAttribute("username")
        //             获取当前登录的用户名
        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("username", username);
        return true;
    }
}

// [状态] AuthInterceptor 完成
// [下一步] 在 WebConfig 里注册这个拦截器
//         → 配置哪些路径需要拦截（/api/admin/**）
//         → 配置哪些路径放行（/api/admin/login）
//         → 然后修改 AdminController 的 login 方法返回真 token
