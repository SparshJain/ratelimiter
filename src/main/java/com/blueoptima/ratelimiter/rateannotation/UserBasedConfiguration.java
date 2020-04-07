package com.blueoptima.ratelimiter.rateannotation;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties("user-based-permits")
public class UserBasedConfiguration {
	
    private final Map<String, String> userBasedPermits = new HashMap<>();
    
    public Map<String, String> getUserBasedPermits() {
        return userBasedPermits;
    }
}