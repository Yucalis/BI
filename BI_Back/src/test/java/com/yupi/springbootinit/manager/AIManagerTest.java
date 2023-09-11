package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AIManagerTest {

    @Autowired
    private AIManager aiManager;

//    @Test
//    void doChart() {
//        String res = aiManager.doChart("周杰伦");
//        System.out.println(res);
//    }
}