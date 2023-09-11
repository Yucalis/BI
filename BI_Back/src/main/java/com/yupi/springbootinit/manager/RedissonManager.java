package com.yupi.springbootinit.manager;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RedissonManager {

    @Resource
    private RedissonClient redissonClient;

    public boolean doRateLimit(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //限流方式： 令牌限流
        //表示1秒内只产生2个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 请求消耗1个令牌
        return rateLimiter.tryAcquire(1);
    }

}
