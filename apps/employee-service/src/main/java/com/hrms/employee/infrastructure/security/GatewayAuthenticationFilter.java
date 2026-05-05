package com.hrms.employee.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Gateway Authentication Filter
 * 
 * Trusts X-User-Id, X-User-Roles, and X-Username headers from API Gateway
 * The gateway is responsible for JWT verification
 * This service only needs to trust the gateway and use the forwarded identity
 */
@Component
@Slf4j
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    // Allow actuator and health endpoints without headers
    private static final String[] PUBLIC_ENDPOINTS = {
        "/actuator",
        "/health",
        "/health/live",
        "/health/ready"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for public endpoints
        String requestPath = request.getRequestURI();
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String rolesHeader = request.getHeader("X-User-Roles");

        // Check if required headers are present
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("[GATEWAY-AUTH] ✗ Missing X-User-Id header for path: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Missing X-User-Id header\"}");
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            username = userId;
        }

        try {
            // Parse roles from header (JSON array string)
            Collection<GrantedAuthority> authorities = parseRoles(rolesHeader);

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            authentication.setDetails(userId);

            // Set in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("[GATEWAY-AUTH] ✓ User authenticated via gateway: {} ({})", username, userId);

        } catch (Exception e) {
            log.error("[GATEWAY-AUTH] ✗ Error processing gateway headers: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid gateway headers\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Parse roles from JSON array string (e.g., "[\"ROLE_USER\",\"ROLE_ADMIN\"]")
     */
    private Collection<GrantedAuthority> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.trim().isEmpty()) {
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        try {
            // Remove brackets and quotes, split by comma
            String cleaned = rolesHeader
                    .replaceAll("[\\[\\]\"]", "")
                    .trim();

            if (cleaned.isEmpty()) {
                return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return Arrays.stream(cleaned.split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("[GATEWAY-AUTH] ⚠ Failed to parse roles header: {}", rolesHeader, e);
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    /**
     * Check if request path is public
     */
    private boolean isPublicEndpoint(String requestPath) {
        return Arrays.stream(PUBLIC_ENDPOINTS)
                .anyMatch(requestPath::startsWith);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestPath = request.getRequestURI();
        return isPublicEndpoint(requestPath);
    }
}
