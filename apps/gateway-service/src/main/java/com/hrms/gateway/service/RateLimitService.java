package com.hrms.gateway.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Rate Limiting Service
 * 
 * Placeholder for future Redis-backed rate limiting.
 * Currently disabled.
 */
@Service
@Slf4j
public class RateLimitService {

	/**
	 * Check if request should be rate limited
	 * Always returns true (rate limiting disabled)
	 */
	public Mono<Boolean> isRequestAllowed(String userId) {
		return Mono.just(true);
	}

}
