package com.blueoptima.ratelimiter.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blueoptima.ratelimiter.annotations.SpecificLimit;

@RestController
@RequestMapping("/specific")
public class SpecificLimitController {
    
    @GetMapping("/developers")
    @SpecificLimit(base = "#Headers['userid']", permits = 2, timeUnit = TimeUnit.MINUTES)
    public String developerAPI() {
        return "Developers API";
    }
    
    @GetMapping("/organisations")
    @SpecificLimit(base = "#Headers['userid']", permits = 2, timeUnit = TimeUnit.MINUTES)
    public String organisationAPI() {
        return "Organisations API";
    }
	
}
