package com.blueoptima.ratelimiter.rateannotation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
public final class RateLimiterWebMvcConfigurer implements WebMvcConfigurer {

    private final RateCheckInterceptor rateCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateCheckInterceptor).addPathPatterns("/**").order(Ordered.HIGHEST_PRECEDENCE);
    }
}
