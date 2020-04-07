package com.blueoptima.ratelimiter.interceptor;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RateLimiterInterceptorConfigurer implements WebMvcConfigurer {

    private final RateLimiterInterceptor rateCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateCheckInterceptor).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
    }
}
