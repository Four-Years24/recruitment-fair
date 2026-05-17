package com.recruitment.entity;

import java.time.LocalDateTime;

/**
 * 企业实体
 */
public class Company {

    private Long id;
    private String name;            // 企业全称
    private String industry;        // 所属行业
    private String scale;           // 规模
    private String nature;          // 性质（国企/私企/外企/合资/事业单位）
    private String address;         // 公司地址
    private String contactName;     // 联系人姓名
    private String contactPhone;    // 联系人电话
    private String contactEmail;    // 联系人邮箱
    private String licenseUrl;      // 营业执照存储路径
    private String description;     // 企业简介
    private Integer status;         // 0禁用 1启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ---------- getter / setter ----------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
