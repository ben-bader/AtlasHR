package com.hrms.auth.infrastructure.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.hrms.auth.infrastructure.config.RabbitConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate = new RabbitTemplate();

    public void publishUserCreated(UserEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "auth.user.created",
                event
        );
    }

    public void publishUserDeleted(UserEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                "auth.user.deleted",
                event
        );
    }
}