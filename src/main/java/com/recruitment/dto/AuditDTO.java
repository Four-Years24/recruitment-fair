package com.recruitment.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 审核请求
 */
public class AuditDTO {

    @NotNull(message = "审核状态不能为空")
    private Integer status;         // 1通过 2驳回

    private String rejectReason;    // 驳回时必填原因

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
