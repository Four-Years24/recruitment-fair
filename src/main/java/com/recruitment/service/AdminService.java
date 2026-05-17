package com.recruitment.service;

import com.recruitment.dto.AdminLoginDTO;

/**
 * 管理员服务
 */
public interface AdminService {

    /**
     * 登录
     * @return 管理员用户名（用于后续 session/token）
     */
    String login(AdminLoginDTO dto);
}
