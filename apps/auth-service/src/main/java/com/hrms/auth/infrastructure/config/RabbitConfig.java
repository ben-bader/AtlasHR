package com.hrms.auth.infrastructure.config;

import com.hrms.common.autoconfigure.HrmsRabbitAutoConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auth-service queue and binding definitions.
 * The shared exchange, JSON converter, and RabbitTemplate are provided by hrms-common.
 */
@Configuration
public class RabbitConfig {

    public static final String AUTH_QUEUE = "auth.queue";
    public static final String AUTH_ROUTING_KEY = "auth.#";

    @Bean
    public Queue authQueue() {
        return new Queue(AUTH_QUEUE, true);
    }

    @Bean
    public Binding authBinding(Queue authQueue, TopicExchange hrmsTopicExchange) {
        return BindingBuilder.bind(authQueue)
                .to(hrmsTopicExchange)
                .with(AUTH_ROUTING_KEY);
    }
}
