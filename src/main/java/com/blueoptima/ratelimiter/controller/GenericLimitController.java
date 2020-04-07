package com.blueoptima.ratelimiter.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blueoptima.ratelimiter.annotations.GenericLimit;

@RestController
@RequestMapping("/generic")
public class GenericLimitController {
    
    @GetMapping("/developers")
    @GenericLimit(base = "#Headers['userid']", permits = 2, timeUnit = TimeUnit.MINUTES)
    public String DeveloperAPI() {
        return "Developers API";
    }
    
    @GetMapping("/organisations")
    @GenericLimit(base = "#Headers['userid']", permits = 2, timeUnit = TimeUnit.MINUTES)
    public String organisationAPI() {
        return "Organisations API";
    }

}
