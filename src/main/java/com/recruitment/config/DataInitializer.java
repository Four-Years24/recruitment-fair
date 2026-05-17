package com.recruitment.config;

import com.recruitment.entity.Admin;
import com.recruitment.mapper.AdminMapper;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

// ============================================================
// [编写顺序 7.1] 数据初始化 — 项目启动时自动执行
// [前置] 所有代码都写完了 → 收尾阶段
// [思维] 这个问题很常见："默认管理员账号怎么创建？"
//        不能手动每次去数据库 INSERT，也不能写在 schema.sql 里
//        因为密码要 BCrypt 加密，SQL 做不到
//        最佳方案：用 Spring Boot 的 CommandLineRunner 钩子
// ============================================================
import org.springframework.context.annotation.Profile;

@Profile("!test")  // 测试环境不执行，避免 DataInitializer 先于 @Sql 建表时报错
@Component
public class DataInitializer implements CommandLineRunner {
    // [思维] CommandLineRunner 是 Spring Boot 的内置接口
    //        实现它 → 项目启动完成后自动执行 run() 方法
    //        类似的还有 ApplicationRunner，传参方式不同

    @Resource
    private AdminMapper adminMapper;
    // [思维] 这里可以注入任意 Mapper/Service
    //        典型用途：初始化字典表、默认管理员、基础配置等

    @Override
    public void run(String... args) {
        // [顺序 7.1.1] 先查默认管理员是否已存在
        //             这是"幂等性"的关键：第一次启动创建，后续启动跳过
        //             如果每次启动都 insert，唯一索引冲突崩溃
        Admin exist = adminMapper.findByUsername("admin");

        // [顺序 7.1.2] 不存在 → 创建
        //             密码用 BCrypt 加密后存储
        //             注意：BCrypt.gensalt() 每次生成的 salt 不同
        //             所以同样的 "admin123" 每次加密结果不同
        if (exist == null) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setRealName("系统管理员");
            adminMapper.insert(admin);
            // [顺序 7.1.3] 打印日志，方便启动时确认初始化成功
            System.out.println(">>> 默认管理员已创建: admin / admin123");
        }
        // [思维] 如果 exist != null → 什么都不做
        //        这才是正确的初始化方式
        //        很多新手直接 INSERT 没有判断存在，第二次启动就崩
    }
}

// [状态] 所有代码编写完成
// [思维] 完整项目写完，按这个顺序从头到尾回顾一遍：
//        schema.sql → Entity → Result/BusinessException →
//        Mapper(接口+XML) → DTO/VO →
//        Service(接口+实现) → Controller → Config
//
// [核心心得]
//        1. 从底层往上层写，从稳定往易变写
//        2. 每写完一层验证一次，不要全部写完再测
//        3. 先写最简单的模块找手感（Admin 登录）
//           再写最复杂的模块（Company 报名）
//        4. 公共类（Result、Exception）是边写边抽出来的
//           不是一开始就设计好的
//        5. 写完一个接口立即用 Postman 测，不要攒到最后
