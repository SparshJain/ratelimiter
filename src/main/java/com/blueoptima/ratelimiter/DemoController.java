package com.blueoptima.ratelimiter;

import com.blueoptima.ratelimiter.rateannotation.Limiter;
import com.blueoptima.ratelimiter.rateannotation.dynamic.DynamicLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/demo")
public class DemoController {
    
    @GetMapping("/test")
    @Limiter(base = "#Headers['userid']", permits = 2, timeUnit = TimeUnit.MINUTES)
    public String test() {
        return "test!";
    }

    @GetMapping("/dynamictest")
    @DynamicLimiter(base = "#Headers['x-real-ip']", permits = 5, timeUnit = TimeUnit.MINUTES)
    public String dynamicTest() {
        return "dynamictest!";
    }
}
