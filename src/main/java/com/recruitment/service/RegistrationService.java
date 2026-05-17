package com.recruitment.service;

import com.recruitment.common.PageResult;
import com.recruitment.dto.AuditDTO;
import com.recruitment.dto.RegistrationPageDTO;
import com.recruitment.vo.RegistrationDetailVO;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 报名记录服务
 */
public interface RegistrationService {

    /** 分页查询报名记录（管理端） */
    PageResult<RegistrationDetailVO> page(RegistrationPageDTO dto);

    /** 查看报名详情（含岗位列表） */
    RegistrationDetailVO getDetail(Long id);

    /** 审核通过/驳回 */
    void audit(Long id, AuditDTO dto);

    /** 学生端：查看某场招聘会的通过企业 */
    List<RegistrationDetailVO> getByJobFairId(Long jobFairId);

    /** 导出 Excel */
    void export(RegistrationPageDTO dto, HttpServletResponse response);
}
