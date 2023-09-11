package com.yupi.springbootinit.mq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AIManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class MQConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AIManager aiManager;

    private static long modelId = 1696869691743600641L;

//    public static void main(String[] args) {
//        try {
//            ConnectionFactory connectionFactory = new ConnectionFactory();
//            connectionFactory.setHost("localhost");
//            Connection connection = connectionFactory.newConnection();
//            Channel channel = connection.createChannel();
//            channel.exchangeDeclare(MqConstant.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
//            channel.queueDeclare(MqConstant.QUEUE_NAME, true, false, false, null);
//            channel.queueBind(MqConstant.QUEUE_NAME, MqConstant.EXCHANGE_NAME, MqConstant.ROUTING_KEY);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//    }

    //模型id
    //        long modelId = 1659171950288818178L;
//    long modelId = 1696869691743600641L;

    @RabbitListener(queues = {MqConstant.QUEUE_NAME}, ackMode = "MANUAL")
    public void reciveMessage(String message, Channel channel, Message messages) {
        Long deliveryTag = messages.getMessageProperties().getDeliveryTag();
        try {
            if (StringUtils.isEmpty(message)) {
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
            }
            Long id = Long.valueOf(message);
            Chart chart = chartService.getById(id);

            if (chart == null) {
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取退表信息失败");
            }

            Chart updateParams = new Chart();
            updateParams.setStatus("running");
            updateParams.setId(chart.getId());
            boolean success = chartService.updateById(updateParams);
            if (!success) {
                setFailed(chart.getId(), "图表状态更新失败");
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "图表状态更新失败");
            }

            StringBuilder userRequest = new StringBuilder();
            userRequest.append("分析需求：").append("\n");
            userRequest.append(chart.getGoal());
            if (StringUtils.isNotBlank(chart.getCharType())) {
                userRequest.append(",请使用").append(chart.getCharType());
            }
            userRequest.append("\n");
            userRequest.append("原始数据：").append("\n").append(chart.getChartDate());
            String result = aiManager.doChart(modelId, userRequest.toString());
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                setFailed(chart.getId(), "AI 生成结果异常");
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成结果异常");
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();
            updateParams = new Chart();
            updateParams.setId(chart.getId());
            updateParams.setGenChart(genChart);
            updateParams.setGenResult(genResult);
            updateParams.setStatus("success");
            success = chartService.updateById(updateParams);
            if (!success) {
                setFailed(chart.getId(), "图表状态更新失败");
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "图表状态更新失败");
            }
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setFailed(Long id, String message) {
        Chart errorParam = new Chart();
        errorParam.setId(id);
        errorParam.setStatus("failed");
        errorParam.setExecMessage(message);
        chartService.updateById(errorParam);
    }

}
