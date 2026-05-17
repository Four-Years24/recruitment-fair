package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.common.PageResult;
import com.recruitment.dto.AuditDTO;
import com.recruitment.dto.RegistrationPageDTO;
import com.recruitment.entity.Registration;
import com.recruitment.mapper.CompanyMapper;
import com.recruitment.mapper.JobFairMapper;
import com.recruitment.mapper.PositionMapper;
import com.recruitment.mapper.RegistrationMapper;
import com.recruitment.vo.RegistrationDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrationServiceImpl 单元测试")
class RegistrationServiceImplTest {

    @Mock
    private RegistrationMapper registrationMapper;
    @Mock
    private PositionMapper positionMapper;
    @Mock
    private CompanyMapper companyMapper;
    @Mock
    private JobFairMapper jobFairMapper;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    // ==================== 审核 ====================
    @Nested
    @DisplayName("审核功能")
    class AuditTests {

        private AuditDTO auditDTO;

        @BeforeEach
        void setUp() {
            auditDTO = new AuditDTO();
        }

        @Test
        @DisplayName("审核通过：status=1 更新成功")
        void shouldApprove() {
            auditDTO.setStatus(1);
            Registration reg = new Registration();
            reg.setId(1L);
            reg.setStatus(0);

            when(registrationMapper.findById(1L)).thenReturn(reg);

            registrationService.audit(1L, auditDTO);

            verify(registrationMapper).updateStatus(1L, 1, null);
        }

        @Test
        @DisplayName("审核驳回：status=2 必须填原因")
        void shouldRequireReasonWhenReject() {
            auditDTO.setStatus(2);
            auditDTO.setRejectReason("资料不全");
            Registration reg = new Registration();
            reg.setId(1L);

            when(registrationMapper.findById(1L)).thenReturn(reg);

            registrationService.audit(1L, auditDTO);

            verify(registrationMapper).updateStatus(1L, 2, "资料不全");
        }

        @Test
        @DisplayName("审核驳回不填原因：抛异常")
        void shouldThrowWhenRejectWithoutReason() {
            auditDTO.setStatus(2);
            // 不设 rejectReason

            Registration reg = new Registration();
            reg.setId(1L);
            when(registrationMapper.findById(1L)).thenReturn(reg);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> registrationService.audit(1L, auditDTO));

            assertTrue(ex.getMessage().contains("驳回"));
            // 没填原因，不应该执行 update
            verify(registrationMapper, never()).updateStatus(anyLong(), anyInt(), anyString());
        }

        @Test
        @DisplayName("审核不存在的记录：抛异常")
        void shouldThrowWhenRegistrationNotFound() {
            auditDTO.setStatus(1);
            when(registrationMapper.findById(999L)).thenReturn(null);

            assertThrows(BusinessException.class,
                    () -> registrationService.audit(999L, auditDTO));
        }
    }

    // ==================== 分页 ====================
    @Nested
    @DisplayName("分页查询")
    class PageTests {

        @Test
        @DisplayName("分页查返回 PageResult")
        void shouldReturnPageResult() {
            RegistrationPageDTO dto = new RegistrationPageDTO();
            dto.setPageNum(1);
            dto.setPageSize(10);

            List<RegistrationDetailVO> list = Arrays.asList(new RegistrationDetailVO());
            when(registrationMapper.count(dto)).thenReturn(1L);
            when(registrationMapper.page(dto)).thenReturn(list);

            PageResult<RegistrationDetailVO> result = registrationService.page(dto);

            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getTotalPages());
            assertEquals(1, result.getList().size());
        }

        @Test
        @DisplayName("分页查无结果：total=0, 列表为空")
        void shouldReturnEmptyPageWhenNoData() {
            RegistrationPageDTO dto = new RegistrationPageDTO();

            when(registrationMapper.count(dto)).thenReturn(0L);
            when(registrationMapper.page(dto)).thenReturn(Arrays.asList());

            PageResult<RegistrationDetailVO> result = registrationService.page(dto);

            assertEquals(0L, result.getTotal());
            assertEquals(0, result.getTotalPages());
            assertTrue(result.getList().isEmpty());
        }
    }

    // ==================== 详情 ====================
    @Nested
    @DisplayName("查看详情")
    class DetailTests {

        @Test
        @DisplayName("查看不存在的报名详情：抛异常")
        void shouldThrowWhenDetailNotFound() {
            when(registrationMapper.findById(999L)).thenReturn(null);

            assertThrows(BusinessException.class,
                    () -> registrationService.getDetail(999L));
        }
    }

    // ==================== 按招聘会查询 ====================
    @Nested
    @DisplayName("按招聘会查报名")
    class FindByJobFairTests {

        @Test
        @DisplayName("招聘会无报名记录：返回空列表")
        void shouldReturnEmptyListWhenNoRegistration() {
            when(registrationMapper.findByJobFairId(99L)).thenReturn(Arrays.asList());

            List<RegistrationDetailVO> result = registrationService.getByJobFairId(99L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
