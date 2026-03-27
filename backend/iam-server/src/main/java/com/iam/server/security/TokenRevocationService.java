package com.iam.server.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 令牌撤销服务
 * 提供对JWT令牌ID (JTI) 的撤销功能。支持两种模式：
 * 1. Redis模式（优先）：将已撤销的JTI存储在Redis中，设置与令牌过期时间一致的TTL。
 * 2. 内存模式：当Redis不可用时，使用ConcurrentHashMap在内存中存储撤销信息。
 * 该服务用于确保已注销或撤销的令牌无法再被用来访问受保护的资源。
 */
@Service
public class TokenRevocationService {
    private static final String PREFIX = "iam:token:revoked:";
    private final boolean redisEnabled;
    private final @Nullable StringRedisTemplate redis;
    private final Map<String, Instant> inMemoryRevokedUntil = new ConcurrentHashMap<>();

    /**
     * 构造函数，用于注入依赖项并根据配置决定是否启用Redis。
     *
     * @param redisProvider 提供StringRedisTemplate实例的工厂，可能为null
     * @param redisEnabled  从配置中读取的布尔值，指示是否启用Redis存储
     */
    public TokenRevocationService(
            final ObjectProvider<StringRedisTemplate> redisProvider,
            @Value("${iam.redis.enabled:true}") final boolean redisEnabled) {
        this.redisEnabled = redisEnabled;
        StringRedisTemplate redisTemplate = null;
        if (redisEnabled) {
            try {
                redisTemplate = redisProvider.getIfAvailable();
            } catch (RuntimeException ignored) {
            }
        }
        this.redis = redisTemplate;
    }

    /**
     * 撤销指定的JWT令牌ID (JTI)。
     * 根据当前配置（Redis是否启用）选择相应的存储策略。
     *
     * @param jti       要撤销的令牌ID
     * @param expiresAt 该令牌的原始过期时间，用于计算在Redis中的TTL
     */
    public void revokeJti(final String jti, final Instant expiresAt) {
        Objects.requireNonNull(jti, "jti");
        Objects.requireNonNull(expiresAt, "expiresAt");

        final StringRedisTemplate redisTemplate = redis;

        if (!redisEnabled || redisTemplate == null) {
            inMemoryRevokedUntil.put(jti, expiresAt);
            return;
        }

        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (ttl.isNegative() || ttl.isZero()) {
            ttl = Duration.ofMinutes(5);
        }
        redisTemplate.opsForValue().set(PREFIX + jti, "1", ttl);
    }

    /**
     * 检查指定的JWT令牌ID (JTI) 是否已被撤销。
     * 会根据当前配置查询Redis或内存映射来判断状态。
     * 对于已过期的内存记录，会自动清理。
     *
     * @param jti 待检查的令牌ID
     * @return 如果令牌已被撤销则返回true，否则返回false
     */
    public boolean isRevoked(final String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }

        final StringRedisTemplate redisTemplate = redis;

        if (!redisEnabled || redisTemplate == null) {
            Instant until = inMemoryRevokedUntil.get(jti);
            if (until == null) {
                return false;
            }
            if (until.isAfter(Instant.now())) {
                return true;
            }
            inMemoryRevokedUntil.remove(jti);
            return false;
        }

        String v = redisTemplate.opsForValue().get(PREFIX + jti);
        return v != null;
    }
}

