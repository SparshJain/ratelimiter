package com.blueoptima.ratelimiter.listener;

import org.springframework.context.ApplicationListener;

public interface RateExceedingListener extends ApplicationListener<RateExceedingEvent> {
}
