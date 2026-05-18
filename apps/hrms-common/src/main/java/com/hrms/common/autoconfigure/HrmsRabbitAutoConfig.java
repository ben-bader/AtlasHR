package com.hrms.common.autoconfigure;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * Auto-configuration for the shared HRMS RabbitMQ exchange, JSON converter, and template.
 *
 * Services keep their own queue and binding @Bean definitions.
 * This auto-config is skipped if a service already defines any of these beans.
 */
@AutoConfiguration
@ConditionalOnClass(RabbitTemplate.class)
public class HrmsRabbitAutoConfig {

    public static final String EXCHANGE_NAME = "hrms.exchange";

    @ConditionalOnMissingBean(TopicExchange.class)
    @org.springframework.context.annotation.Bean
    public TopicExchange hrmsTopicExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @ConditionalOnMissingBean(Jackson2JsonMessageConverter.class)
    @org.springframework.context.annotation.Bean
    public Jackson2JsonMessageConverter hrmsMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @ConditionalOnMissingBean(RabbitTemplate.class)
    @org.springframework.context.annotation.Bean
    public RabbitTemplate hrmsRabbitTemplate(ConnectionFactory connectionFactory,
                                              Jackson2JsonMessageConverter hrmsMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(hrmsMessageConverter);
        return template;
    }
}
