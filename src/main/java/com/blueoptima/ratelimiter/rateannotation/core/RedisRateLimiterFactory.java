package com.blueoptima.ratelimiter.rateannotation.core;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public final class RedisRateLimiterFactory {

    private final JedisPool jedisPool;

    private final Cache<TimeUnit, RedisRateLimiter> redisRateLimiterCache =
            Caffeine.newBuilder().maximumSize(10).build();

    public RedisRateLimiter get(final TimeUnit timeUnit) {
        RedisRateLimiter redisRateLimiter = redisRateLimiterCache.getIfPresent(timeUnit);
        if(redisRateLimiter == null) {
            synchronized (RedisRateLimiterFactory.class) {
                redisRateLimiter = redisRateLimiterCache.getIfPresent(timeUnit);
                if(redisRateLimiter == null) {
                    redisRateLimiter = new RedisRateLimiter(jedisPool, timeUnit);
                    redisRateLimiterCache.put(timeUnit, redisRateLimiter);
                }
            }
        }
        return redisRateLimiter;
    }
}
