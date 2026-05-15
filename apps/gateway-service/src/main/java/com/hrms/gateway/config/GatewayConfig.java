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
	 * - /api/** → employee-service:8083 (employees, departments, designations, skills, insurance, org-chart)
	 */
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			// Auth Service - handles authentication
			.route("auth-service", r -> r
				.path("/api/auth/**")
				.uri("http://hrms-auth-service:8081"))

			// Attendance Service - handles attendance, device attendance and daily attendance APIs
			.route("attendance-service", r -> r
				.path(
					"/api/attendance/**",
					"/api/device-attendance/**",
					"/api/daily-attendance/**"
				)
				.uri("http://hrms-attendance-service:8085"))

			// Performance Service - handles performance management APIs
			.route("performance-service", r -> r
				.path("/api/performance/**")
				.uri("http://hrms-performance-service:8086"))

			// Employee/HR Service - handles employee and org data
			.route("employee-service", r -> r
				.path(
					"/api/employees/**",
					"/api/departments/**",
					"/api/designations/**",
					"/api/organization-chart/**",
				    "/api/employees/insurances/**",
				    "/api/skills/**"
			)
			.uri("http://hrms-employee-service:8083"))
		.build();
	}
}
