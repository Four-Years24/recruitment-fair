package com.recruitment.entity;

import java.time.LocalDateTime;

/**
 * 招聘岗位实体 — 属于某一条报名记录
 */
public class Position {

    private Long id;
    private Long registrationId;    // 报名记录ID
    private String title;           // 岗位名称
    private Integer headcount;      // 招聘人数
    private String education;       // 学历要求
    private String majorRequire;    // 专业要求
    private String salaryRange;     // 薪资范围
    private String workCity;        // 工作城市
    private String description;     // 岗位描述
    private LocalDateTime createTime;

    // ---------- getter / setter ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }

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

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
