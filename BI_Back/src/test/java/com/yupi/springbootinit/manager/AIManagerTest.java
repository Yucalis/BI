package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AIManagerTest {

    @Autowired
    private AIManager aiManager;

    @Autowired
    private ChartService chartService;

    @Test
    void doChart() {
        Chart chart = new Chart();
        chart.setId(1701577397676687361L);
        chart.setStatus("success");
        chartService.updateById(chart);
    }
}