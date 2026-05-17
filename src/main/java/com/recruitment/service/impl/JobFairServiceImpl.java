package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.JobFairCreateDTO;
import com.recruitment.entity.JobFair;
import com.recruitment.mapper.JobFairMapper;
import com.recruitment.service.JobFairService;
import com.recruitment.vo.JobFairListVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobFairServiceImpl implements JobFairService {

    @Resource
    private JobFairMapper jobFairMapper;

    @Override
    public void create(JobFairCreateDTO dto) {
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }

        JobFair jobFair = new JobFair();
        jobFair.setTitle(dto.getTitle());
        jobFair.setStartTime(dto.getStartTime());
        jobFair.setEndTime(dto.getEndTime());
        jobFair.setLocation(dto.getLocation());
        jobFair.setDescription(dto.getDescription());
        jobFair.setStatus(1); // 默认直接发布
        jobFairMapper.insert(jobFair);
    }

    @Override
    public void update(Long id, JobFairCreateDTO dto) {
        JobFair jobFair = jobFairMapper.findById(id);
        if (jobFair == null) {
            throw new BusinessException("招聘会不存在");
        }
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }

        jobFair.setTitle(dto.getTitle());
        jobFair.setStartTime(dto.getStartTime());
        jobFair.setEndTime(dto.getEndTime());
        jobFair.setLocation(dto.getLocation());
        jobFair.setDescription(dto.getDescription());
        jobFairMapper.update(jobFair);
    }

    @Override
    public List<JobFairListVO> listPublished() {
        List<JobFair> list = jobFairMapper.findPublished();
        return toVOList(list);
    }

    @Override
    public List<JobFairListVO> listAll() {
        List<JobFair> list = jobFairMapper.findAll();
        return toVOList(list);
    }

    @Override
    public JobFairListVO getById(Long id) {
        JobFair jobFair = jobFairMapper.findById(id);
        if (jobFair == null) {
            throw new BusinessException("招聘会不存在");
        }
        return toVO(jobFair);
    }

    // ---------- 转换方法 ----------
    private JobFairListVO toVO(JobFair entity) {
        JobFairListVO vo = new JobFairListVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setLocation(entity.getLocation());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        return vo;
    }

    private List<JobFairListVO> toVOList(List<JobFair> entities) {
        List<JobFairListVO> vos = new ArrayList<>();
        for (JobFair e : entities) {
            vos.add(toVO(e));
        }
        return vos;
    }
}
