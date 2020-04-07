package com.blueoptima.ratelimiter.rateannotation.dynamic;

import lombok.Data;

@Data
public final class LimiterConfig {
    private String controllerName;
    private String methodName;
    private String baseExp;
    private String path;
    private String timeUnit;
    private int permits;
    private boolean deleted;
}

