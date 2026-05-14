package com.hrms.gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Token Utility
 * 
 * Validates JWT tokens using the same secret as auth-service.
 * Uses HS256 algorithm (HMAC with SHA-256).
 */
@Component
@Slf4j
public class JwtUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Validates JWT token and extracts claims
	 */
	public Claims validateToken(String token) {
		try {
			SecretKey key = getSigningKey();
			return Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtException("Invalid JWT token", e);
		}
	}

	/**
	 * Checks if token is expired
	 */
	public boolean isTokenExpired(Claims claims) {
		return claims.getExpiration().before(new Date());
	}

	/**
	 * Extracts username from claims
	 * Falls back to 'username' claim if subject is not set
	 */
	public String getUsername(Claims claims) {
		String subject = claims.getSubject();
		if (subject != null && !subject.isEmpty()) {
			return subject;
		}
		// Fallback to username claim
		Object username = claims.get("username");
		return username != null ? username.toString() : null;
	}

	/**
	 * Extracts userId from claims
	 * Uses username if userId claim is not present
	 */
	public String getUserId(Claims claims) {
		Object userId = claims.get("userId");
		if (userId != null) {
			return userId.toString();
		}
		// Fallback to username as userId if not explicitly set
		return getUsername(claims);
	}

	/**
	 * Extracts roles from claims
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRoles(Claims claims) {
		Object roles = claims.get("roles");
		return roles instanceof List ? (List<String>) roles : List.of();
	}

}
