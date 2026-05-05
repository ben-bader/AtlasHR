package com.hrms.auth.infrastructure.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishUserCreatedEvent(UserEvent event) {
        log.info("Publishing user.created event for userId: {}", event.getUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.USER_CREATED_ROUTING_KEY,
                event
        );
    }

    public void publishUserDeletedEvent(UserEvent event) {
        log.info("Publishing user.deleted event for userId: {}", event.getUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                event
        );
    }
}
