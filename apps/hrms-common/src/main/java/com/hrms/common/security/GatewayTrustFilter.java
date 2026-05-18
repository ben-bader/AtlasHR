package com.hrms.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unified gateway trust filter for all downstream microservices.
 *
 * Reads X-User-Id, X-Username, X-User-Roles headers injected by the gateway
 * and populates the Spring Security SecurityContext + thread-local HrmsRequestContextHolder.
 *
 * Register this filter in each service's SecurityConfig via:
 *   http.addFilterBefore(gatewayTrustFilter, UsernamePasswordAuthenticationFilter.class)
 */
@Slf4j
public class GatewayTrustFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String userId   = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String rolesStr = request.getHeader("X-User-Roles");

        if (userId != null) {
            if (username == null) username = userId;

            List<SimpleGrantedAuthority> authorities = parseRoles(rolesStr);

            // Populate Spring Security context (enables @PreAuthorize, .authenticated())
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            auth.setDetails(userId);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Populate thread-local context (enables HrmsRequestContextHolder.getUserId())
            String[] rolesArray = rolesStr != null
                    ? Arrays.stream(rolesStr.split(",")).map(String::trim).toArray(String[]::new)
                    : new String[0];
            HrmsRequestContextHolder.setUserContext(new UserContext(userId, username, rolesArray));

            log.debug("Gateway auth set for user={} roles={}", username, authorities);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            HrmsRequestContextHolder.clear();
        }
    }

    private List<SimpleGrantedAuthority> parseRoles(String rolesStr) {
        if (rolesStr == null || rolesStr.isBlank()) {
            return Collections.emptyList();
        }
        // Strip brackets/quotes that some gateway implementations include
        String cleaned = rolesStr.replace("[", "").replace("]", "").replace("\"", "");
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
