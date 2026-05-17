package com.recruitment.entity;

import java.time.LocalDateTime;

/**
 * 报名记录实体 — 连接企业和招聘会的中间表
 */
public class Registration {

    private Long id;
    private Long companyId;         // 企业ID
    private Long jobFairId;         // 招聘会ID
    private String boothNumber;     // 展位号
    private Integer status;         // 0待审核 1已通过 2已驳回
    private String rejectReason;    // 驳回原因
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ---------- getter / setter ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getJobFairId() { return jobFairId; }
    public void setJobFairId(Long jobFairId) { this.jobFairId = jobFairId; }

    public String getBoothNumber() { return boothNumber; }
    public void setBoothNumber(String boothNumber) { this.boothNumber = boothNumber; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
