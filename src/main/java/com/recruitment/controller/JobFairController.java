package com.recruitment.controller;

import com.recruitment.common.Result;
import com.recruitment.service.JobFairService;
import com.recruitment.service.RegistrationService;
import com.recruitment.vo.JobFairListVO;
import com.recruitment.vo.RegistrationDetailVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 招聘会 API — 学生端浏览，无需登录
 */
@RestController
@RequestMapping("/api/job-fair")
public class JobFairController {

    @Resource
    private JobFairService jobFairService;

    @Resource
    private RegistrationService registrationService;

    /** 学生端：查看所有已发布的招聘会 */
    @GetMapping("/list")
    public Result<List<JobFairListVO>> list() {
        return Result.ok(jobFairService.listPublished());
    }

    /** 学生端：查看某场招聘会的详情 + 参会企业 */
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
