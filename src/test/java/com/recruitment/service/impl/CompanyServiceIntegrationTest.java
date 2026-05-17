package com.recruitment.service.impl;

import com.recruitment.dto.CompanyRegisterDTO;
import com.recruitment.entity.JobFair;
import com.recruitment.mapper.JobFairMapper;
import com.recruitment.mapper.PositionMapper;
import com.recruitment.vo.RegistrationDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("CompanyService 集成测试")
class CompanyServiceIntegrationTest {

    @Autowired
    private CompanyServiceImpl companyService;

    @Autowired
    private JobFairMapper jobFairMapper;

    @Autowired
    private PositionMapper positionMapper;

    private CompanyRegisterDTO dto;

    @BeforeEach
    void setUp() {
        // 先创建一场招聘会（报名需要有 jobFairId）
        JobFair fair = new JobFair();
        fair.setTitle("测试招聘会");
        fair.setStartTime(LocalDateTime.of(2026, 6, 1, 9, 0));
        fair.setEndTime(LocalDateTime.of(2026, 6, 1, 17, 0));
        fair.setLocation("体育馆");
        fair.setStatus(1);
        jobFairMapper.insert(fair);

        CompanyRegisterDTO.PositionItem pos = new CompanyRegisterDTO.PositionItem();
        pos.setTitle("Java开发");
        pos.setHeadcount(3);
        pos.setEducation("本科");
        pos.setSalaryRange("15K-25K");
        pos.setWorkCity("深圳");

        dto = new CompanyRegisterDTO();
        dto.setCompanyName("测试企业");
        dto.setIndustry("互联网");
        dto.setScale("100-499");
        dto.setContactName("李四");
        dto.setContactPhone("13900001111");
        dto.setJobFairId(fair.getId());
        dto.setPositions(Arrays.asList(pos));
    }

    @Test
    @DisplayName("集成测试：完整报名流程 — 企业+报名+岗位 全部入库")
    void shouldCompleteRegistrationFlow() {
        Long regId = companyService.register(dto);

        assertNotNull(regId);
        // 验证岗位真的入库了
        assertEquals(1, positionMapper.findByRegistrationId(regId).size());
    }

    @Test
    @DisplayName("集成测试：同一企业同一招聘会重复报名抛异常")
    void shouldRejectDuplicate() {
        companyService.register(dto);

        assertThrows(Exception.class, () -> companyService.register(dto));
    }
}
