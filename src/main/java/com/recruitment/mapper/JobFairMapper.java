package com.recruitment.mapper;

import com.recruitment.entity.JobFair;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobFairMapper {

    int insert(JobFair jobFair);

    JobFair findById(Long id);

    JobFair findByTitle(String title);

    List<JobFair> findPublished();

    List<JobFair> findAll();

    int update(JobFair jobFair);
}
