-- H2 内存数据库建表脚本（集成测试用）
-- 用 DROP TABLE IF EXISTS 确保多个测试类共用时不会冲突

DROP TABLE IF EXISTS position;
DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS job_fair;
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    id          BIGINT  AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(100) NOT NULL,
    real_name   VARCHAR(20),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE company (
    id            BIGINT AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    industry      VARCHAR(50),
    scale         VARCHAR(20),
    nature        VARCHAR(20),
    address       VARCHAR(200),
    contact_name  VARCHAR(20),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(50),
    license_url   VARCHAR(200),
    description   TEXT,
    status        INT DEFAULT 1,
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE job_fair (
    id          BIGINT AUTO_INCREMENT,
    title       VARCHAR(100) NOT NULL,
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    location    VARCHAR(200),
    description TEXT,
    status      INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE registration (
    id            BIGINT AUTO_INCREMENT,
    company_id    BIGINT NOT NULL,
    job_fair_id   BIGINT NOT NULL,
    booth_number  VARCHAR(10),
    status        INT DEFAULT 0,
    reject_reason VARCHAR(500),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE position (
    id               BIGINT AUTO_INCREMENT,
    registration_id  BIGINT NOT NULL,
    title            VARCHAR(100) NOT NULL,
    headcount        INT,
    education        VARCHAR(20),
    major_require    VARCHAR(200),
    salary_range     VARCHAR(50),
    work_city        VARCHAR(50),
    description      TEXT,
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
