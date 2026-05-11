package com.hrms.attendance_service.infrastructure.event;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "hrms.exchange";

    public static final String ATTENDANCE_QUEUE = "attendance.queue";

    public static final String ATTENDANCE_CHECKIN = "attendance.checkin";
    public static final String ATTENDANCE_CHECKOUT = "attendance.checkout";
    public static final String ATTENDANCE_CREATED = "attendance.created";
    public static final String ATTENDANCE_DELETED = "attendance.deleted";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue attendanceQueue() {
        return new Queue(ATTENDANCE_QUEUE, true);
    }

    @Bean
    public Binding bindCheckIn(Queue attendanceQueue, TopicExchange exchange) {
        return BindingBuilder.bind(attendanceQueue)
                .to(exchange)
                .with(ATTENDANCE_CHECKIN);
    }

    @Bean
    public Binding bindCheckOut(Queue attendanceQueue, TopicExchange exchange) {
        return BindingBuilder.bind(attendanceQueue)
                .to(exchange)
                .with(ATTENDANCE_CHECKOUT);
    }
}