package com.blueoptima.ratelimiter.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.blueoptima.ratelimiter.listener.RateCheckFailureEvent;
import com.blueoptima.ratelimiter.reddis.RedisProperties;
import com.blueoptima.ratelimiter.reddis.RedisRateFactory;
import com.blueoptima.ratelimiter.reddis.RedisRateLimiter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RateCheckCallableTask implements ApplicationContextAware {
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final RedisRateFactory redisRateLimiterFactory;

    private final RedisProperties redisLimiterProperties;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean checkRun(String rateLimiterKey, TimeUnit timeUnit, int permits) {
        CheckTask task = new CheckTask(rateLimiterKey, timeUnit, permits);
        Future<Boolean> checkResult = executorService.submit(task);
        boolean retVal = true;
        try {
            retVal = checkResult.get(redisLimiterProperties.getCheckActionTimeout(), TimeUnit.MILLISECONDS);
        }
        catch(Exception e) {
            applicationContext.publishEvent(new RateCheckFailureEvent(e, "Access rate check task executed failed."));
        }
        return retVal;
    }

    class CheckTask implements Callable<Boolean> {
        private String rateLimiterKey;
        private TimeUnit timeUnit;
        private int permits;
        CheckTask(String rateLimiterKey, TimeUnit timeUnit, int permits) {
            this.rateLimiterKey = rateLimiterKey;
            this.timeUnit = timeUnit;
            this.permits = permits;
        }
        public Boolean call() {
            RedisRateLimiter redisRatelimiter = redisRateLimiterFactory.get(timeUnit);
            return redisRatelimiter.acquire(rateLimiterKey, permits);
        }
    }
}
