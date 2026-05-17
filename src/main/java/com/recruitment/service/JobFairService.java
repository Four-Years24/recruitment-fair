package com.recruitment.service;

import com.recruitment.dto.JobFairCreateDTO;
import com.recruitment.vo.JobFairListVO;

import java.util.List;

/**
 * 招聘会服务
 */
public interface JobFairService {

    void create(JobFairCreateDTO dto);

    void update(Long id, JobFairCreateDTO dto);

    /** 修改招聘会状态（发布/结束/回退草稿） */
    void updateStatus(Long id, Integer status);

    /** 学生端：查看所有已发布的招聘会 */
    List<JobFairListVO> listPublished();

    /** 管理端：查看所有招聘会（含草稿） */
    List<JobFairListVO> listAll();

    JobFairListVO getById(Long id);
}
