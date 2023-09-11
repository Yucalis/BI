package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.ThrowUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonManagerTest {

    @Resource
    private RedissonManager redissonManager;

    @Test
    void doRateLimit() {
        String key = "1";
        for (int i = 0; i < 5; i++) {
            boolean isAccess = redissonManager.doRateLimit(key);
            ThrowUtils.throwIf(!isAccess, ErrorCode.FORBIDDEN_ERROR, "请求过于频繁");
            System.out.println("哈哈哈哈");
        }

    }
}