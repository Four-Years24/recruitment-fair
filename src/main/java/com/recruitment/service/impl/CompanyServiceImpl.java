package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.CompanyRegisterDTO;
import com.recruitment.entity.Company;
import com.recruitment.entity.Position;
import com.recruitment.entity.Registration;
import com.recruitment.mapper.CompanyMapper;
import com.recruitment.mapper.PositionMapper;
import com.recruitment.mapper.RegistrationMapper;
import com.recruitment.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// [编写顺序 5.3] CompanyService — 最复杂的 Service
// [前置] CompanyMapper、RegistrationMapper、PositionMapper 全完成
// [思维] 这个方法是整个项目中最能体现"工程化思维"的代码
//        涉及到：事务管理、数据去重、批量插入、DTO 到 Entity 的转换
// ============================================================
@Service
public class CompanyServiceImpl implements CompanyService {

    // [顺序 5.3.1] 注入三个 Mapper
    //             为什么需要三个？
    //             因为企业报名涉及三张表：company、registration、position
    //             每个 Mapper 只管自己那张表
    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private RegistrationMapper registrationMapper;

    @Resource
    private PositionMapper positionMapper;

    // [顺序 5.3.2] @Transactional = 下面这些操作要么全成功，要么全回滚
    //             如果 batchInsert 失败 → company 和 registration 的 insert 自动撤销
    //             原理：Spring AOP 代理在方法外面包了一层 try-catch
    //             try { 执行方法 } catch { rollback() }
    // [思维] 什么时候加 @Transactional：
    //        一个方法里有多条写操作（INSERT/UPDATE/DELETE）→ 必须加
    //        只有读操作 → 不需要，加 readonly=true 优化性能
    @Override
    @Transactional
    public Long register(CompanyRegisterDTO dto) {

        // [顺序 5.3.3] 第一步：根据企业名称查是否存在
        //             这里体现了"数据去重"的思维
        //             不能每次报名都新建企业，同名的要复用
        // [思维] 为什么按名称查而不是让前端传 companyId？
        //        企业报名时不知道自己的 ID（甚至不知道自己是否在系统里）
        //        按名称查最自然，类似"用手机号查用户是否存在"
        Company company = companyMapper.findByName(dto.getCompanyName());

        // [顺序 5.3.4] 第二步：企业不存在 → 新增
        //             DTO 转 Entity：把前端传来的数据拷贝到 Entity 对象
        //             然后 insert 到数据库
        if (company == null) {
            company = new Company();
            company.setName(dto.getCompanyName());
            company.setIndustry(dto.getIndustry());
            company.setScale(dto.getScale());
            company.setNature(dto.getNature());
            company.setAddress(dto.getAddress());
            company.setContactName(dto.getContactName());
            company.setContactPhone(dto.getContactPhone());
            company.setContactEmail(dto.getContactEmail());
            company.setLicenseUrl(dto.getLicenseUrl());
            company.setDescription(dto.getCompanyDescription());
            // [思维] 这里没设 status，数据库默认值是 1（启用）
            //        创建的企业默认就是启用的
            companyMapper.insert(company);
            // [思维] insert 之后，MyBatis 自动把自增 ID 回填到 company.id
            //        后续代码可以用 company.getId() 获取新生成的 ID
        }

        // [顺序 5.3.5] 第三步：检查重复报名（同一企业 + 同一招聘会）
        //             这里体现了"防御性编程"：信任前端但验证它
        //             前端可能双击提交按钮，或者用户开了两个窗口
        // [思维] findByCompanyAndFair 接收 companyId 和 jobFairId
        //        存在的记录 ≠ null → 说明已经报过名了 → 抛异常
        Registration existReg = registrationMapper.findByCompanyAndFair(
                company.getId(), dto.getJobFairId());
        if (existReg != null) {
            throw new BusinessException("该企业已报名本场招聘会，请勿重复提交");
        }

        // [顺序 5.3.6] 第四步：创建报名记录
        //             状态设为 0（待审核），等管理员来审核
        Registration reg = new Registration();
        reg.setCompanyId(company.getId());
        reg.setJobFairId(dto.getJobFairId());
        reg.setStatus(0);  // 0=待审核，1=已通过，2=已驳回
        // [思维] 展位号 boothNumber 暂时不设，审核通过后由管理员分配
        registrationMapper.insert(reg);
        // [思维] insert 后 reg.getId() 被 MyBatis 回填

        // [顺序 5.3.7] 第五步：遍历岗位列表，逐个转成 Entity
        //             这里体现了 DTO 和 Entity 分层的好处
        //             DTO 是前端传来的格式（PositionItem 内嵌在 CompanyRegisterDTO 里）
        //             Entity 是数据库的格式（Position 单独一张表）
        //             两者结构不同，需要手动转换
        List<Position> positions = new ArrayList<>();
        for (CompanyRegisterDTO.PositionItem item : dto.getPositions()) {
            Position p = new Position();
            p.setRegistrationId(reg.getId());  // ← 关键：关联到刚创建的报名记录
            p.setTitle(item.getTitle());
            p.setHeadcount(item.getHeadcount());
            p.setEducation(item.getEducation());
            p.setMajorRequire(item.getMajorRequire());
            p.setSalaryRange(item.getSalaryRange());
            p.setWorkCity(item.getWorkCity());
            p.setDescription(item.getDescription());
            positions.add(p);
        }

        // [顺序 5.3.8] 第六步：批量插入岗位
        //             batchInsert 用一条 SQL 插入所有岗位
        //             比循环 positionMapper.insert(p) 高效得多
        //             100 个岗位 → 1 条 SQL vs 100 条 SQL
        // [思维] MyBatis <foreach> 标签实现批量插入：
        //             INSERT INTO position (...) VALUES (...), (...), (...)
        positionMapper.batchInsert(positions);

        // [顺序 5.3.9] 最后返回报名记录 ID
        //             前端可以根据这个 ID 查询审核进度
        return reg.getId();
    }
}

// [状态] CompanyService 完成
// [思维] 写完这个方法你学到的工程化知识点：
//        1. 多表操作的事务管理
//        2. 数据去重（按名称查企业，按组合条件查重复报名）
//        3. DTO → Entity 的手动转换（分层解耦的体现）
//        4. 批量插入 vs 循环单条插入（性能思维）
//        5. 防御性编程（验证重复报名，不信任前端）
// [下一步] 写完 Service 后你会发现缺少 CompanyRegisterDTO
//         → 去 dto/ 目录创建（已经创建好了，因为写 Service 前就规划了）
//         → 继续写 RegistrationService（分页查询 + 审核 + 导出）
//         → 全部 Service 完成后进入 Phase 7：Controller 层
