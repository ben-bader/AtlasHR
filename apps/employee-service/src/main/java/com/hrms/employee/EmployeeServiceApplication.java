package com.hrms.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Employee Service Main Application
 * 
 * Microservice for managing employee lifecycle, departments, designations, and skills
 * Uses PostgreSQL for persistence and RabbitMQ for event publishing
 * Integrates with API Gateway via header-based authentication
 */
@SpringBootApplication(scanBasePackages = {
	"com.hrms.employee"
})
@EnableJpaRepositories(basePackages = "com.hrms.employee.domain.repository")
@ComponentScan(basePackages = {
	"com.hrms.employee.infrastructure",
	"com.hrms.employee.application",
	"com.hrms.employee.domain",
	"com.hrms.employee.presentation",
	"com.hrms.employee.common"
})
public class EmployeeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeServiceApplication.class, args);
	}

}
