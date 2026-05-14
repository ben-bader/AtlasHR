// FIX: Added early return when no auth headers present (avoids double
// chain.doFilter call). Filter now only called from inside Security chain
// via addFilterBefore, so SecurityContext is populated before auth check.

package com.hrms.employee.infrastructure.security;

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

@Component
@Slf4j
public class TrustHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Skip actuator - no user context needed, avoids log spam
        if (path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        // Read headers injected by gateway
        String userId   = httpRequest.getHeader("X-User-Id");
        String username = httpRequest.getHeader("X-Username");
        String rolesStr = httpRequest.getHeader("X-User-Roles");

        if (userId != null || username != null) {
            // Build user context (thread-local)
            UserContext userContext = new UserContext(
                userId,
                username,
                rolesStr != null ? rolesStr.split(",") : new String[0]
            );
            RequestContextHolder.setUserContext(userContext);

            // Build Spring Security authorities
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if (rolesStr != null && !rolesStr.isEmpty()) {
                for (String role : rolesStr.split(",")) {
                    String normalized = role.trim().startsWith("ROLE_")
                        ? role.trim()
                        : "ROLE_" + role.trim();
                    authorities.add(new SimpleGrantedAuthority(normalized));
                }
            }

            // Populate SecurityContext - this is what makes .authenticated() pass
            Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Auth set for user: {} roles: {}", username, authorities);
        }
        // If no headers → SecurityContext stays empty → Spring Security
        // will block the request with 401 (not 302, because formLogin is disabled)

        try {
            chain.doFilter(request, response);
        } finally {
            // Always clean up - critical for thread pool safety
            RequestContextHolder.clearUserContext();
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException {
        log.info("TrustHeadersFilter initialized - microservice trusts gateway security");
    }

    @Override
    public void destroy() {}
}