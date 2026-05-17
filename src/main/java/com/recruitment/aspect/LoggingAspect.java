package com.recruitment.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// ============================================================
// [编写顺序 LOG-1] AOP 日志切面
// [原理] AOP = 面向切面编程
//        把"日志记录"这个横切关注点从业务代码里抽出来
//        不用在每个方法里写 logger.info()，一切自动完成
// [思维] 切面像一个透明罩子，罩在所有 Controller 方法上面
//        请求进来 → 切面记录"谁、调了什么、参数是什么"
//        方法执行 → 切面记录"耗时多少"
//        异常抛出 → 切面记录"什么错、堆栈"
//        业务代码完全不知道这一切的发生
// ============================================================
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // 切点：所有 Controller 里的 public 方法
    @Pointcut("execution(public * com.recruitment.controller..*.*(..))")
    public void controllerMethods() {}

    // 环绕通知：包裹在切点方法的前后
    @Around("controllerMethods()")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // 请求进来时
        log.info("→ {} | 参数: {}", method, formatArgs(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();  // 执行真实方法
            long elapsed = System.currentTimeMillis() - start;
            log.info("← {} | 耗时: {}ms | 成功", method, elapsed);
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("← {} | 耗时: {}ms | 异常: {}", method, elapsed, e.getMessage(), e);
            throw e;  // 重新抛出让 GlobalExceptionHandler 处理
        }
    }

    // 过滤敏感参数（密码），不在日志里打印明文
    private String formatArgs(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String s = arg.toString();
                    // 如果包含 password，只显示类型，不显示值
                    if (s.contains("password")) {
                        return "[***]";
                    }
                    // 太长的参数截断
                    if (s.length() > 200) {
                        return s.substring(0, 200) + "...";
                    }
                    return s;
                })
                .toList()
                .toString();
    }
}
