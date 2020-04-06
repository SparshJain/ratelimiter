package com.blueoptima.ratelimiter.rateannotation.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultRateCheckFailureListener implements RateCheckFailureListener{

    private static Logger logger = LoggerFactory.getLogger(DefaultRateCheckFailureListener.class);
    @Override
    public void onApplicationEvent(RateCheckFailureEvent event) {
        logger.warn(event.getMsg());
    }
}
