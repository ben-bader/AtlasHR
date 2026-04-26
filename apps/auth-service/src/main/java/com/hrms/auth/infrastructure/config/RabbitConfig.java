package main.java.com.hrms.auth.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;

public class RabbitConfig {
      public static final String EXCHANGE = "hrms.exchange";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }
}
