# 校园招聘会管理系统

[![测试](https://github.com/Four-Years24/recruitment-fair/actions/workflows/test.yml/badge.svg)](https://github.com/Four-Years24/recruitment-fair/actions/workflows/test.yml)

Spring Boot + MyBatis + JWT 全栈后端项目，解决校园招聘会企业报名与审核管理。

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 17 (Eclipse Temurin) |
| Spring Boot | 4.0.6 |
| MyBatis | 4.0.1 |
| MySQL | 8.4 |
| JWT (jjwt) | 0.12.5 |
| Swagger (springdoc) | 2.8.0 |
| Apache POI | 5.2.3 |
| BCrypt (jbcrypt) | 0.4 |
| JUnit 5 + Mockito | 测试 |

## 功能模块

- **管理员**：登录 → 创建招聘会 → 查看企业报名 → 审核通过/驳回 → 导出 Excel
- **企业**：在线报名招聘会，提交企业信息 + 岗位列表
- **学生**：浏览已发布的招聘会，查看参会企业及岗位

## 项目结构

```
src/main/java/com/recruitment/
├── common/          # 公共组件（统一响应、异常处理、分页）
├── config/          # 配置（CORS、JWT拦截器、Swagger、数据初始化）
├── controller/      # HTTP 接口层（3个Controller，10个API）
├── dto/             # 请求参数对象
├── entity/          # 数据库实体
├── mapper/          # MyBatis 数据访问层（接口 + XML）
├── service/         # 业务逻辑层
├── util/            # 工具类（JWT）
└── vo/              # 响应视图对象
```

## 数据库

5 张表：`admin` `company` `job_fair` `registration` `position`

表关系：企业(company) 和 招聘会(job_fair) 通过报名表(registration) 关联，岗位(position) 挂在报名记录下。

## API 文档

项目启动后访问：**http://localhost:8080/swagger-ui/index.html**

### 管理端（需要 JWT）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/login` | 登录，返回 JWT token |
| POST | `/api/admin/job-fair` | 创建招聘会 |
| PUT | `/api/admin/job-fair/{id}` | 编辑招聘会 |
| GET | `/api/admin/job-fair` | 查看所有招聘会 |
| GET | `/api/admin/registration/page` | 报名分页查询（含筛选） |
| GET | `/api/admin/registration/{id}` | 报名详情（含岗位） |
| PUT | `/api/admin/registration/{id}/audit` | 审核报名（通过/驳回） |
| GET | `/api/admin/registration/export` | 导出 Excel |

### 企业端（公开）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/company/register` | 企业报名（含岗位） |

### 学生端（公开）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/job-fair/list` | 浏览已发布招聘会 |
| GET | `/api/job-fair/{id}` | 招聘会详情 + 参会企业 |

## 认证说明

管理端接口（除 `/api/admin/login`）需在 Header 中携带：

```
Authorization: Bearer <登录返回的token>
```

企业端和学生端无需认证。

## 运行项目

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS recruitment_fair
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;
```

### 3. 执行建表

```bash
mysql -u root -p recruitment_fair < sql/schema.sql
```

### 4. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息。

### 5. 启动

```bash
mvnw spring-boot:run
```

首次启动自动创建默认管理员账号：`admin` / `admin123`

### 6. 验证

浏览器访问 http://localhost:8080/swagger-ui/index.html

## 运行测试

```bash
mvnw test
```

29 个单元测试，覆盖 Service 层和 JWT 工具类。

## 开发路线图

详见 [DEV_ROADMAP.md](DEV_ROADMAP.md)，记录了从数据库设计到 Controller 的完整开发顺序和思维过程。
