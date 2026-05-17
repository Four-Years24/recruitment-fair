package com.recruitment.controller;

import com.recruitment.dto.CompanyRegisterDTO;
import com.recruitment.service.CompanyService;
import com.recruitment.service.JobFairService;
import com.recruitment.service.RegistrationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

    @Resource
    private CompanyService companyService;

    // ==================== 学生端 ====================

    @GetMapping("/fairs")
    public String index(Model model) {
        model.addAttribute("fairs", jobFairService.listPublished());
        model.addAttribute("currentPage", "fairs");
        return "index";
    }

    @GetMapping("/fairs/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("fair", jobFairService.getById(id));
        model.addAttribute("companies", registrationService.getByJobFairId(id));
        model.addAttribute("currentPage", "fairs");
        return "fair-detail";
    }

    // ==================== 企业端 ====================

    @GetMapping("/company/register")
    public String companyHome(Model model) {
        model.addAttribute("fairs", jobFairService.listPublished());
        model.addAttribute("currentPage", "register");
        return "company-home";
    }

    @GetMapping("/company/register/{fairId}")
    public String registerForm(@PathVariable Long fairId, Model model) {
        model.addAttribute("fair", jobFairService.getById(fairId));
        model.addAttribute("currentPage", "register");
        return "company-register";
    }

    @PostMapping("/company/register/{fairId}")
    public String doRegister(@PathVariable Long fairId, CompanyRegisterDTO dto,
                             RedirectAttributes redirect) {
        dto.setJobFairId(fairId);
        if (dto.getPositions() != null) {
            dto.getPositions().removeIf(p ->
                p.getTitle() == null || p.getTitle().isBlank());
        }
        companyService.register(dto);
        redirect.addFlashAttribute("success", "报名成功，请等待管理员审核");
        return "redirect:/company/register/" + fairId;
    }
}
