package com.recruitment.service.impl;

import com.recruitment.common.BusinessException;
import com.recruitment.dto.AdminLoginDTO;
import com.recruitment.entity.Admin;
import com.recruitment.mapper.AdminMapper;
import com.recruitment.service.AdminService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

// ============================================================
// [编写顺序 5.1] 第一个 Service 实现
// [前置] AdminMapper 已完成 → 现在写业务逻辑
// [思维] Service 先写接口再写实现
//        接口 = 定义这个模块提供什么能力
//        实现 = 具体怎么做
//        先写 AdminService.java 接口（就一行 login 方法声明）
//        再写这个 AdminServiceImpl.java 实现类
// [思维] 为什么需要接口？这里只有一个实现类
//        1. 方便写单元测试（Mock 接口而非实现）
//        2. 以后换实现（比如换认证方式）不用改调用方
// ============================================================
@Service  // ← 告诉 Spring：这是一个 Service，帮我管理它的生命周期
public class AdminServiceImpl implements AdminService {

    // [顺序 5.1.1] 注入依赖：@Resource 按名称注入 AdminMapper
    //             这时候 AdminMapper 已经被 MyBatis 生成了实现类
    //             Spring 会自动把实现类的实例赋给这个字段
    @Resource
    private AdminMapper adminMapper;

    // [顺序 5.1.2] 实现 login 方法
    //             参数：AdminLoginDTO，包含 username 和 password
    //             返回：管理员用户名（用于后续 session/token）
    // [思维] 流程分两步：
    //        1. 查用户 → 不存在 = 失败
    //        2. 验密码 → 不对   = 失败
    //        两步都用"用户名或密码错误"，不区分具体原因
    //        这是安全考虑：防止攻击者试探哪些用户名存在
    @Override
    public String login(AdminLoginDTO dto) {
        // [顺序 5.1.3] 第一步：查数据库
        //             调 AdminMapper.findByUsername，传前端发来的 username
        Admin admin = adminMapper.findByUsername(dto.getUsername());

        // [顺序 5.1.4] 第二步：判断用户是否存在
        //             admin == null 说明数据库里没有这个用户名
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // [顺序 5.1.5] 第三步：验证密码
        //             BCrypt.checkpw(明文, 密文) → true/false
        //             内部原理：从密文中提取 salt，用同样的 salt 加密明文
        //             如果结果和密文一致 → 密码正确
        // [思维] 为什么不直接 equals() 比较？
        //        BCrypt 每次加密结果不同（salt 随机），没法直接比较
        //        必须用 checkpw() 方法
        if (!BCrypt.checkpw(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // [顺序 5.1.6] 最后一步：返回用户名
        //             MVP 阶段返回用户名，Controller 会把用户名存 session
        //             工业级应该返回 JWT token
        return admin.getUsername();
    }
}

// [状态] AdminService 完成（接口 + 实现）
// [下一步] 这个 Service 写完后，你会发现缺少 AdminLoginDTO
//         → 去 dto/ 目录创建 AdminLoginDTO.java
//         → 然后回来检查 login 方法的参数类型是否正确
//         → 继续写其他 Service：
//            JobFairService  → 招聘会的增删改查
//            CompanyService  → 企业报名（最复杂，涉及事务）
//            RegistrationService → 报名查询、审核、导出
