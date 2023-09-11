package com.yupi.springbootinit.model.vo;

import lombok.Data;

/**
 * AI根据用户请求生成的BI结果
 */
@Data
public class BiResponse {

    /**
     * 生成的图表的前端代码
     */
    private String genChart;

    /**
     * 生成的结论
     */
    private String genResult;

    /**
     * 生成的图表的id
     */
    private Long chartId;

}
