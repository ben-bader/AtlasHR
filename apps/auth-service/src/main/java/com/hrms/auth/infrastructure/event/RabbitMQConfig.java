package com.hrms.auth.infrastructure.event;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "hrms.exchange";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";

    @Bean
    public TopicExchange hrmTopicExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }
}
