package com.hrms.auth.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "hrms.exchange";
    public static final String AUTH_QUEUE = "auth.queue";
    public static final String AUTH_ROUTING_KEY = "auth.#";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(AUTH_QUEUE, true, false, false);
    }

    @Bean
    public Binding binding(Queue authQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(authQueue)
                .to(topicExchange)
                .with(AUTH_ROUTING_KEY);
    }
}
