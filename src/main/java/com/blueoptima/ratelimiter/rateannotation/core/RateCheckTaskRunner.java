package com.blueoptima.ratelimiter.rateannotation.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.blueoptima.ratelimiter.rateannotation.RedisLimiterProperties;
import com.blueoptima.ratelimiter.rateannotation.event.RateCheckFailureEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RateCheckTaskRunner implements ApplicationContextAware {
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final RedisRateLimiterFactory redisRateLimiterFactory;

    private final RedisLimiterProperties redisLimiterProperties;

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
