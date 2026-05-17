package com.recruitment.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报名详情 VO — 管理员查看某条报名的完整信息
 */
public class RegistrationDetailVO {

    private Long id;
    private Long companyId;
    private String companyName;
    private String industry;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Long jobFairId;
    private String jobFairTitle;
    private String boothNumber;
    private Integer status;         // 0待审核 1已通过 2已驳回
    private String rejectReason;
    private LocalDateTime createTime;

    // 关联的岗位列表（Service 层手动填充）
    private List<PositionVO> positions;

    // ---------- getter / setter ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Long getJobFairId() { return jobFairId; }
    public void setJobFairId(Long jobFairId) { this.jobFairId = jobFairId; }

    public String getJobFairTitle() { return jobFairTitle; }
    public void setJobFairTitle(String jobFairTitle) { this.jobFairTitle = jobFairTitle; }

    public String getBoothNumber() { return boothNumber; }
    public void setBoothNumber(String boothNumber) { this.boothNumber = boothNumber; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public List<PositionVO> getPositions() { return positions; }
    public void setPositions(List<PositionVO> positions) { this.positions = positions; }

    /**
     * 内嵌岗位 VO
     */
    public static class PositionVO {
        private String title;
        private Integer headcount;
        private String education;
        private String majorRequire;
        private String salaryRange;
        private String workCity;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getHeadcount() { return headcount; }
        public void setHeadcount(Integer headcount) { this.headcount = headcount; }

        public String getEducation() { return education; }
        public void setEducation(String education) { this.education = education; }

        public String getMajorRequire() { return majorRequire; }
        public void setMajorRequire(String majorRequire) { this.majorRequire = majorRequire; }

        public String getSalaryRange() { return salaryRange; }
        public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

        public String getWorkCity() { return workCity; }
        public void setWorkCity(String workCity) { this.workCity = workCity; }
    }
}
