package com.blueoptima.ratelimiter.listener;

import org.springframework.context.ApplicationListener;

public interface RateCheckFailureListener extends ApplicationListener<RateCheckFailureEvent> {

}
