package com.recruitment.mapper;

import com.recruitment.dto.RegistrationPageDTO;
import com.recruitment.entity.Registration;
import com.recruitment.vo.RegistrationDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegistrationMapper {

    int insert(Registration registration);

    Registration findById(Long id);

    Registration findByCompanyAndFair(@Param("companyId") Long companyId,
                                       @Param("jobFairId") Long jobFairId);

    List<RegistrationDetailVO> page(RegistrationPageDTO dto);

    long count(RegistrationPageDTO dto);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("rejectReason") String rejectReason);

    List<RegistrationDetailVO> findByJobFairId(Long jobFairId);
}
