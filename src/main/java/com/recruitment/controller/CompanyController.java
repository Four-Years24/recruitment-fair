package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.dto.CompanyRegisterDTO;
import com.recruitment.service.CompanyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * 企业端 API — 无需登录
 */
@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody CompanyRegisterDTO dto) {
        Long registrationId = companyService.register(dto);
        return Result.ok(registrationId);
    }
}
