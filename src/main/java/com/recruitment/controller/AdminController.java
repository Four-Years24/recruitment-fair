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
import com.recruitment.config.LoginRateLimiter;
import com.recruitment.util.JwtUtil;
import com.recruitment.vo.JobFairListVO;
import com.recruitment.vo.RegistrationDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
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
// [编写顺序 6.1] 管理端 Controller
// ============================================================
@Tag(name = "管理端", description = "管理员操作，除登录外需在 Header 带 Authorization: Bearer <token>")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private LoginRateLimiter rateLimiter;


    // ==================== 登录 ====================
    @Operation(summary = "管理员登录", description = "返回 JWT token，24小时有效。同一IP连续5次失败锁定15分钟")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody AdminLoginDTO dto,
                                              HttpServletRequest request) {
        String ip = request.getRemoteAddr();

        // 检查是否被锁定
        if (rateLimiter.isLocked(ip)) {
            long seconds = rateLimiter.getRemainingLockSeconds(ip);
            return Result.fail(429, "登录尝试过于频繁，请" + seconds + "秒后再试");
        }

        try {
            String username = adminService.login(dto);
            String token = jwtUtil.generateToken(username);
            rateLimiter.clear(ip);  // 成功后清除失败记录

            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("token", token);
            return Result.ok(data);
        } catch (Exception e) {
            rateLimiter.recordFailure(ip);  // 记录失败
            throw e;
        }
    }


    // ==================== 招聘会管理 ====================
    @Operation(summary = "创建招聘会")
    @PostMapping("/job-fair")
    public Result<?> createJobFair(@Valid @RequestBody JobFairCreateDTO dto) {
        jobFairService.create(dto);
        return Result.ok();
    }

    @Operation(summary = "编辑招聘会")
    @PutMapping("/job-fair/{id}")
    public Result<?> updateJobFair(@PathVariable Long id,
                                   @Valid @RequestBody JobFairCreateDTO dto) {
        jobFairService.update(id, dto);
        return Result.ok();
    }

    @Operation(summary = "查看所有招聘会（含草稿）")
    @GetMapping("/job-fair")
    public Result<List<JobFairListVO>> listAllJobFairs() {
        return Result.ok(jobFairService.listAll());
    }

    @Operation(summary = "修改招聘会状态", description = "status: 0草稿 1已发布 2已结束")
    @PutMapping("/job-fair/{id}/status")
    public Result<?> updateJobFairStatus(@PathVariable Long id,
                                         @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null || status < 0 || status > 2) {
            return Result.fail("状态值不合法（0/1/2）");
        }
        jobFairService.updateStatus(id, status);
        return Result.ok();
    }


    // ==================== 报名管理 ====================
    @Operation(summary = "报名分页查询", description = "可按企业名、招聘会ID、审核状态筛选")
    @GetMapping("/registration/page")
    public Result<PageResult<RegistrationDetailVO>> pageRegistrations(RegistrationPageDTO dto) {
        return Result.ok(registrationService.page(dto));
    }

    @Operation(summary = "查看报名详情（含岗位）")
    @GetMapping("/registration/{id}")
    public Result<RegistrationDetailVO> getRegistrationDetail(@PathVariable Long id) {
        return Result.ok(registrationService.getDetail(id));
    }

    @Operation(summary = "审核报名", description = "status: 1通过 2驳回，驳回时需填 rejectReason")
    @PutMapping("/registration/{id}/audit")
    public Result<?> auditRegistration(@PathVariable Long id,
                                       @Valid @RequestBody AuditDTO dto) {
        registrationService.audit(id, dto);
        return Result.ok();
    }

    @Operation(summary = "导出报名数据为 Excel")
    @GetMapping("/registration/export")
    public void exportRegistrations(RegistrationPageDTO dto, HttpServletResponse response) {
        registrationService.export(dto, response);
    }
}
