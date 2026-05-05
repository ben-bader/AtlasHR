package com.hrms.employee.infrastructure.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserEventListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {} for user: {}", event.getEventType(), event.getUsername());

        if ("user.created".equals(event.getEventType())) {
            handleUserCreated(event);
        } else if ("user.deleted".equals(event.getEventType())) {
            handleUserDeleted(event);
        }
    }

    private void handleUserCreated(UserEvent event) {
        log.info("Processing user.created event - Creating employee profile for user: {}", event.getUserId());
        // TODO: Create employee profile in database
        // This would typically involve calling EmployeeService to create a new employee record
        // linked to the userId from the auth service
    }

    private void handleUserDeleted(UserEvent event) {
        log.info("Processing user.deleted event - Deactivating employee for user: {}", event.getUserId());
        // TODO: Deactivate or delete employee profile
        // This would typically involve calling EmployeeService to mark employee as inactive
    }
}
