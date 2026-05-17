package com.recruitment.controller;

import com.recruitment.common.BusinessException;
import com.recruitment.config.LoginRateLimiter;
import com.recruitment.dto.AdminLoginDTO;
import com.recruitment.dto.AuditDTO;
import com.recruitment.dto.JobFairCreateDTO;
import com.recruitment.dto.RegistrationPageDTO;
import com.recruitment.service.AdminService;
import com.recruitment.service.JobFairService;
import com.recruitment.service.RegistrationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @Resource
    private AdminService adminService;

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

    @Resource
    private LoginRateLimiter rateLimiter;

    // ==================== 登录 ====================
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String doLogin(AdminLoginDTO dto, HttpServletRequest request,
                          RedirectAttributes redirect) {
        String ip = request.getRemoteAddr();
        if (rateLimiter.isLocked(ip)) {
            redirect.addFlashAttribute("error", "登录过于频繁，请15分钟后再试");
            return "redirect:/admin/login";
        }
        try {
            String username = adminService.login(dto);
            rateLimiter.clear(ip);
            request.getSession().setAttribute("adminUser", username);
            return "redirect:/admin/dashboard";
        } catch (BusinessException e) {
            rateLimiter.recordFailure(ip);
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ==================== 工作台 ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        model.addAttribute("fairs", jobFairService.listAll());
        return "admin/dashboard";
    }

    // ==================== 招聘会管理 ====================
    @GetMapping("/job-fairs")
    public String jobFairs(Model model, HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        model.addAttribute("fairs", jobFairService.listAll());
        return "admin/job-fairs";
    }

    @GetMapping("/job-fairs/new")
    public String newJobFairForm(HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        return "admin/job-fair-form";
    }

    @PostMapping("/job-fairs")
    public String createJobFair(JobFairCreateDTO dto, HttpSession session,
                                RedirectAttributes redirect) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        jobFairService.create(dto);
        redirect.addFlashAttribute("success", "招聘会创建成功");
        return "redirect:/admin/job-fairs";
    }

    @GetMapping("/job-fairs/{id}/edit")
    public String editJobFairForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        model.addAttribute("fair", jobFairService.getById(id));
        return "admin/job-fair-edit";
    }

    @PostMapping("/job-fairs/{id}")
    public String updateJobFair(@PathVariable Long id, JobFairCreateDTO dto,
                                HttpSession session, RedirectAttributes redirect) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        jobFairService.update(id, dto);
        redirect.addFlashAttribute("success", "招聘会更新成功");
        return "redirect:/admin/job-fairs";
    }

    @PostMapping("/job-fairs/{id}/status")
    public String updateJobFairStatus(@PathVariable Long id, Integer status,
                                      HttpSession session, RedirectAttributes redirect) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        jobFairService.updateStatus(id, status);
        redirect.addFlashAttribute("success", "状态已更新");
        return "redirect:/admin/job-fairs";
    }

    // ==================== 报名管理 ====================
    @GetMapping("/registrations")
    public String registrations(Model model, HttpSession session,
                                 RegistrationPageDTO dto) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        model.addAttribute("page", registrationService.page(dto));
        model.addAttribute("dto", dto);
        return "admin/registrations";
    }

    @GetMapping("/registrations/{id}")
    public String registrationDetail(@PathVariable Long id, Model model,
                                      HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        model.addAttribute("reg", registrationService.getDetail(id));
        return "admin/registration-detail";
    }

    @PostMapping("/registrations/{id}/audit")
    public String audit(@PathVariable Long id, AuditDTO dto, HttpSession session,
                        RedirectAttributes redirect) {
        if (session.getAttribute("adminUser") == null) return "redirect:/admin/login";
        registrationService.audit(id, dto);
        redirect.addFlashAttribute("success", dto.getStatus() == 1 ? "已通过" : "已驳回");
        return "redirect:/admin/registrations/" + id;
    }
}
