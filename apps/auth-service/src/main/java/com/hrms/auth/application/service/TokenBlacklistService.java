package com.hrms.auth.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrms.auth.domain.model.TokenBlacklist;
import com.hrms.auth.domain.repository.TokenBlacklistRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Token Blacklist Service
 * 
 * Manages JWT token blacklisting for logout and security purposes.
 * Uses Redis for fast lookups and database for persistence.
 */
@Service
@Slf4j
@Transactional
public class TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository blacklistRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_KEY_PREFIX = "jwt:blacklist:";
    private static final long REDIS_TTL_SECONDS = 24 * 60 * 60; // 24 hours

    /**
     * Add a token to the blacklist
     * 
     * @param tokenJti the JWT ID claim from the token
     * @param userId the user ID
     * @param employeeId the employee ID
     * @param expirationTime when the token expires
     * @param reason why the token is being blacklisted
     */
    public void blacklistToken(String tokenJti, UUID userId, String employeeId, 
                              LocalDateTime expirationTime, String reason) {
        try {
            // Create blacklist entry
            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .tokenJti(tokenJti)
                    .userId(userId)
                    .employeeId(employeeId)
                    .reason(reason)
                    .blacklistUntil(expirationTime)
                    .build();

            // Save to database
            blacklistRepository.save(blacklist);

            // Cache in Redis for fast lookup
            String redisKey = BLACKLIST_KEY_PREFIX + tokenJti;
            redisTemplate.opsForValue().set(redisKey, "true", 
                java.time.Duration.ofSeconds(REDIS_TTL_SECONDS));

            log.info("Token blacklisted - JTI: {}, EmployeeId: {}, Reason: {}", 
                    tokenJti, employeeId, reason);

        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to blacklist token", e);
        }
    }

    /**
     * Check if a token is blacklisted
     * 
     * @param tokenJti the JWT ID claim from the token
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String tokenJti) {
        if (tokenJti == null || tokenJti.isEmpty()) {
            return false;
        }

        try {
            // Check Redis cache first (fast path)
            String redisKey = BLACKLIST_KEY_PREFIX + tokenJti;
            Boolean cachedResult = redisTemplate.hasKey(redisKey);
            if (cachedResult != null && cachedResult) {
                return true;
            }

            // Fall back to database check
            return blacklistRepository.existsByTokenJti(tokenJti);

        } catch (Exception e) {
            log.error("Error checking token blacklist: {}", e.getMessage(), e);
            // On error, assume token is NOT blacklisted to allow graceful degradation
            // In production, you might want to deny access instead
            return false;
        }
    }

    /**
     * Get blacklist details for a token
     * 
     * @param tokenJti the JWT ID claim
     * @return TokenBlacklist entry if found, null otherwise
     */
    public TokenBlacklist getBlacklistEntry(String tokenJti) {
        return blacklistRepository.findByTokenJti(tokenJti).orElse(null);
    }

    /**
     * Remove all blacklist entries for a user
     * (e.g., when user password is reset or account is restored)
     * 
     * @param userId the user ID
     */
    public void clearUserBlacklist(UUID userId) {
        try {
            blacklistRepository.deleteByUserId(userId);
            log.info("Cleared blacklist for user: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing user blacklist: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up expired blacklist entries
     * This should be called periodically (e.g., via scheduled task)
     * 
     * @return number of deleted entries
     */
    public int cleanupExpiredEntries() {
        try {
            int deleted = blacklistRepository.deleteExpiredEntries(LocalDateTime.now());
            if (deleted > 0) {
                log.info("Cleaned up {} expired blacklist entries", deleted);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error cleaning up expired entries: {}", e.getMessage(), e);
            return 0;
        }
    }

}
