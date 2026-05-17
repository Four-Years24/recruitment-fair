package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.JobFairCreateDTO;
import com.recruitment.entity.JobFair;
import com.recruitment.mapper.JobFairMapper;
import com.recruitment.vo.JobFairListVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobFairServiceImpl 单元测试")
class JobFairServiceImplTest {

    @Mock
    private JobFairMapper jobFairMapper;

    @InjectMocks
    private JobFairServiceImpl jobFairService;

    private JobFairCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        createDTO = new JobFairCreateDTO();
        createDTO.setTitle("春季招聘会");
        createDTO.setStartTime(LocalDateTime.of(2026, 6, 1, 9, 0));
        createDTO.setEndTime(LocalDateTime.of(2026, 6, 1, 17, 0));
        createDTO.setLocation("体育馆");
        createDTO.setDescription("面向2026届");
    }

    @Test
    @DisplayName("创建招聘会：结束时间早于开始时间抛异常")
    void shouldRejectInvalidTimeRange() {
        createDTO.setEndTime(LocalDateTime.of(2026, 5, 1, 9, 0)); // 早于开始时间

        BusinessException ex = assertThrows(BusinessException.class,
                () -> jobFairService.create(createDTO));

        assertTrue(ex.getMessage().contains("结束时间不能早于开始时间"));
        verify(jobFairMapper, never()).insert(any());
    }

    @Test
    @DisplayName("更新不存在的招聘会：抛异常")
    void shouldThrowWhenUpdateNotFound() {
        when(jobFairMapper.findById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> jobFairService.update(999L, createDTO));

        assertEquals("招聘会不存在", ex.getMessage());
    }

    @Test
    @DisplayName("查询不存在的招聘会：抛异常")
    void shouldThrowWhenGetByIdNotFound() {
        when(jobFairMapper.findById(999L)).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> jobFairService.getById(999L));
    }

    @Test
    @DisplayName("listPublished 只返回已发布的招聘会")
    void shouldOnlyReturnPublished() {
        when(jobFairMapper.findPublished()).thenReturn(Arrays.asList());

        List<JobFairListVO> result = jobFairService.listPublished();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("创建招聘会：默认状态是已发布(1)")
    void shouldSetStatusToPublishedOnCreate() {
        jobFairService.create(createDTO);

        ArgumentCaptor<JobFair> captor = ArgumentCaptor.forClass(JobFair.class);
        verify(jobFairMapper).insert(captor.capture());
        assertEquals(1, captor.getValue().getStatus());
    }

    @Test
    @DisplayName("编辑招聘会：结束时间早于开始时间抛异常")
    void shouldRejectUpdateWithInvalidTimeRange() {
        JobFair existing = new JobFair();
        existing.setId(1L);
        existing.setStatus(1);
        when(jobFairMapper.findById(1L)).thenReturn(existing);

        createDTO.setEndTime(LocalDateTime.of(2026, 5, 1, 9, 0)); // 早于开始时间

        BusinessException ex = assertThrows(BusinessException.class,
                () -> jobFairService.update(1L, createDTO));

        assertTrue(ex.getMessage().contains("结束时间不能早于开始时间"));
        verify(jobFairMapper, never()).update(any());
    }
}
