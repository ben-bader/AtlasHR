package com.hrms.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JWT Token Blacklist Entity
 * 
 * Stores blacklisted tokens to prevent their usage after logout or expiration.
 * This entity is used with Redis for high-performance lookup.
 */
@Entity
@Table(name = "token_blacklist", indexes = {
    @Index(name = "idx_token_jti", columnList = "token_jti", unique = true),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_blacklist_until", columnList = "blacklist_until")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TokenBlacklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * JWT ID (jti) claim - unique identifier for the token
     * This is extracted from the JWT token
     */
    @Column(nullable = false, unique = true, name = "token_jti", length = 500)
    private String tokenJti;

    /**
     * User ID - reference to the user who owns this token
     */
    @Column(nullable = false, name = "user_id")
    private UUID userId;

    /**
     * Employee ID - for easy reference to employee service
     */
    @Column(name = "employee_id", length = 20)
    private String employeeId;

    /**
     * Reason for blacklisting
     * e.g., "logout", "token_refresh", "password_change", "account_locked"
     */
    @Column(name = "reason", length = 100)
    private String reason;

    /**
     * Token expiration time - after this time, record can be deleted
     * We store this to automatically clean up old blacklist entries
     */
    @Column(nullable = false, name = "blacklist_until")
    private LocalDateTime blacklistUntil;

    /**
     * Timestamp when token was added to blacklist
     */
    @Column(nullable = false, name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
