package com.blueoptima.ratelimiter.common;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties("user-based-permits")
@Getter
public class PermitsPerUserIdConfiguration {
	
    private final Map<String, String> userid = new HashMap<>();

}