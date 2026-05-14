package com.hrms.auth.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Token Provider
 * 
 * Generates JWT tokens for authenticated users.
 * Used ONLY in auth-service for login endpoint.
 * Gateway validates these tokens and injects user headers.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    // FIXED: Changed from app.jwtSecret to jwt.secret for consistency with gateway
    @Value("${jwt.secret:IEsr9J344VSPBtwUCOH467cAtqm7b0YAqAYBAa8AYTg=}")
    private String jwtSecret;

    // FIXED: Changed from app.jwtExpirationMs to jwt.expiration for consistency
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs; // 24 hours default

    // FIXED: Changed from app.jwtRefreshExpirationMs to jwt.refresh.expiration for consistency
    @Value("${jwt.refresh.expiration:604800000}")
    private long jwtRefreshExpirationMs; // 7 days default

    public String generateToken(UserDetails userDetails) {
        // If UserDetails is a User entity, extract the userId
        String userId = null;
        if (userDetails instanceof com.hrms.auth.domain.model.User user) {
            userId = user.getId() != null ? user.getId().toString() : null;
        }
        return generateToken(userDetails.getUsername(), extractRoles(userDetails), userId);
    }

    public String generateToken(String username, Collection<String> roles) {
        return generateToken(username, roles, null);
    }

    /**
     * Generate JWT token with userId claim
     * 
     * FIXED (BUG #5): Added userId claim so gateway can inject X-User-Id header
     * with the actual user ID instead of username
     */
    public String generateToken(String username, Collection<String> roles, String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        var builder = Jwts.builder()
                .subject(username)
                .claim("username", username);
        
        // Add userId if provided, otherwise use username as fallback
        if (userId != null && !userId.isEmpty()) {
            builder.claim("userId", userId);
        } else {
            builder.claim("userId", username);
        }
        
        builder.claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);
        
        return builder.compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(userDetails.getUsername());
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("username", username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public Claims parseToken(String token) {
        try {
            SecretKey key = getSigningKey();
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Collection<String> extractRoles(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}

