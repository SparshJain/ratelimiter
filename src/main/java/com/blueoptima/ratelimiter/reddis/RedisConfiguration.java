package com.blueoptima.ratelimiter.reddis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blueoptima.ratelimiter.common.RateCheckCallableTask;
import com.blueoptima.ratelimiter.controller.SpecificConfigurationController;
import com.blueoptima.ratelimiter.interceptor.RateLimiterInterceptor;
import com.blueoptima.ratelimiter.interceptor.RateLimiterInterceptorConfigurer;
import com.blueoptima.ratelimiter.listener.DefaultRateCheckFailureListener;
import com.blueoptima.ratelimiter.listener.DefaultRateExceedingListener;
import com.blueoptima.ratelimiter.listener.RateCheckFailureListener;
import com.blueoptima.ratelimiter.listener.RateExceedingListener;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration {

    @Autowired
    private RedisProperties redisLimiterProperties;

    @Bean
    @ConditionalOnMissingBean(JedisPool.class)
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisLimiterProperties.getRedisPoolMaxIdle());
        jedisPoolConfig.setMinIdle(redisLimiterProperties.getRedisPoolMinIdle());
        jedisPoolConfig.setMaxWaitMillis(redisLimiterProperties.getRedisPoolMaxWaitMillis());
        jedisPoolConfig.setMaxTotal(redisLimiterProperties.getRedisPoolMaxTotal());
        jedisPoolConfig.setTestOnBorrow(true);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisLimiterProperties.getRedisHost(), redisLimiterProperties.getRedisPort(), redisLimiterProperties.getRedisConnectionTimeout(), redisLimiterProperties.getRedisPassword());
        return jedisPool;
    }

    @Bean
    @ConditionalOnMissingBean(RedisRateFactory.class)
    public RedisRateFactory redisRateLimiterFactory() {
    	RedisRateFactory redisRateLimiterFactory = new RedisRateFactory(jedisPool());
        return redisRateLimiterFactory;
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiterInterceptor.class)
    public RateLimiterInterceptor rateLimiterInterceptor() {
        RateLimiterInterceptor rateLimiterInterceptor;
        if (redisLimiterProperties.isEnableDynamicalConf()) {
            rateLimiterInterceptor = new RateLimiterInterceptor(redisLimiterProperties, rateCheckTaskRunner(), redisLimiterConfigProcessor());
        } else {
            rateLimiterInterceptor = new RateLimiterInterceptor(redisLimiterProperties, rateCheckTaskRunner(),null);
        }
        return rateLimiterInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiterInterceptorConfigurer.class)
    public RateLimiterInterceptorConfigurer rateLimiterWebMvcConfigurer() {
        RateLimiterInterceptorConfigurer rateLimiterWebMvcConfigurer = new RateLimiterInterceptorConfigurer(rateLimiterInterceptor());
        return rateLimiterWebMvcConfigurer;
    }

    @Bean
    @ConditionalOnMissingBean(RateCheckCallableTask.class)
    public RateCheckCallableTask rateCheckTaskRunner() {
    	RateCheckCallableTask rateCheckTaskRunner = new RateCheckCallableTask(redisRateLimiterFactory(), redisLimiterProperties);
        return rateCheckTaskRunner;
    }

    @Bean
    @ConditionalOnMissingBean(RateCheckFailureListener.class)
    public RateCheckFailureListener rateCheckFailureListener() {
        RateCheckFailureListener rateCheckFailureListener = new DefaultRateCheckFailureListener();
        return rateCheckFailureListener;
    }

    @Bean
    @ConditionalOnMissingBean(RateExceedingListener.class)
    public RateExceedingListener rateExceedingListener() {
        RateExceedingListener rateExceedingListener = new DefaultRateExceedingListener();
        return rateExceedingListener;
    }

    @Bean
    @ConditionalOnMissingBean(ReddisProcessor.class)
    @ConditionalOnProperty(prefix = "spring.redis-limiter", name = "enable-dynamical-conf", havingValue = "true")
    public ReddisProcessor redisLimiterConfigProcessor() {
    	ReddisProcessor redisLimiterConfigProcessor = new ReddisProcessor(redisLimiterProperties);
        return redisLimiterConfigProcessor;
    }

    @Bean
    @ConditionalOnMissingBean(SpecificConfigurationController.class)
    @ConditionalOnProperty(prefix = "spring.redis-limiter", name = "enable-dynamical-conf", havingValue = "true")
    public SpecificConfigurationController limiterConfigResource() {
    	SpecificConfigurationController limiterConfigResource = new SpecificConfigurationController(jedisPool(), redisLimiterProperties, redisLimiterConfigProcessor());
        return limiterConfigResource;
    }


}
