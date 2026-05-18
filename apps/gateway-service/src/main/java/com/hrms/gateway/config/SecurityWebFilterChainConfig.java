package com.hrms.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring Security Configuration for API Gateway
 * 
 * Centralized security and CORS handling at gateway level.
 * Microservices trust gateway-injected headers.
 * 
 * Security responsibilities:
 * - CORS configuration (browser preflight)
 * - JWT validation via JwtAuthenticationFilter
 * - Allow public routes: /api/auth/**
 * - Require authentication for protected routes
 * - CSRF disabled (stateless API)
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityWebFilterChainConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityWebFilterChainConfig.class);

	/**
	 * Main security filter chain for WebFlux
	 * 
	 * FIXED (BUG #4): Changed from .anyExchange().authenticated() to .permitAll()
	 * FIXED (BUG #8): Added CORS filter explicitly with .and().cors()
	 * 
	 * Reason: JwtAuthenticationFilter (order -1 global filter) is the REAL security layer.
	 * Spring Security's .authenticated() runs at order 0 BEFORE routing happens.
	 * Two-layer auth causes conflicts and 401 errors even with valid JWT.
	 * 
	 * For CORS: CorsWebFilter (Spring's WebFilter) needs to run BEFORE security filters
	 * to properly intercept and respond to preflight OPTIONS requests.
	 * 
	 * Order of execution:
	 * 1. CorsWebFilter intercepts OPTIONS preflight and responds with CORS headers
	 * 2. JwtAuthenticationFilter validates JWT (global filter at order -1)
	 * 3. SecurityWebFilterChain permits all (just auth checks)
	 * 4. Request routed to downstream service
	 * 5. Microservices trust X-User-* headers injected by gateway
	 */
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
			.formLogin(ServerHttpSecurity.FormLoginSpec::disable)

			.csrf(csrf -> csrf.disable())

			.authorizeExchange(auth -> auth
				.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.anyExchange().permitAll()); // JwtAuthenticationFilter handles auth

		return http.build();
	}

	/**
	 * Explicit CORS Web Filter bean
	 * 
	 * Ensures CORS runs at WebFilter level (order before -1) rather than 
	 * relying on SecurityWebFilterChain's cors() config alone.
	 * This guarantees preflight OPTIONS requests are handled correctly.
	 */
	@Bean
	public org.springframework.web.cors.reactive.CorsWebFilter corsWebFilter() {
		return new org.springframework.web.cors.reactive.CorsWebFilter(corsConfigurationSource());
	}

	/**
	 * CORS Configuration Source
	 * 
	 * Applied globally to all routes via SecurityWebFilterChain.
	 * Handles browser preflight OPTIONS requests.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		
		// Allow all origins in dev, restrict in production
		config.setAllowedOrigins(java.util.Arrays.asList(
			"http://localhost:3000",      // Frontend dev
			"http://localhost:3001",      // Frontend alternate
			"http://127.0.0.1:3000",      // Loopback
			"http://127.0.0.1:3001"
		));
		
		// Allow all HTTP methods
		config.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		
		// Allow all headers
		config.setAllowedHeaders(java.util.Arrays.asList("*"));
		
		// Allow credentials (cookies, auth headers)
		config.setAllowCredentials(true);
		
		// Cache preflight response for 1 hour
		config.setMaxAge(3600L);
		
		// Expose headers that client needs
		config.setExposedHeaders(java.util.Arrays.asList(
			"Authorization",
			"Content-Type",
			"X-User-Id",
			"X-Username",
			"X-User-Roles"
		));
		
		// Apply to all paths
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		log.info("CORS configured for gateway - allows all methods, credentials enabled");
		return source;
	}

}
