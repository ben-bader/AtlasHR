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
 * JWT Authentication Filter - Gateway Level
 * 
 * SINGLE POINT OF SECURITY for entire system.
 * 
 * Responsibilities:
 * - Validate JWT tokens
 * - Inject X-User-* headers for downstream services
 * - Handle CORS headers on error responses
 * - Never block OPTIONS preflight requests
 * 
 * Microservices blindly trust X-User-* headers from gateway.
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
		"/api/auth/refresh"
	};

	/**
	 * Filter execution order - run after security filters
	 */


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getPath().value();
		String method = exchange.getRequest().getMethod().toString();

		log.debug("JWT Filter: {} {}", method, path);

		// ✅ ALWAYS allow OPTIONS preflight requests (no JWT needed)
		if ("OPTIONS".equals(method)) {
			log.debug("Allowing OPTIONS preflight request");
			return chain.filter(exchange);
		}

		// ✅ ALLOW public routes without JWT
		if (isPublicRoute(path)) {
			log.debug("Public route {} - no auth required", path);
			return chain.filter(exchange);
		}

		// 🔐 PROTECTED ROUTES - require JWT

		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		// Missing or invalid Bearer token
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Missing/invalid JWT for {}", path);
			return sendUnauthorizedWithCorsHeaders(exchange);
		}

		String token = authHeader.substring(7); // Remove "Bearer " prefix

		try {
			// Validate JWT signature and expiration
			Claims claims = jwtUtil.validateToken(token);

			if (jwtUtil.isTokenExpired(claims)) {
				log.warn("Token expired for {}", path);
				return sendUnauthorizedWithCorsHeaders(exchange);
			}

			// ✅ Extract user info from JWT
			String userId = jwtUtil.getUserId(claims);
			String username = jwtUtil.getUsername(claims);
			String roles = String.join(",", jwtUtil.getRoles(claims));

			log.debug("JWT valid for user: {} ({})", username, userId);

			// ✅ INJECT HEADERS - Gateway passes user context to services
			// Services read these headers, no JWT parsing needed
			ServerWebExchange mutatedExchange = exchange.mutate()
				.request(r -> r
					.header("X-User-Id", userId)
					.header("X-Username", username)
					.header("X-User-Roles", roles))
				.build();

			return chain.filter(mutatedExchange);

		} catch (JwtException | IllegalArgumentException e) {
			log.error("JWT validation failed: {}", e.getMessage());
			return sendUnauthorizedWithCorsHeaders(exchange);
		}
	}

	/**
	 * Send 401 with CORS headers
	 * 
	 * This fixes the browser "No 'Access-Control-Allow-Origin' header" error.
	 * Browser gets CORS headers in error response, preventing CORS block.
	 */
private Mono<Void> sendUnauthorizedWithCorsHeaders(ServerWebExchange exchange) {
    String origin = exchange.getRequest().getHeaders().getOrigin();

    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

    if (origin != null) {
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", origin);
    }

    exchange.getResponse().getHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH,OPTIONS");
    exchange.getResponse().getHeaders().set("Access-Control-Allow-Headers", "Authorization,Content-Type");
    exchange.getResponse().getHeaders().set("Access-Control-Allow-Credentials", "true");

    return exchange.getResponse().setComplete();
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
