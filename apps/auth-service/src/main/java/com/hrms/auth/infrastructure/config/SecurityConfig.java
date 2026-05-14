package com.hrms.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Security Configuration for Auth-Service
 * 
 * Auth-service is a RESOURCE SERVER that trusts gateway-injected headers.
 * This config enables PasswordEncoder for password hashing.
 * JWT validation is done by gateway (JwtAuthenticationFilter).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig {

	/**
	 * Password encoder bean for user authentication
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Authentication manager for login endpoint
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Security filter chain - stateless (no session needed)
	 * 
	 * Auth-service doesn't handle JWT validation.
	 * Gateway validates JWT and injects X-User-* headers.
	 * This service trusts those headers (no additional auth checks).
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// Stateless - no session cookies
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
			// Disable CSRF (stateless API)
			.csrf(csrf -> csrf.disable())
			
			// Public endpoints
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/login").permitAll()
				.requestMatchers("/api/auth/register").permitAll()
				.requestMatchers("/api/auth/refresh").permitAll()
				.requestMatchers("/actuator/health").permitAll()
				.requestMatchers("/actuator/health/**").permitAll()
				.requestMatchers("/api/**").permitAll()
				.anyRequest().authenticated());

		return http.build();
	}
}
