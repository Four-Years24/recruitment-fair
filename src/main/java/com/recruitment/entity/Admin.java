package com.recruitment.entity;

import java.time.LocalDateTime;

// ============================================================
// [编写顺序 2.1] 第一个 Entity，对应 admin 表
// [前置] 刚写完 schema.sql 的 admin 表 → 现在创建对应的 Java 类
// [思维] Entity 命名规则：表名 admin → 类名 Admin（去掉下划线，大驼峰）
//        表字段 username → 属性 username（下划线转驼峰）
//        表字段 real_name → 属性 realName（下划线转驼峰）
//        VARCHAR → String, BIGINT → Long, TINYINT → Integer
//        DATETIME → LocalDateTime（Java 8+ 推荐，不用 Date）
// [思维] Entity 是纯数据容器，不写业务逻辑
//        只有属性 + getter/setter，没有 if/else/循环
// ============================================================
public class Admin {

    // [顺序 2.1.1] 先写好所有属性，对着数据库列一个个翻译
    private Long id;
    private String username;
    private String password;
    private String realName;
    private LocalDateTime createTime;

    // [顺序 2.1.2] 然后用 IDE 自动生成 getter/setter
    //             IntelliJ: Alt+Insert → Getter and Setter → 全选 → OK
    // [思维] getter/setter 是 JavaBean 规范，框架靠它读写属性
    //        面试常问："为什么不用 public 属性？"
    //        答案：封装。以后可以在 setter 里加校验逻辑
    //        比如 setPassword() 里可以要求长度≥6
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

// [状态] Admin Entity 完成
// [下一步] 回到 schema.sql，继续建 company 表
//         → 然后创建 entity/Company.java
//         → 依次完成 5 个 Entity
//         → 全部完成后进入 Phase 3：公共组件
