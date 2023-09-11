package com.yupi.springbootinit.mq;

import lombok.Data;

public interface MqConstant {

    String QUEUE_NAME = "bi_queue";

    String EXCHANGE_NAME = "bi_exchange";

    String ROUTING_KEY = "bi_routingKey";

}
