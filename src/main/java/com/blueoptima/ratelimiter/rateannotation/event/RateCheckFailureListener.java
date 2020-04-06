package com.blueoptima.ratelimiter.rateannotation.event;

import org.springframework.context.ApplicationListener;

public interface RateCheckFailureListener extends ApplicationListener<RateCheckFailureEvent> {

}
