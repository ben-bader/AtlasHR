package com.hrms.employee.infrastructure.event;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "hrms.exchange";
    public static final String QUEUE_NAME = "employee.user.queue";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";

    @Bean
    public TopicExchange hrmTopicExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue employeeUserQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding bindUserCreatedQueue(Queue employeeUserQueue, TopicExchange hrmTopicExchange) {
        return BindingBuilder.bind(employeeUserQueue)
                .to(hrmTopicExchange)
                .with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding bindUserDeletedQueue(Queue employeeUserQueue, TopicExchange hrmTopicExchange) {
        return BindingBuilder.bind(employeeUserQueue)
                .to(hrmTopicExchange)
                .with(USER_DELETED_ROUTING_KEY);
    }
}
