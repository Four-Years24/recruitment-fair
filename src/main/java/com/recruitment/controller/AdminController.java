package com.recruitment.controller;

import com.recruitment.common.PageResult;
import com.recruitment.common.Result;
import com.recruitment.dto.AdminLoginDTO;
import com.recruitment.dto.AuditDTO;
import com.recruitment.dto.JobFairCreateDTO;
import com.recruitment.dto.RegistrationPageDTO;
import com.recruitment.service.AdminService;
import com.recruitment.service.JobFairService;
import com.recruitment.service.RegistrationService;
import com.recruitment.util.JwtUtil;
import com.recruitment.vo.JobFairListVO;
import com.recruitment.vo.RegistrationDetailVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ============================================================
// [编写顺序 6.1] 第一个 Controller — 管理端
// [前置] 所有 Service 和 DTO/VO 已完成
// [思维] Controller 是整个项目最后写的层
//        因为 Controller 只是给 Service 套上 HTTP 路径
//        Service 写好了，Controller 就是"体力活"
// [思维] Controller 的三个职责（记住，只有这三个）：
//        1. 接收 HTTP 请求参数（@RequestBody, @PathVariable, @RequestParam）
//        2. 调用 Service 方法
//        3. 把返回值包装成 Result 返回
//        不要往 Controller 里写业务逻辑！
// ============================================================
@RestController               // ← = @Controller + @ResponseBody（返回 JSON）
@RequestMapping("/api/admin") // ← 这个类所有接口的前缀
public class AdminController {

    // [顺序 6.1.1] 注入需要的 Service
    //             为什么 AdminController 注入了三个 Service？
    //             因为管理端功能多：登录、管招聘会、管报名
    //             每个 Controller 可以注入多个 Service，但要克制
    //             一般 2-3 个，太多说明 Controller 职责不清需要拆分
    @Resource
    private AdminService adminService;

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

    @Resource
    private JwtUtil jwtUtil;


    // ==================== 登录 ====================
    // [顺序 JWT-4] 登录接口 — 返回真 JWT token
    // [改动] 用 JwtUtil 生成 token 替代原来的假 token
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody AdminLoginDTO dto) {
        String username = adminService.login(dto);

        // [顺序 JWT-4.1] 调用 JwtUtil 生成真实 JWT
        //             token 包含用户名 + 过期时间 + 签名
        //             24 小时后自动过期，需要重新登录
        String token = jwtUtil.generateToken(username);

        Map<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("token", token);

        // [顺序 6.1.5] 包装成统一格式返回
        return Result.ok(data);
    }


    // ==================== 招聘会管理 ====================
    // [顺序 6.1.6] 创建招聘会接口
    //             POST 方法，路径是 /api/admin/job-fair（类上 @RequestMapping 已定义前缀）
    //             @Valid 校验 title 不为空、startTime/endTime 不为 null
    //             成功返回空 data（Result.ok() 无参数版本）
    @PostMapping("/job-fair")
    public Result<?> createJobFair(@Valid @RequestBody JobFairCreateDTO dto) {
        jobFairService.create(dto);
        return Result.ok();
    }

    // [顺序 6.1.7] 编辑招聘会接口
    //             PUT 方法：RESTful 规范，修改资源用 PUT
    //             @PathVariable：从 URL 路径中提取 id
    //             路径：/api/admin/job-fair/1 → id=1
    @PutMapping("/job-fair/{id}")
    public Result<?> updateJobFair(@PathVariable Long id,
                                   @Valid @RequestBody JobFairCreateDTO dto) {
        jobFairService.update(id, dto);
        return Result.ok();
    }

    // [顺序 6.1.8] 查看所有招聘会（管理端）
    //             GET 方法，不需要传参
    //             返回 List<JobFairListVO>，包含草稿、已发布、已结束的全部
    @GetMapping("/job-fair")
    public Result<List<JobFairListVO>> listAllJobFairs() {
        return Result.ok(jobFairService.listAll());
    }


    // ==================== 报名管理 ====================
    // [顺序 6.1.9] 报名分页查询
    //             GET 方法，参数用 Query String：/api/admin/registration/page?pageNum=1&pageSize=10
    //             RegistrationPageDTO 的属性自动从 URL 参数绑定
    //             不需要 @RequestParam 逐个声明，Spring 自动把 ?pageNum=1 映射到 dto.pageNum
    @GetMapping("/registration/page")
    public Result<PageResult<RegistrationDetailVO>> pageRegistrations(RegistrationPageDTO dto) {
        return Result.ok(registrationService.page(dto));
    }

    // [顺序 6.1.10] 查看某条报名详情
    //              @PathVariable Long id → /api/admin/registration/1
    @GetMapping("/registration/{id}")
    public Result<RegistrationDetailVO> getRegistrationDetail(@PathVariable Long id) {
        return Result.ok(registrationService.getDetail(id));
    }

    // [顺序 6.1.11] 审核报名
    //              PUT 方法：修改报名状态
    //              AuditDTO 包含 status（1通过/2驳回）和 rejectReason（驳回原因）
    @PutMapping("/registration/{id}/audit")
    public Result<?> auditRegistration(@PathVariable Long id,
                                       @Valid @RequestBody AuditDTO dto) {
        registrationService.audit(id, dto);
        return Result.ok();
    }

    // [顺序 6.1.12] 导出报名数据
    //              返回类型 void：因为数据直接写入 HttpServletResponse 的输出流
    //              Controller 不返回 JSON，而是让浏览器下载文件
    @GetMapping("/registration/export")
    public void exportRegistrations(RegistrationPageDTO dto, HttpServletResponse response) {
        registrationService.export(dto, response);
    }
}

// [状态] AdminController 完成
// [思维] Controller 写完后回顾：
//        每个方法 ≤ 5 行代码，逻辑全在 Service
//        如果 Controller 方法超过 10 行，说明你把业务逻辑写到 Controller 了
//        这是判断代码质量的一个简单标准
// [下一步] 同样的流程完成另外两个 Controller：
//         CompanyController（企业报名接口，1 个方法）
//         JobFairController（学生浏览接口，2 个方法）
//         → 然后写配置类：WebConfig（跨域）+ DataInitializer（初始化）
