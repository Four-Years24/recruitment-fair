-- ============================================================
-- [编写顺序 1] 这个文件是整个项目的起点
-- 写完这个文件后，你已经完成了 30% 的工作
-- 因为数据库设计决定了后面所有代码的结构
-- ============================================================
-- [思维] 先想清楚系统里有哪些"东西"：
--   管理员 → admin 表
--   企业   → company 表
--   招聘会 → job_fair 表
--   企业报名招聘会（多对多）→ 需要中间表 registration
--   报名要带岗位 → position 表，挂在 registration 下面
-- ============================================================

CREATE DATABASE IF NOT EXISTS recruitment_fair
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE recruitment_fair;


-- ============================================================
-- [编写顺序 1.1] 第 1 张表：admin
-- [写完这行后] 去创建 entity/Admin.java
-- [思维] 这张表独立存在，不依赖其他表，所以第一个建
-- [思维] password 用 VARCHAR(100) 因为 BCrypt 密文固定 60 字符
-- ============================================================
CREATE TABLE admin (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    username    VARCHAR(50)     NOT NULL                 COMMENT '登录用户名',
    password    VARCHAR(100)    NOT NULL                 COMMENT '密码（BCrypt加密）',
    real_name   VARCHAR(20)     DEFAULT NULL             COMMENT '真实姓名',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员';
-- [返回] schema.sql 继续写第 2 张表


-- ============================================================
-- [编写顺序 1.2] 第 2 张表：company
-- [写完这行后] 去创建 entity/Company.java
-- [思维] company 也独立存在，不依赖 admin
-- [思维] contact_name/phone/email 是联系人信息，不是企业属性
--         企业自身属性是 name/industry/scale/nature/address
-- [思维] status 字段控制企业是否被禁用，软删除而非真删
-- ============================================================
CREATE TABLE company (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    name            VARCHAR(100)    NOT NULL                 COMMENT '企业全称',
    industry        VARCHAR(50)     DEFAULT NULL             COMMENT '所属行业',
    scale           VARCHAR(20)     DEFAULT NULL             COMMENT '规模：1-49/50-99/100-499/500-999/1000+',
    nature          VARCHAR(20)     DEFAULT NULL             COMMENT '性质：国企/私企/外企/合资/事业单位',
    address         VARCHAR(200)    DEFAULT NULL             COMMENT '公司地址',
    contact_name    VARCHAR(20)     DEFAULT NULL             COMMENT '联系人姓名',
    contact_phone   VARCHAR(20)     DEFAULT NULL             COMMENT '联系人电话',
    contact_email   VARCHAR(50)     DEFAULT NULL             COMMENT '联系人邮箱',
    license_url     VARCHAR(200)    DEFAULT NULL             COMMENT '营业执照存储路径',
    description     TEXT            DEFAULT NULL             COMMENT '企业简介',
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：0禁用 1启用',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业';
-- [返回] schema.sql 继续写第 3 张表


-- ============================================================
-- [编写顺序 1.3] 第 3 张表：job_fair
-- [写完这行后] 去创建 entity/JobFair.java
-- [思维] status 字段设计了三个状态：草稿/已发布/已结束
--         这是"状态机"思想的体现——数据在不同状态间流转
-- [思维] start_time 和 end_time 用 DATETIME 而非 DATE
--         因为招聘会可能有具体的小时分钟
-- ============================================================
CREATE TABLE job_fair (
    id          BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    title       VARCHAR(100)    NOT NULL                 COMMENT '招聘会名称',
    start_time  DATETIME        NOT NULL                 COMMENT '开始时间',
    end_time    DATETIME        NOT NULL                 COMMENT '结束时间',
    location    VARCHAR(200)    DEFAULT NULL             COMMENT '举办地点',
    description TEXT            DEFAULT NULL             COMMENT '招聘会说明',
    status      TINYINT         NOT NULL DEFAULT 0       COMMENT '状态：0草稿 1已发布 2已结束',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘会';
-- [返回] schema.sql 继续写第 4 张表


-- ============================================================
-- [编写顺序 1.4] 第 4 张表：registration（中间表）
-- [写完这行后] 去创建 entity/Registration.java
-- [思维] 这是整个系统最核心的表——连接 company 和 job_fair
-- [思维] 为什么需要中间表：
--         一家企业可以参加多场招聘会
--         一场招聘会有多家企业参加
--         这就是"多对多"关系，必须用中间表来拆
-- [思维] company_id + job_fair_id 的组合应该唯一吗？
--         业务上：一家企业不能对同一招聘会重复报名
--         但这里没建唯一索引，而是在 Service 层代码校验
--         两种方式都可以，代码校验更灵活（可以给友好提示）
-- ============================================================
CREATE TABLE registration (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    company_id      BIGINT          NOT NULL                 COMMENT '企业ID',
    job_fair_id     BIGINT          NOT NULL                 COMMENT '招聘会ID',
    booth_number    VARCHAR(10)     DEFAULT NULL             COMMENT '展位号',
    status          TINYINT         NOT NULL DEFAULT 0       COMMENT '状态：0待审核 1已通过 2已驳回',
    reject_reason   VARCHAR(500)    DEFAULT NULL             COMMENT '驳回原因',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_company_id (company_id),
    INDEX idx_job_fair_id (job_fair_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名记录';
-- [返回] schema.sql 继续写第 5 张表


-- ============================================================
-- [编写顺序 1.5] 第 5 张表：position（子表）
-- [写完这行后] 去创建 entity/Position.java
-- [思维] position 挂在 registration 下面，是"一对多"的子表
-- [思维] 为什么岗位不直接放在 registration 里？
--         因为一个企业报名时可以带多个岗位
--         如果放在一张表，就得写 position1_title, position2_title...
--         这叫"字段冗余"，是数据库设计的反面教材
-- [思维] registration_id 是外键，指向 registration.id
--         这里没写 FOREIGN KEY 约束，实际项目建议加上
-- ============================================================
CREATE TABLE position (
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    registration_id BIGINT          NOT NULL                 COMMENT '报名记录ID',
    title           VARCHAR(100)    NOT NULL                 COMMENT '岗位名称',
    headcount       INT             DEFAULT NULL             COMMENT '招聘人数',
    education       VARCHAR(20)     DEFAULT NULL             COMMENT '学历要求',
    major_require   VARCHAR(200)    DEFAULT NULL             COMMENT '专业要求',
    salary_range    VARCHAR(50)     DEFAULT NULL             COMMENT '薪资范围',
    work_city       VARCHAR(50)     DEFAULT NULL             COMMENT '工作城市',
    description     TEXT            DEFAULT NULL             COMMENT '岗位描述',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_registration_id (registration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘岗位';


-- ============================================================
-- [编写顺序 1.6] schema.sql 写完
-- [此时状态] 5 张表全部建好，对应 5 个 Entity 也都创建完了
-- [下一步] 创建 application.yml，配置数据库连接
-- [下一步] 然后开始写 Mapper 层（数据访问层）
-- ============================================================
-- 默认管理员账号由 DataInitializer 在首次启动时自动创建
-- 账号: admin / admin123
