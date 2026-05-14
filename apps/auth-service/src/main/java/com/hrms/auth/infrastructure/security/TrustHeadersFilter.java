package com.hrms.auth.infrastructure.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Trust Gateway Headers Filter
 * 
 * Microservices trust headers injected by API Gateway.
 * No JWT validation here - all security done at gateway.
 * 
 * This filter reads:
 * - X-User-Id: User ID from authenticated JWT
 * - X-Username: Username from authenticated JWT  
 * - X-User-Roles: Comma-separated roles from JWT
 * 
 * And makes them available to request handlers via:
 * 1. RequestContextHolder (thread-local)
 * 2. Spring Security SecurityContext (for @Secured, @PreAuthorize, etc.)
 */
@Component
@Slf4j
public class TrustHeadersFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String path = httpRequest.getRequestURI();
		
		// BUG #6 FIX: Skip filter processing for actuator health checks
		// These don't need user context and generate unnecessary log spam
		if (path.startsWith("/actuator")) {
			chain.doFilter(request, response);
			return;
		}
		
		// Read headers injected by gateway
		String userId = httpRequest.getHeader("X-User-Id");
		String username = httpRequest.getHeader("X-Username");
		String rolesStr = httpRequest.getHeader("X-User-Roles");
		
		// If headers present, user is authenticated by gateway
		if (userId != null || username != null) {
			log.debug("Authenticated request from gateway: userId={}, username={}", userId, username);
			
			// Create user context for RequestContextHolder
			UserContext userContext = new UserContext(
				userId,
				username,
				rolesStr != null ? rolesStr.split(",") : new String[0]
			);
			RequestContextHolder.setUserContext(userContext);
			
			// CRITICAL: Also populate Spring Security SecurityContext
			// This allows @Secured, @PreAuthorize, .authenticated() to work
			Collection<GrantedAuthority> authorities = new ArrayList<>();
			if (rolesStr != null && !rolesStr.isEmpty()) {
				String[] roles = rolesStr.split(",");
				for (String role : roles) {
					// BUG #7 FIX: Normalize ROLE_ prefix - prevent ROLE_ROLE_ADMIN duplication
					// If gateway sends roles already prefixed, only add ROLE_ if not present
					String normalizedRole = role.trim().startsWith("ROLE_") 
						? role.trim() 
						: "ROLE_" + role.trim();
					authorities.add(new SimpleGrantedAuthority(normalizedRole));
				}
			}
			
			// Create authentication token (already authenticated by gateway)
			Authentication auth = new UsernamePasswordAuthenticationToken(
				username,
				null, // no password needed, gateway already validated
				authorities
			);
			
			SecurityContextHolder.getContext().setAuthentication(auth);
			log.debug("Spring Security authentication set for user: {} with roles: {}", username, authorities);
		}
		
		try {
			chain.doFilter(request, response);
		} finally {
			// Always clean up both contexts
			RequestContextHolder.clearUserContext();
			SecurityContextHolder.clearContext();
		}
	}

	@Override
	public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException {
		log.info("TrustHeadersFilter initialized - microservice trusts gateway security");
	}

	@Override
	public void destroy() {
		// Cleanup
	}

}
