package com.recruitment.dto;

/**
 * 报名分页查询 DTO
 * 管理员按条件筛选报名记录
 */
public class RegistrationPageDTO {

    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String companyName;     // 企业名称模糊搜索
    private Long jobFairId;         // 按招聘会筛选
    private Integer status;         // 按审核状态筛选

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    // ---------- getter / setter ----------
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getJobFairId() { return jobFairId; }
    public void setJobFairId(Long jobFairId) { this.jobFairId = jobFairId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
