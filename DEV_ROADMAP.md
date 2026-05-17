# 开发路线图 — 按这个顺序写，一行不乱

## 阶段 0：画在纸上（0 行代码）

```
想清楚：谁用这个系统？管理员发布招聘会 → 企业报名 → 管理员审核 → 学生浏览
画数据流：管理员登录 → 创建招聘会 → 企业填表报名+岗位 → 管理员查报名 → 审核通过/驳回
定状态机：报名状态: 待审核(0) → 已通过(1) / 已驳回(2)
          招聘会状态: 草稿(0) → 已发布(1) → 已结束(2)
```

---

## 阶段 1：数据库（30% 工作量）

```
文件: sql/schema.sql

写第1张表 admin       →  创建 entity/Admin.java        →  回到 schema.sql
写第2张表 company     →  创建 entity/Company.java      →  回到 schema.sql
写第3张表 job_fair    →  创建 entity/JobFair.java      →  回到 schema.sql
写第4张表 registration → 创建 entity/Registration.java  →  回到 schema.sql
写第5张表 position    →  创建 entity/Position.java     →  结束
```

### 之后还要写

```
文件: src/main/resources/application.yml
配置数据库连接、MyBatis、端口
```

---

## 阶段 2：公共组件（边写边抽）

```
1. common/Result.java              ← 写 Service 前必须先有统一返回格式
2. common/BusinessException.java   ← 写 Service 时需要抛异常
3. common/GlobalExceptionHandler.java ← 有了异常需要全局捕获
4. common/PageResult.java          ← 写分页查询时发现需要
```

---

## 阶段 3：数据访问层 Mapper（从最简单的开始）

```
Admin（最简单，只有 findById + insert）
  1. mapper/AdminMapper.java       ← 接口：定义方法签名
  2. mapper/AdminMapper.xml        ← SQL：写具体查询
  3. 验证：写个 main 方法测一下能不能查到数据

Company
  4. mapper/CompanyMapper.java
  5. mapper/CompanyMapper.xml

JobFair
  6. mapper/JobFairMapper.java
  7. mapper/JobFairMapper.xml
  8. ★ 发现了 Bug：INSERT 漏了 status 字段 → 修复

Registration（最复杂）
  9. mapper/RegistrationMapper.java
  10. mapper/RegistrationMapper.xml    ← 动态SQL + LEFT JOIN

Position
  11. mapper/PositionMapper.java
  12. mapper/PositionMapper.xml        ← batchInsert 批量插入
```

---

## 阶段 4：DTO/VO（Service 写的时候需要就创建）

```
1. dto/AdminLoginDTO.java           ← login() 需要
2. dto/JobFairCreateDTO.java        ← create() 需要
3. dto/CompanyRegisterDTO.java      ← register() 需要（含 PositionItem 内部类）
4. dto/RegistrationPageDTO.java     ← 分页查询需要（含 offset 计算）
5. dto/AuditDTO.java                ← 审核需要
6. vo/JobFairListVO.java            ← 返回招聘会列表需要
7. vo/RegistrationDetailVO.java     ← 返回报名详情需要（含 PositionVO 内部类）
```

---

## 阶段 5：业务逻辑 Service（同样从最简单的开始）

```
Admin（最简单，登录验证）
  1. service/AdminService.java          ← 接口
  2. service/impl/AdminServiceImpl.java ← 实现
  3. 验证：写单元测试 → 3 个用例全通过

JobFair
  4. service/JobFairService.java
  5. service/impl/JobFairServiceImpl.java
  6. 验证：写单元测试 → 4 个用例全通过

Company（最复杂，多表+事务）
  7. service/CompanyService.java
  8. service/impl/CompanyServiceImpl.java
     ★ 知识点：@Transactional、数据去重、DTO→Entity转换、批量插入
  9. 验证：写单元测试 → 3 个用例 → 发现 bug → 修复 → 再跑 → 通过

Registration
  10. service/RegistrationService.java
  11. service/impl/RegistrationServiceImpl.java
      ★ 知识点：分页计算、VO组装、审核业务校验、Excel导出
  12. 验证：写单元测试 → 5 个用例 → 通过
```

---

## 阶段 6：HTTP 接口 Controller

```
1. controller/AdminController.java
   /api/admin/login              POST    登录
   /api/admin/job-fair           POST    创建招聘会
   /api/admin/job-fair/{id}      PUT     编辑招聘会
   /api/admin/job-fair           GET     查看所有招聘会
   /api/admin/registration/page  GET     报名分页
   /api/admin/registration/{id}  GET     报名详情
   /api/admin/registration/{id}/audit PUT 审核
   /api/admin/registration/export  GET   导出Excel

2. controller/CompanyController.java
   /api/company/register         POST    企业报名

3. controller/JobFairController.java
   /api/job-fair/list            GET     学生浏览招聘会
   /api/job-fair/{id}            GET     招聘会详情+参会企业
```

---

## 阶段 7：配置收尾

```
1. config/WebConfig.java           ← 跨域CORS，前端对接需要
2. config/DataInitializer.java     ← 默认管理员自动创建
```

---

## 阶段 8：测试验证

```
1. Postman 导入 postman_collection.json → 逐个测 10 个接口
2. 写单元测试：4 个 test 类，24 个用例
3. mvn test → BUILD SUCCESS
```

---

## 核心思维总结

```
为什么要从下往上写？
  → 每一层依赖下一层，就像盖房子不能先装窗户再打地基

为什么先写最简单的模块？
  → 先在登录上跑通全流程（Entity→Mapper→Service→Controller→Postman验证）
  → 验证框架配置正确、MyBatis 正常工作
  → 然后再写复杂的业务逻辑

为什么每写完一层就验证？
  → 写完 Mapper 就测 Mapper，不要等了
  → 80% 的 Bug 来自"我以为上一步是对的"
  → 及早发现，及时修复

公共类什么时候创建？
  → 不是一开始就设计好的
  → 写到某个地方发现"这个需求反复出现"→ 抽出来
  → 比如写到第2个 Service 发现每个方法都要包装返回 → 创建 Result
  → 写到第3个 Service 发现每个方法都要抛异常 → 创建 BusinessException
```

---

## 文件清单（总共 30 个源文件）

```
sql/                       1 个: schema.sql
src/main/resources/        7 个: application.yml + 5个Mapper.xml + 1个fix_mysql_user.sql
entity/                    5 个: Admin Company JobFair Registration Position
mapper/(接口)              5 个: Admin Company JobFair Registration Position
service/(接口)             4 个: Admin Company JobFair Registration
service/impl/(实现)        4 个: Admin Company JobFair Registration
controller/                3 个: Admin Company JobFair
dto/                       5 个: AdminLogin JobFairCreate CompanyRegister RegistrationPage Audit
vo/                        2 个: JobFairList RegistrationDetail
common/                    4 个: Result PageResult BusinessException GlobalExceptionHandler
config/                    2 个: WebConfig DataInitializer
test/                      4 个: AdminServiceImpl CompanyServiceImpl JobFairServiceImpl RegistrationServiceImpl

总计: ~46 个文件（含 test JSON 文件）
```
