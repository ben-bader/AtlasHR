package com.hrms.auth.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "hrms.exchange";
    public static final String AUTH_QUEUE = "auth.queue";
    public static final String AUTH_ROUTING_KEY = "auth.#";

    // ===== Exchange =====
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    // ===== Queue =====
    @Bean
    public Queue authQueue() {
        return new Queue(AUTH_QUEUE, true);
    }

    // ===== Binding =====
    @Bean
    public Binding binding(Queue authQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(authQueue)
                .to(topicExchange)
                .with(AUTH_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Tell RabbitTemplate to use JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}