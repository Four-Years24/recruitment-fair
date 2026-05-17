package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.CompanyRegisterDTO;
import com.recruitment.entity.Company;
import com.recruitment.entity.Registration;
import com.recruitment.mapper.CompanyMapper;
import com.recruitment.mapper.PositionMapper;
import com.recruitment.mapper.RegistrationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyServiceImpl 单元测试")
class CompanyServiceImplTest {

    @Mock
    private CompanyMapper companyMapper;
    @Mock
    private RegistrationMapper registrationMapper;
    @Mock
    private PositionMapper positionMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private CompanyRegisterDTO dto;

    @BeforeEach
    void setUp() {
        CompanyRegisterDTO.PositionItem pos = new CompanyRegisterDTO.PositionItem();
        pos.setTitle("Java开发");
        pos.setHeadcount(3);
        pos.setEducation("本科");
        pos.setSalaryRange("10K-20K");
        pos.setWorkCity("深圳");

        dto = new CompanyRegisterDTO();
        dto.setCompanyName("字节跳动");
        dto.setIndustry("互联网");
        dto.setScale("1000+");
        dto.setNature("私企");
        dto.setAddress("深圳南山区");
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");
        dto.setContactEmail("hr@bytedance.com");
        dto.setJobFairId(1L);
        dto.setPositions(Arrays.asList(pos));
    }

    // ==================== 正常场景 ====================

    @Test
    @DisplayName("新企业报名成功：创建企业+报名+岗位")
    void shouldRegisterNewCompany() {
        // 1. 企业不存在
        when(companyMapper.findByName("字节跳动")).thenReturn(null);
        // 2. 没有重复报名
        when(registrationMapper.findByCompanyAndFair(any(), eq(1L))).thenReturn(null);
        // 3. 模拟 MyBatis useGeneratedKeys：insert 后自动回填 ID
        doAnswer(inv -> {
            Registration r = inv.getArgument(0);
            r.setId(100L);
            return 1;
        }).when(registrationMapper).insert(any(Registration.class));

        Long regId = companyService.register(dto);

        assertEquals(100L, regId);
        verify(companyMapper).insert(any(Company.class));
        verify(registrationMapper).insert(any(Registration.class));
        verify(positionMapper).batchInsert(anyList());
    }

    @Test
    @DisplayName("已存在企业重复报名同一场：抛异常")
    void shouldRejectDuplicateRegistration() {
        Company existing = new Company();
        existing.setId(5L);
        existing.setName("字节跳动");

        Registration dupReg = new Registration();
        dupReg.setId(99L);

        when(companyMapper.findByName("字节跳动")).thenReturn(existing);
        when(registrationMapper.findByCompanyAndFair(5L, 1L)).thenReturn(dupReg);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> companyService.register(dto));

        assertTrue(ex.getMessage().contains("已报名"));
        // 重复报名时不应该插入新企业和新报名
        verify(companyMapper, never()).insert(any());
        verify(registrationMapper, never()).insert(any());
    }

    @Test
    @DisplayName("插入报名记录时状态必须是待审核(0)")
    void shouldSetStatusToPending() {
        when(companyMapper.findByName("字节跳动")).thenReturn(null);
        when(registrationMapper.findByCompanyAndFair(any(), eq(1L))).thenReturn(null);

        companyService.register(dto);

        // 捕获 insert 时传入的 Registration 对象
        ArgumentCaptor<Registration> captor = ArgumentCaptor.forClass(Registration.class);
        verify(registrationMapper).insert(captor.capture());

        assertEquals(0, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("已存在企业报名不同招聘会：成功创建新报名")
    void shouldAllowSameCompanyDifferentFair() {
        Company existing = new Company();
        existing.setId(5L);
        existing.setName("字节跳动");

        dto.setJobFairId(2L); // 换一场招聘会

        when(companyMapper.findByName("字节跳动")).thenReturn(existing);
        when(registrationMapper.findByCompanyAndFair(5L, 2L)).thenReturn(null);
        doAnswer(inv -> {
            Registration r = inv.getArgument(0);
            r.setId(200L);
            return 1;
        }).when(registrationMapper).insert(any(Registration.class));

        Long regId = companyService.register(dto);

        assertEquals(200L, regId);
        // 企业已存在，不应该再 insert 企业
        verify(companyMapper, never()).insert(any());
        verify(registrationMapper).insert(any(Registration.class));
        verify(positionMapper).batchInsert(anyList());
    }

    @Test
    @DisplayName("报名带多个岗位：batchInsert 只调用一次")
    void shouldBatchInsertMultiplePositions() {
        CompanyRegisterDTO.PositionItem pos1 = new CompanyRegisterDTO.PositionItem();
        pos1.setTitle("Java开发");
        CompanyRegisterDTO.PositionItem pos2 = new CompanyRegisterDTO.PositionItem();
        pos2.setTitle("前端开发");
        dto.setPositions(Arrays.asList(pos1, pos2));

        when(companyMapper.findByName("字节跳动")).thenReturn(null);
        when(registrationMapper.findByCompanyAndFair(any(), eq(1L))).thenReturn(null);

        companyService.register(dto);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(positionMapper).batchInsert(captor.capture());
        assertEquals(2, captor.getValue().size());
    }
}
