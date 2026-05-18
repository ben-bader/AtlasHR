package com.hrms.auth.infrastructure.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hrms.auth.application.service.TokenBlacklistService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Authentication Filter for Auth Service
 * 
 * This filter:
 * 1. Validates JWT tokens
 * 2. Checks if tokens are blacklisted
 * 3. Sets up Spring Security context for authenticated requests
 * 
 * Used for:
 * - Token validation on protected endpoints
 * - Logout endpoint that requires valid token
 * - Admin endpoints that require authentication
 */
@Component
@Slf4j
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String jwt = extractJwtFromRequest(httpRequest);

            if (StringUtils.hasText(jwt)) {
                // Validate token
                if (tokenProvider.validateToken(jwt)) {
                    
                    // Check if token is blacklisted
                    Claims claims = tokenProvider.parseToken(jwt);
                    String tokenJti = claims.getId();
                    
                    if (tokenJti != null && tokenBlacklistService.isTokenBlacklisted(tokenJti)) {
                        log.warn("Attempt to use blacklisted token: {}", tokenJti);
                        // Don't set authentication - let the request proceed but will fail on @PreAuthorize
                    } else {
                        // Token is valid and not blacklisted
                        String username = tokenProvider.getUsernameFromToken(jwt);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("JWT authentication set for user: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication filter error: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * 
     * @param request the HTTP request
     * @return JWT token or null if not found
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
