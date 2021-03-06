package com.blueoptima.ratelimiter.reddis;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.blueoptima.ratelimiter.common.SpecificConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

@RequiredArgsConstructor
public final class ReddisProcessor extends JedisPubSub implements BeanPostProcessor, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(ReddisProcessor.class);

    private final RedisProperties redisLimiterProperties;

    private ConcurrentHashMap<String, SpecificConfiguration> configMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet(){
        SubThread subThread = new SubThread();
        subThread.start();
        WatcherThread watcherThread = new WatcherThread(subThread);
        watcherThread.start();
    }

    class SubThread extends Thread {
        boolean mistaken = false;
        @Override
        public void run() {
            Jedis jedis = null;
            try {
                jedis = new Jedis(redisLimiterProperties.getRedisHost(), redisLimiterProperties.getRedisPort(), 0);
                if(redisLimiterProperties.getRedisPassword() != null) {
                    jedis.auth(redisLimiterProperties.getRedisPassword());
                }
                jedis.subscribe(ReddisProcessor.this, redisLimiterProperties.getChannel());
            }
            catch (JedisConnectionException e) {
                mistaken = true;
            }
            finally {
                if(jedis != null) {
                    jedis.close();
                }

            }
        }
        public boolean isMistaken() {
            return mistaken;
        }
    }

    class WatcherThread extends Thread {
        SubThread subThread;
        WatcherThread(SubThread subThread) {
            this.subThread = subThread;
        }
        public void run() {
            while(true) {
                try {
                    sleep(5000);
                }
                catch(InterruptedException e) {
                }
                if(subThread.isMistaken()) {
                    subThread = new SubThread();
                    subThread.start();
                }
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void onMessage(String channel, String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        SpecificConfiguration config = null;
        try {
            config = objectMapper.readValue(message, SpecificConfiguration.class);
        }
        catch(IOException e) {
            logger.error("read config from message failed. the message content is " + message);
        }
        if(config != null) {
                String key = config.getControllerName() + ":" + config.getMethodName()+ ":" + config.getUserId();
                synchronized(this) {
                    if (config.isDeleted()) {
                        configMap.remove(key);
                    } else {
                        configMap.put(key, config);
                    }
                }
        }
    }

    public synchronized SpecificConfiguration get(String key) {
        return configMap.get(key);
    }

}
