package com.blueoptima.ratelimiter.rateannotation.event;

import org.springframework.context.ApplicationListener;

public interface RateExceedingListener extends ApplicationListener<RateExceedingEvent> {
}
