package com.recruitment.mapper;

import com.recruitment.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

// ============================================================
// [编写顺序 4.1] 第一个 Mapper 接口
// [前置] Entity 和公共类已完成 → 现在搭数据访问层
// [思维] 为什么先写 Mapper 再写 Service？
//        Service 依赖 Mapper（需要查数据库）
//        如果 Service 写一半发现 Mapper 没提供需要的方法，思路会断
//        所以先把 Mapper 的方法定义好，Service 写起来就顺
// [思维] Mapper 接口只声明"要做什么"，不写"怎么做"
//        "怎么做"在 XML 里写 SQL
//        "要做什么"根据 Service 层的需求来定
// ============================================================
@Mapper  // ← 告诉 MyBatis：这是一个 Mapper，帮我在启动时生成实现类
public interface AdminMapper {

    // [顺序 4.1.1] 登录需要：根据用户名查管理员
    //             参数：username
    //             返回：Admin 对象（查到）或 null（查不到）
    // [思维] 为什么返回 null 而不是 Optional<Admin>？
    //        MyBatis 查不到就是 null，Java 惯例
    //        Service 层判断 if (admin == null) 抛异常
    Admin findByUsername(String username);

    // [顺序 4.1.2] DataInitializer 需要：插入管理员
    //             参数：完整的 Admin 对象
    //             返回：影响行数（1=成功）
    // [思维] 为什么接口只有两个方法？
    //        只写当前需要的，不预留给将来
    //        将来需要改密码时再加 updatePassword 方法
    //        这叫 YAGNI：You Ain't Gonna Need It
    int insert(Admin admin);
}

// [状态] AdminMapper 接口完成
// [下一步] 去 AdminMapper.xml 写对应的 SQL
//         → 为什么先写接口再写 XML？
//         → 接口是"契约"——定了方法名、参数、返回值
//         → XML 是实现——具体的 SQL 语句
//         → 契约先定，实现后做，思路清晰
