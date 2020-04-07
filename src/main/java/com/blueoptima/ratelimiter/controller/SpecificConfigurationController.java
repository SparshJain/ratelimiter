package com.blueoptima.ratelimiter.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blueoptima.ratelimiter.common.SpecificConfiguration;
import com.blueoptima.ratelimiter.reddis.ReddisProcessor;
import com.blueoptima.ratelimiter.reddis.RedisProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/specificConfig")
@RequiredArgsConstructor
public final class SpecificConfigurationController{
    private static Logger logger = LoggerFactory.getLogger(ReddisProcessor.class);
    private final JedisPool jedisPool;
    private final RedisProperties redisLimiterProperties;
    private final ReddisProcessor RedisProcessor;

    @PutMapping
    public void update(@RequestBody SpecificConfiguration limiterConfig, HttpServletResponse response) throws IOException {
            publish(limiterConfig);
    }

    @GetMapping
    public SpecificConfiguration get(@RequestParam("controller") String controller, @RequestParam("method")String method) {
        String limiterConfigKey = controller + ":" + method;
        return RedisProcessor.get(limiterConfigKey);
    }

    @DeleteMapping
    public void delete(@RequestParam("controller") String controller, @RequestParam("method")String method) {
        SpecificConfiguration limiterConfig = new SpecificConfiguration();
        limiterConfig.setControllerName(controller);
        limiterConfig.setMethodName(method);
        limiterConfig.setDeleted(true);
        publish(limiterConfig);
    }

    private void publish(SpecificConfiguration limiterConfig) {
        ObjectMapper objectMapper = new ObjectMapper();
        String configMessage = null;
        try {
            configMessage = objectMapper.writeValueAsString(limiterConfig);
        }
        catch(IOException e) {
            logger.error("convert LimiterConfig object to json failed.");
        }
        Jedis jedis = jedisPool.getResource();
        jedis.publish(redisLimiterProperties.getChannel(), configMessage);
    }

}
