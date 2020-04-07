package com.blueoptima.ratelimiter.rateannotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blueoptima.ratelimiter.rateannotation.core.RateCheckCallableTask;
import com.blueoptima.ratelimiter.rateannotation.core.RedisRateFactory;
import com.blueoptima.ratelimiter.rateannotation.dynamic.LimiterConfig;
import com.blueoptima.ratelimiter.rateannotation.dynamic.LimiterConfigurationController;
import com.blueoptima.ratelimiter.rateannotation.dynamic.ReddisProcessor;
import com.blueoptima.ratelimiter.rateannotation.event.DefaultRateCheckFailureListener;
import com.blueoptima.ratelimiter.rateannotation.event.DefaultRateExceedingListener;
import com.blueoptima.ratelimiter.rateannotation.event.RateCheckFailureListener;
import com.blueoptima.ratelimiter.rateannotation.event.RateExceedingListener;
import com.blueoptima.ratelimiter.rateannotation.web.RateCheckInterceptor;
import com.blueoptima.ratelimiter.rateannotation.web.RateLimiterWebMvcConfigurer;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisLimiterConfiguration {

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
    @ConditionalOnMissingBean(RateCheckInterceptor.class)
    public RateCheckInterceptor rateCheckInterceptor() {
        RateCheckInterceptor rateCheckInterceptor;
        if (redisLimiterProperties.isEnableDynamicalConf()) {
            rateCheckInterceptor = new RateCheckInterceptor(redisLimiterProperties, rateCheckTaskRunner(), redisLimiterConfigProcessor());
        } else {
            rateCheckInterceptor = new RateCheckInterceptor(redisLimiterProperties, rateCheckTaskRunner(),null);
        }
        return rateCheckInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiterWebMvcConfigurer.class)
    public RateLimiterWebMvcConfigurer rateLimiterWebMvcConfigurer() {
        RateLimiterWebMvcConfigurer rateLimiterWebMvcConfigurer = new RateLimiterWebMvcConfigurer(rateCheckInterceptor());
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
    @ConditionalOnMissingBean(LimiterConfigurationController.class)
    @ConditionalOnProperty(prefix = "spring.redis-limiter", name = "enable-dynamical-conf", havingValue = "true")
    public LimiterConfigurationController limiterConfigResource() {
    	LimiterConfigurationController limiterConfigResource = new LimiterConfigurationController(jedisPool(), redisLimiterProperties, redisLimiterConfigProcessor());
        return limiterConfigResource;
    }


}
