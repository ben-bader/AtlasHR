package com.hrms.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Gateway Route Configuration
 * 
 * Defines routes from gateway to downstream microservices.
 * Routes are configured programmatically for flexibility.
 */
@Configuration
@Slf4j
public class GatewayConfig {

	/**
	 * Route definitions
	 * 
	 * Maps API paths to backend services:
	 * - /api/auth/** → auth-service:8081 (login, register, refresh, user info)
	 * - /api/v1/** → employee-service:8083 (employees, departments, designations, skills, insurance, org-chart)
	 */
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			// Auth Service - handles authentication
			.route("auth-service", r -> r
				.path("/api/auth/**")
				.uri("http://hrms-auth-service:8081"))

			// Employee/HR Service - handles all employee data and organization info
			.route("employee-service", r -> r
				.path("/api/v1/**")
				.uri("http://hrms-employee-service:8083"))

			.build();
	}

}
