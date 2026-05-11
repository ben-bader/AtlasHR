package com.hrms.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

/**
 * JWT Authentication Filter
 * 
 * GlobalFilter for Gateway request processing.
 * Validates JWT tokens and injects user context headers.
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

	@Autowired
	private JwtUtil jwtUtil;

	// Public routes that don't require authentication
	private static final String[] PUBLIC_ROUTES = {
		"/api/auth/login",
		"/api/auth/register",
		"/api/auth/refresh",
		"/health",
		"/actuator"
	};

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getPath().value();

		// Skip filter for public routes and preflight requests
		if (isPublicRoute(path) || exchange.getRequest().getMethod().toString().equals("OPTIONS")) {
			return chain.filter(exchange);
		}

		// Extract Authorization header
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		if (authHeader == null || authHeader.isEmpty()) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		// Parse Bearer token
		if (!authHeader.startsWith("Bearer ")) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String token = authHeader.substring(7); // Remove "Bearer " prefix

		try {
			// Validate token
			Claims claims = jwtUtil.validateToken(token);

			// Check expiration
			if (jwtUtil.isTokenExpired(claims)) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			// Extract user info
			String userId = jwtUtil.getUserId(claims);
			String username = jwtUtil.getUsername(claims);
			String roles = String.join(",", jwtUtil.getRoles(claims));

			// Inject headers for downstream services
			ServerWebExchange mutatedExchange = exchange.mutate()
				.request(r -> r
					.header("X-User-Id", userId)
					.header("X-Username", username)
					.header("X-User-Roles", roles))
				.build();

			return chain.filter(mutatedExchange);

		} catch (JwtException e) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}
	}

	/**
	 * Check if route is public (no auth required)
	 */
	private boolean isPublicRoute(String path) {
		for (String publicRoute : PUBLIC_ROUTES) {
			if (path.startsWith(publicRoute)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getOrder() {
		// Run after CORS (highest precedence) but before routing
		return -1;
	}

}
