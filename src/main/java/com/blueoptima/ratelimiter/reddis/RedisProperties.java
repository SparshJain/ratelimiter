package com.blueoptima.ratelimiter.reddis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "spring.redis-limiter")
@Data
public final class RedisProperties {
    /**
     * Redis server host
     */
    private String redisHost = "127.0.0.1";
    /**
     * Redis service port
     */
    private int redisPort = 6379;
    /**
     * Redis access password
     */
    private String redisPassword = null;
    /**
     * Redis connection timeout
     */
    private int redisConnectionTimeout = 2000;
    /**
     * max idle connections in the pool
     */
    private int redisPoolMaxIdle = 50;
    /**
     * min idle connection in the pool
     */
    private int redisPoolMinIdle = 10;
    /**
     * the max wait milliseconds for borrowing an instance from the pool
     */
    private long redisPoolMaxWaitMillis = -1;
    /**
     * the max total instances in the pool
     */
    private int redisPoolMaxTotal = 200;
    /**
     * the redis key prefix
     */
    private String redisKeyPrefix = "#RL";

    /**
     * check action execution timeout(MILLISECONDS)
     */
    private int checkActionTimeout = 100;

    /**
     * the flag to tell whether rate limiter configuration can change dynamically
     */
    private boolean enableDynamicalConf = false;

    /**
     * channel for pub/sub limiter configuration change event
     */
    private String channel = "#RLConfigChannel";
}
