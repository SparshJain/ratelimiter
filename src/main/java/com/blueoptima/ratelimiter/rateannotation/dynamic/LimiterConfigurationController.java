package com.blueoptima.ratelimiter.rateannotation.dynamic;

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

import com.blueoptima.ratelimiter.rateannotation.RedisProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/limiterconfig")
@RequiredArgsConstructor
public final class LimiterConfigurationController{

    private static Logger logger = LoggerFactory.getLogger(ReddisProcessor.class);

    private final JedisPool jedisPool;

    private final RedisProperties redisLimiterProperties;

    private final ReddisProcessor RedisProcessor;

    @PutMapping
    public void update(@RequestBody LimiterConfig limiterConfig, HttpServletResponse response) throws IOException {
            publish(limiterConfig);
    }

    @GetMapping
    public LimiterConfig get(@RequestParam("controller") String controller, @RequestParam("method")String method) {
        String limiterConfigKey = controller + ":" + method;
        return RedisProcessor.get(limiterConfigKey);
    }

    @DeleteMapping
    public void delete(@RequestParam("controller") String controller, @RequestParam("method")String method) {
        LimiterConfig limiterConfig = new LimiterConfig();
        limiterConfig.setControllerName(controller);
        limiterConfig.setMethodName(method);
        limiterConfig.setDeleted(true);
        publish(limiterConfig);
    }

    private void publish(LimiterConfig limiterConfig) {
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
