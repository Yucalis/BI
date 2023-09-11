package com.yupi.springbootinit.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return new Queue(MqConstant.QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingQueue() {
        return BindingBuilder.bind(queue())
                .to(directExchange())
                .with(MqConstant.ROUTING_KEY);
    }

}
