package com.recruitment.mapper;

import com.recruitment.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper {

    int insert(Company company);

    Company findById(Long id);

    Company findByName(String name);

    int update(Company company);
}
