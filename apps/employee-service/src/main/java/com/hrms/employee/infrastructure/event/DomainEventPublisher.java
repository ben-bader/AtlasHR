package com.hrms.employee.infrastructure.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class DomainEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EMPLOYEE_EXCHANGE = "hrms.employee.exchange";
    private static final String EMPLOYEE_CREATED_ROUTING_KEY = "employee.created";
    private static final String EMPLOYEE_UPDATED_ROUTING_KEY = "employee.updated";
    private static final String EMPLOYEE_TRANSFERRED_ROUTING_KEY = "employee.transferred";
    private static final String EMPLOYEE_TERMINATED_ROUTING_KEY = "employee.terminated";

    public DomainEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEmployeeCreatedEvent(EmployeeCreatedEvent event) {
        event.setEventTimestamp(LocalDateTime.now());
        rabbitTemplate.convertAndSend(EMPLOYEE_EXCHANGE, EMPLOYEE_CREATED_ROUTING_KEY, event);
    }

    public void publishEmployeeUpdatedEvent(EmployeeUpdatedEvent event) {
        event.setEventTimestamp(LocalDateTime.now());
        rabbitTemplate.convertAndSend(EMPLOYEE_EXCHANGE, EMPLOYEE_UPDATED_ROUTING_KEY, event);
    }

    public void publishEmployeeTransferredEvent(EmployeeTransferredEvent event) {
        event.setEventTimestamp(LocalDateTime.now());
        rabbitTemplate.convertAndSend(EMPLOYEE_EXCHANGE, EMPLOYEE_TRANSFERRED_ROUTING_KEY, event);
    }

    public void publishEmployeeTerminatedEvent(EmployeeTerminatedEvent event) {
        event.setEventTimestamp(LocalDateTime.now());
        rabbitTemplate.convertAndSend(EMPLOYEE_EXCHANGE, EMPLOYEE_TERMINATED_ROUTING_KEY, event);
    }

}
