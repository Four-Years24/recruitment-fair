package com.recruitment.service;

import com.recruitment.dto.CompanyRegisterDTO;

/**
 * 企业服务 — 处理企业报名等业务
 */
public interface CompanyService {

    /**
     * 企业报名参会
     * 1. 检查企业是否已存在（按名称查找）
     * 2. 不存在则创建新企业
     * 3. 创建报名记录（待审核状态）
     * 4. 批量创建招聘岗位
     *
     * @param dto 报名信息（企业信息 + 岗位列表 + 招聘会ID）
     * @return 报名记录ID
     */
    Long register(CompanyRegisterDTO dto);
}
