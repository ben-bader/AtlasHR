package com.hrms.attendance_service.infrastructure.event;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Attendance-service queue and binding definitions.
 * The shared exchange, JSON converter, and RabbitTemplate are provided by hrms-common.
 */
@Configuration
public class RabbitMQConfig {

    public static final String ATTENDANCE_QUEUE   = "attendance.queue";
    public static final String ATTENDANCE_CHECKIN  = "attendance.checkin";
    public static final String ATTENDANCE_CHECKOUT = "attendance.checkout";
    public static final String ATTENDANCE_CREATED  = "attendance.created";
    public static final String ATTENDANCE_DELETED  = "attendance.deleted";

    @Bean
    public Queue attendanceQueue() {
        return new Queue(ATTENDANCE_QUEUE, true);
    }

    @Bean
    public Binding bindCheckIn(Queue attendanceQueue, TopicExchange hrmsTopicExchange) {
        return BindingBuilder.bind(attendanceQueue).to(hrmsTopicExchange).with(ATTENDANCE_CHECKIN);
    }

    @Bean
    public Binding bindCheckOut(Queue attendanceQueue, TopicExchange hrmsTopicExchange) {
        return BindingBuilder.bind(attendanceQueue).to(hrmsTopicExchange).with(ATTENDANCE_CHECKOUT);
    }
}
