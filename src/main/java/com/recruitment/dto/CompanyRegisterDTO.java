package com.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 企业报名请求 DTO
 * 一次报名包含企业基本信息 + 至少一个招聘岗位
 */
public class CompanyRegisterDTO {

    @NotBlank(message = "企业名称不能为空")
    private String companyName;

    private String industry;
    private String scale;
    private String nature;
    private String address;

    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @NotBlank(message = "联系人电话不能为空")
    private String contactPhone;

    private String contactEmail;
    private String licenseUrl;
    private String companyDescription;

    // 这批报名的招聘会ID
    @NotNull(message = "招聘会ID不能为空")
    private Long jobFairId;

    // 至少一个岗位
    @NotEmpty(message = "至少需要一个招聘岗位")
    private List<PositionItem> positions;

    // ---------- getter / setter ----------
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getScale() { return scale; }
    public void setScale(String scale) { this.scale = scale; }

    public String getNature() { return nature; }
    public void setNature(String nature) { this.nature = nature; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getLicenseUrl() { return licenseUrl; }
    public void setLicenseUrl(String licenseUrl) { this.licenseUrl = licenseUrl; }

    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }

    public Long getJobFairId() { return jobFairId; }
    public void setJobFairId(Long jobFairId) { this.jobFairId = jobFairId; }

    public List<PositionItem> getPositions() { return positions; }
    public void setPositions(List<PositionItem> positions) { this.positions = positions; }

    /**
     * 内嵌类：单个岗位
     */
    public static class PositionItem {
        @NotBlank(message = "岗位名称不能为空")
        private String title;
        private Integer headcount;
        private String education;
        private String majorRequire;
        private String salaryRange;
        private String workCity;
        private String description;

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

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
