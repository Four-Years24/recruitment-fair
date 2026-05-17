package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.dto.CompanyRegisterDTO;
import com.recruitment.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "企业端", description = "企业公开接口，无需登录")
@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Resource
    private CompanyService companyService;

    @Operation(summary = "企业报名", description = "提交企业信息+岗位列表，自动判断企业是否已存在")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody CompanyRegisterDTO dto) {
        Long registrationId = companyService.register(dto);
        return Result.ok(registrationId);
    }
}
