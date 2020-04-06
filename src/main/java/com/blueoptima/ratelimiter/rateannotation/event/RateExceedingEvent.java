package com.blueoptima.ratelimiter.rateannotation.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public final class RateExceedingEvent extends ApplicationEvent {
    private static Object dummy = new Object();
    private String applicationName;
    private String controllerName;
    private String methodName;
    private String baseExp;
    private String baseValue;
    private String path;
    private String timeUnit;
    private int permits;
    public RateExceedingEvent() {
        super(dummy);
    }
}
