package com.blueoptima.ratelimiter.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultRateExceedingListener implements  RateExceedingListener {
    private static Logger logger = LoggerFactory.getLogger(DefaultRateExceedingListener.class);

    @Override
    public void onApplicationEvent(RateExceedingEvent event) {
        logger.warn("Rate exceeding event arise: " + event.toString());
    }
}
