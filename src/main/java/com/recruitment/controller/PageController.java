package com.recruitment.controller;

import com.recruitment.service.JobFairService;
import com.recruitment.service.RegistrationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

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
}
