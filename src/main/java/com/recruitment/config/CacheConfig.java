package com.recruitment.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

// ============================================================
// [编写顺序 Cache-1] 缓存配置
// [思维] 为什么抽成配置而不是在 Service 里写死？
//        以后换 Redis，只改这个文件，Service 代码不动
//        Spring Cache 抽象层屏蔽了底层缓存实现
// ============================================================
@Configuration
@EnableCaching  // ← 开启 Spring Cache 注解支持
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)  // 写后 10 分钟过期
                .maximumSize(100)                          // 最多缓存 100 个 key
                .recordStats());                            // 开启统计，方便调优
        return manager;
    }
    // [思维] expireAfterWrite vs expireAfterAccess:
    //        write = 数据写入后开始计时，到期失效（适合招聘会列表）
    //        access = 每次访问重置计时（适合用户 session）
    // [思维] 缓存过期 ≠ 数据丢了。过期后下次请求重新查库。
}
