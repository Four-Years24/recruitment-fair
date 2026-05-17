package com.recruitment.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

// ============================================================
// [编写顺序 LIMIT-1] 登录限流器
// [原理] 内存中维护一个 Map<IP, 尝试记录>
//        同一IP连续失败 → 计数+1 → 达到阈值 → 锁定15分钟
// [思维] 为什么不存数据库？
//        限流是临时状态，不需要持久化，重启就清空
//        Redis 最佳，但内存 Map 对单机已足够
// [思维] ConcurrentHashMap 是线程安全的
//        多个请求同时登录时不会出现计数错乱
// ============================================================
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;       // 最多尝试次数
    private static final long LOCK_MINUTES = 15;     // 锁定时长（分钟）

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    /**
     * 记录一次失败尝试
     * @return true=已被锁定，false=未锁定
     */
    public boolean recordFailure(String ip) {
        cleanupExpired();

        AttemptRecord record = attempts.compute(ip, (key, existing) -> {
            if (existing == null) {
                return new AttemptRecord(1, null);
            }
            if (existing.isLocked()) {
                return existing;  // 还在锁定中，不递增
            }
            existing.count++;
            if (existing.count >= MAX_ATTEMPTS) {
                existing.lockedUntil = Instant.now().plusSeconds(LOCK_MINUTES * 60);
            }
            return existing;
        });

        return record.isLocked();
    }

    /** 登录成功后清除记录 */
    public void clear(String ip) {
        attempts.remove(ip);
    }

    /** 检查是否被锁定 */
    public boolean isLocked(String ip) {
        AttemptRecord record = attempts.get(ip);
        return record != null && record.isLocked();
    }

    /** 获取剩余锁定秒数 */
    public long getRemainingLockSeconds(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null || !record.isLocked()) return 0;
        return Math.max(0, record.lockedUntil.getEpochSecond() - Instant.now().getEpochSecond());
    }

    private void cleanupExpired() {
        attempts.entrySet().removeIf(entry -> {
            AttemptRecord r = entry.getValue();
            return !r.isLocked() && r.count < MAX_ATTEMPTS &&
                   r.lockedUntil != null &&
                   r.lockedUntil.isBefore(Instant.now());
        });
    }

    private static class AttemptRecord {
        int count;
        Instant lockedUntil;

        AttemptRecord(int count, Instant lockedUntil) {
            this.count = count;
            this.lockedUntil = lockedUntil;
        }

        boolean isLocked() {
            return lockedUntil != null && lockedUntil.isAfter(Instant.now());
        }
    }
}
