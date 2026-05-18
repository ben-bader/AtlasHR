package com.hrms.employee.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application Configuration
 * 
 * Configures application-level beans and components
 */
@Configuration
public class ApplicationConfig {

    /**
     * RestTemplate bean for HTTP client communication
     * Used by AuthServiceClient to communicate with Auth Service
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * ObjectMapper bean for JSON serialization/deserialization
     * Used by AuthServiceClient for request/response mapping
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
