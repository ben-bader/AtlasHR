package com.hrms.auth.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hrms.auth.domain.model.TokenBlacklist;

/**
 * Repository for Token Blacklist
 * Handles CRUD operations for blacklisted JWT tokens
 */
@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {

    /**
     * Find a blacklist entry by token JTI
     * @param tokenJti the JWT ID claim from the token
     * @return Optional containing the blacklist entry if found
     */
    Optional<TokenBlacklist> findByTokenJti(String tokenJti);

    /**
     * Check if a token is blacklisted
     * @param tokenJti the JWT ID claim
     * @return true if token is blacklisted, false otherwise
     */
    boolean existsByTokenJti(String tokenJti);

    /**
     * Find all blacklisted tokens for a specific user
     * @param userId the user ID
     * @return list of blacklist entries for the user
     */
    java.util.List<TokenBlacklist> findByUserId(UUID userId);

    /**
     * Delete expired blacklist entries
     * @param now the current timestamp
     * @return number of deleted entries
     */
    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.blacklistUntil <= :now")
    int deleteExpiredEntries(@Param("now") LocalDateTime now);

    /**
     * Delete all blacklist entries for a user
     * @param userId the user ID
     * @return number of deleted entries
     */
    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.userId = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

}
