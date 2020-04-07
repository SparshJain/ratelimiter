package com.blueoptima.ratelimiter.common;

import lombok.Data;

@Data
public final class SpecificConfiguration {
    private String userId;
    private String controllerName;
    private String methodName;
    private String baseExp;
    private String path;
    private String timeUnit;
    private int permits;
    private boolean deleted;
}

