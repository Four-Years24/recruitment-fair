package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.service.JobFairService;
import com.recruitment.service.RegistrationService;
import com.recruitment.vo.JobFairListVO;
import com.recruitment.vo.RegistrationDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "学生端", description = "学生公开浏览，无需登录")
@RestController
@RequestMapping("/api/job-fair")
public class JobFairController {

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

    @Operation(summary = "浏览已发布的招聘会列表")
    @GetMapping("/list")
    public Result<List<JobFairListVO>> list() {
        return Result.ok(jobFairService.listPublished());
    }

    @Operation(summary = "查看招聘会详情 + 参会企业及岗位")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        JobFairListVO fair = jobFairService.getById(id);
        List<RegistrationDetailVO> companies = registrationService.getByJobFairId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("jobFair", fair);
        data.put("companies", companies);
        return Result.ok(data);
    }
}
