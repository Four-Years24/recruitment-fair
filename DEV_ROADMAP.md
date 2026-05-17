# 校园招聘会管理系统 — 完整开发路线图

## 总原则

```
从底层往上层写，从稳定往易变写。
数据库 → Entity → Mapper → Service → Controller → 横切能力
每一步写完就验证，不攒到最后。
```

## 阶段 0：纸上设计（0 行代码）

```
想清三件事：
1. 谁用这系统？管理员发布招聘会 → 企业报名 → 管理员审核 → 学生浏览
2. 有哪些数据？管理员、企业、招聘会、报名记录、岗位
3. 状态怎么变？报名：待审核→已通过/已驳回  招聘会：草稿→已发布→已结束
```

## 阶段 1：数据库（项目地基）

```
文件: sql/schema.sql

admin        ← 第 1 张表，独立 → entity/Admin.java       → 回到 schema.sql
company      ← 第 2 张表，独立 → entity/Company.java     → 回到 schema.sql
job_fair     ← 第 3 张表，独立 → entity/JobFair.java     → 回到 schema.sql
registration ← 第 4 张表，关联 company + job_fair
                                  → entity/Registration.java
position     ← 第 5 张表，挂在 registration 下
                                  → entity/Position.java

文件: application.yml  ← 数据库连接、MyBatis、端口
```

## 阶段 2：公共组件（边写边抽）

```
common/Result.java              ← 先写，统一返回格式 {code, message, data}
common/BusinessException.java   ← Service 需要抛业务异常
common/GlobalExceptionHandler.java ← 统一捕获异常转 Result
common/PageResult.java          ← 写分页时创建
```

## 阶段 3：数据访问层（从最简单的 Admin 开始）

```
AdminMapper.java → AdminMapper.xml
  findByUsername() + insert()

CompanyMapper.java → CompanyMapper.xml
  insert() + findById() + findByName() + update()

JobFairMapper.java → JobFairMapper.xml
  insert() + findById() + findPublished() + findAll() + update()
  ★ Bug: INSERT 漏了 status 列

RegistrationMapper.java → RegistrationMapper.xml
  ★ 最复杂：动态 SQL + LEFT JOIN + 分页 + COUNT

PositionMapper.java → PositionMapper.xml
  ★ batchInsert: <foreach> 批量插入，一条 SQL 替代 N 条
```

## 阶段 4：DTO / VO（Service 需要时创建）

```
dto/AdminLoginDTO.java           ← login() 参数
dto/JobFairCreateDTO.java        ← create() 参数
dto/CompanyRegisterDTO.java      ← register() 参数（含 PositionItem 内嵌类）
dto/RegistrationPageDTO.java     ← 分页查询参数（含 offset 计算）
dto/AuditDTO.java                ← 审核参数
vo/JobFairListVO.java            ← 招聘会列表响应
vo/RegistrationDetailVO.java     ← 报名详情响应（含 PositionVO 内嵌类）
```

## 阶段 5：业务逻辑（从最简单的 Admin 开始）

```
AdminService.java → AdminServiceImpl.java
  login(): 查用户 → 验 BCrypt → 返回用户名

JobFairService.java → JobFairServiceImpl.java
  create/update/listPublished/listAll/getById

CompanyService.java → CompanyServiceImpl.java
  ★ register(): 6 步流程 + @Transactional 事务
  1.查企业是否已存在 → 2.不存在则新建 → 3.查是否重复报名
  → 4.创建报名(状态=待审核) → 5.批量插入岗位 → 6.返回报名ID

RegistrationService.java → RegistrationServiceImpl.java
  page(): 分页 + LEFT JOIN + 动态条件
  getDetail(): 组装 VO（报名+企业+招聘会+岗位）
  audit(): 审核通过/驳回（驳回必填原因）
  export(): Apache POI 生成 Excel
```

## 阶段 6：HTTP 接口（最后写）

```
AdminController.java    ← /api/admin/*    8 个接口
CompanyController.java  ← /api/company/*  1 个接口
JobFairController.java  ← /api/job-fair/* 2 个接口
```

## 阶段 7：配置收尾

```
WebConfig.java          ← CORS 跨域
DataInitializer.java    ← 首次启动创建默认管理员 admin/admin123
```

---

## 阶段 8：单元测试（写一个测一个）

```
AdminServiceImplTest.java        → 5 个用例
CompanyServiceImplTest.java      → 5 个用例（含 doAnswer 模拟 ID 回填）
JobFairServiceImplTest.java      → 6 个用例
RegistrationServiceImplTest.java → 8 个用例（嵌套 @Nested 分组）
JwtUtilTest.java                 → 5 个用例
```

---

## 阶段 9：JWT 认证（安全层）

```
新增文件:
  util/JwtUtil.java               ← 生成/验证/解析 JWT
  config/AuthInterceptor.java     ← 拦截器，检查 Authorization Header

修改文件:
  pom.xml                         ← jjwt 依赖
  config/WebConfig.java           ← 注册拦截器，放行 /api/admin/login
  AdminController.java            ← login() 返回真 JWT

认证流程:
  登录 → JwtUtil.generateToken() → 返回 token
  后续请求 → AuthInterceptor 拦截 → 从 Header 取 token → 验证 → 放行/拒绝
  管理端需要 token，企业端和学生端公开访问
```

---

## 阶段 10：Swagger API 文档（可观测性）

```
新增文件:
  config/OpenApiConfig.java       ← API 信息 + JWT 认证按钮

修改文件:
  pom.xml                         ← springdoc 2.8.0
  AdminController.java            ← @Tag + @Operation 注解
  CompanyController.java          ← @Tag + @Operation 注解
  JobFairController.java          ← @Tag + @Operation 注解
  ★ 兼容性: springdoc 2.6.0 不兼容 Spring Boot 4.x，升到 2.8.0

访问: http://localhost:8080/swagger-ui/index.html
```

---

## 阶段 11：Git 版本控制 + GitHub（基础设施）

```
1. git init
2. .gitignore                    ← 排除 .idea/ target/ test_*.json
3. git add → git commit
4. GitHub 创建仓库
5. git remote add origin → git push
6. Personal Access Token 认证（repo + workflow 权限）

以后每完成一个功能:
  git add → git commit → git push
```

---

## 阶段 12：缓存（性能优化）

```
新增文件:
  config/CacheConfig.java         ← Caffeine 配置（10分钟过期，100条上限）

修改文件:
  pom.xml                         ← spring-boot-starter-cache + caffeine
  JobFairServiceImpl.java         ← @Cacheable + @CacheEvict

缓存策略:
  @Cacheable("jobFairs")     → listPublished() 查缓存
  @CacheEvict(allEntries)    → create() / update() 清缓存
  管理端 listAll() 不缓存（需要实时看到草稿状态）

★ 以后换 Redis: 只改 CacheConfig，业务代码零改动
```

---

## 阶段 13：CI/CD 自动测试（DevOps）

```
新增文件:
  .github/workflows/test.yml      ← push 到 master 时自动 mvn test

修改文件:
  README.md                       ← 添加测试状态徽章

流程:
  git push → GitHub Actions 启动 Ubuntu VM
  → 检出代码 → 装 JDK 17 → 缓存 Maven 依赖 → mvn test
  → 34 个测试全过 → ✅ 绿色徽章

★ 第一次失败: mvnw 无执行权限 → chmod +x
★ 第二次失败: Token 无 workflow 权限 → 重新生成
```

---

## 阶段 14：Docker 容器化（一键部署）

```
新增文件:
  Dockerfile                      ← 两阶段构建（Maven编译 + JRE运行）
  docker-compose.yml              ← MySQL + App 编排，自动建表

★ 当前环境（阿里云 ECS 虚拟机）无法运行 Docker Desktop
★ Dockerfile 和 compose 文件作为项目交付物，有 Docker 环境即可一键启动
```

---

## 阶段 15：集成测试（真连数据库）

```
新增文件:
  test/resources/application.yml  ← H2 内存数据库配置
  test/resources/schema-h2.sql    ← H2 兼容建表脚本
  test/.../AdminServiceIntegrationTest.java    ← 登录集成测试（3个用例）
  test/.../CompanyServiceIntegrationTest.java  ← 报名集成测试（2个用例）

修改文件:
  pom.xml                         ← H2 依赖
  config/DataInitializer.java     ← @Profile("!test") 测试环境跳过

★ 问题: DataInitializer 在 @Sql 建表前就执行了 → 加 @Profile 跳过
★ 问题: 多个 @SpringBootTest 共用 H2 → DROP TABLE IF EXISTS 幂等建表
```

---

## 阶段 16：AOP 请求日志（可观测性）

```
新增文件:
  aspect/LoggingAspect.java       ← 环绕通知记录 Controller 方法调用

修改文件:
  pom.xml                         ← aspectjweaver 依赖

日志内容:
  → 入参 + 方法名
  ← 耗时 + 成功/失败 + 异常堆栈
  密码类参数显示 [***] 不打印明文

★ Spring Boot 4.x 无 spring-boot-starter-aop，改引用 aspectjweaver
```

---

## 阶段 17：多环境配置（工程化）

```
新增文件:
  application-dev.yml             ← 开发环境（SQL日志 + debug级别）
  application-prod.yml            ← 生产环境（${DB_URL} 环境变量注入）

修改文件:
  application.yml                 ← 精简为公共配置 + spring.profiles.active=dev

启动方式:
  开发: mvn spring-boot:run -Dspring-boot.run.profiles=dev
  生产: java -jar app.jar --spring.profiles.active=prod
```

---

## 阶段 18：登录限流（安全防护）

```
新增文件:
  config/LoginRateLimiter.java    ← ConcurrentHashMap 计数器

修改文件:
  AdminController.java            ← login() 注入限流器

策略:
  同 IP 连续 5 次登录失败 → 锁定 15 分钟
  登录成功 → 清除失败计数
  锁定中请求 → HTTP 429 + 剩余秒数
```

---

## 阶段 19：README 文档（项目门面）

```
README.md:
  技术栈表格
  功能模块说明
  项目结构树
  API 列表（管理端/企业端/学生端）
  认证说明
  Docker / 本地 两种运行方式
  测试运行命令
  GitHub Actions 状态徽章
```

---

## 文件全景

```
recruitment-fair/
├── .github/workflows/test.yml                  # CI 自动测试
├── Dockerfile                                   # 容器镜像构建
├── docker-compose.yml                          # 一键启动 MySQL + App
├── DEV_ROADMAP.md                              # 本文件
├── README.md                                   # 项目文档
├── pom.xml                                     # Maven 依赖
├── sql/schema.sql                              # 建库建表
│
├── src/main/java/com/recruitment/
│   ├── RecruitmentApplication.java             # 启动类
│   ├── aspect/
│   │   └── LoggingAspect.java                  # AOP 日志切面
│   ├── common/
│   │   ├── BusinessException.java              # 业务异常
│   │   ├── GlobalExceptionHandler.java         # 全局异常处理
│   │   ├── PageResult.java                     # 分页响应
│   │   └── Result.java                         # 统一响应
│   ├── config/
│   │   ├── AuthInterceptor.java                # JWT 拦截器
│   │   ├── CacheConfig.java                    # 缓存配置
│   │   ├── DataInitializer.java                # 默认管理员初始化
│   │   ├── LoginRateLimiter.java               # 登录限流
│   │   ├── OpenApiConfig.java                  # Swagger 配置
│   │   └── WebConfig.java                      # CORS + 拦截器注册
│   ├── controller/
│   │   ├── AdminController.java                # /api/admin/*
│   │   ├── CompanyController.java              # /api/company/*
│   │   └── JobFairController.java              # /api/job-fair/*
│   ├── dto/                                    # 请求对象 (5个)
│   ├── entity/                                 # 实体类 (5个)
│   ├── mapper/                                 # Mapper接口 + XML (5组)
│   ├── service/                                # 业务接口 + 实现 (4组)
│   ├── util/JwtUtil.java                       # JWT 工具
│   └── vo/                                     # 响应对象 (2个)
│
├── src/main/resources/
│   ├── application.yml                         # 公共配置
│   ├── application-dev.yml                    # 开发环境
│   ├── application-prod.yml                   # 生产环境
│   └── mapper/                                 # MyBatis XML (5个)
│
└── src/test/
    ├── java/com/recruitment/
    │   ├── service/impl/
    │   │   ├── AdminServiceImplTest.java       # 单元测试
    │   │   ├── AdminServiceIntegrationTest.java # 集成测试
    │   │   ├── CompanyServiceImplTest.java
    │   │   ├── CompanyServiceIntegrationTest.java
    │   │   ├── JobFairServiceImplTest.java
    │   │   └── RegistrationServiceImplTest.java
    │   └── util/
    │       └── JwtUtilTest.java
    └── resources/
        ├── application.yml                     # 测试配置 (H2)
        └── schema-h2.sql                       # H2 建表脚本
```

---

## 核心思维总结

```
1. 从底层往上层写：数据库 → Entity → Mapper → Service → Controller
2. 从最简单的开始：Admin（登录，2个方法）→ Company（报名，6步流程）
3. 写完一层验证一层：Mapper 写完就测 Mapper，不要等
4. 公共类边写边抽：不是提前设计的，是"反复出现"了才抽
5. 横切能力后加：认证、缓存、日志、限流都不影响核心业务
6. 每个 commit 一个功能：可追溯、可回滚、面试时能讲清楚
```
