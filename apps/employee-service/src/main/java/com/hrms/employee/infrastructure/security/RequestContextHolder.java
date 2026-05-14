package com.hrms.employee.infrastructure.security;

import lombok.extern.slf4j.Slf4j;

/**
 * Request Context Holder - Thread-Local User Context
 * 
 * Makes authenticated user info available throughout request handling.
 * Populated by TrustHeadersFilter from gateway-injected headers.
 * 
 * Usage in service/controller:
 *   UserContext ctx = RequestContextHolder.getUserContext();
 *   if (ctx != null) {
 *     String userId = ctx.getUserId();
 *     String username = ctx.getUsername();
 *   }
 */
@Slf4j
public class RequestContextHolder {
	
	private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();
	
	/**
	 * Set user context for current thread (called by TrustHeadersFilter)
	 */
	public static void setUserContext(UserContext userContext) {
		userContextThreadLocal.set(userContext);
		log.debug("User context set: {}", userContext);
	}
	
	/**
	 * Get user context for current thread
	 */
	public static UserContext getUserContext() {
		return userContextThreadLocal.get();
	}
	
	/**
	 * Get user ID or null
	 */
	public static String getUserId() {
		UserContext ctx = userContextThreadLocal.get();
		return ctx != null ? ctx.getUserId() : null;
	}
	
	/**
	 * Get username or null
	 */
	public static String getUsername() {
		UserContext ctx = userContextThreadLocal.get();
		return ctx != null ? ctx.getUsername() : null;
	}
	
	/**
	 * Check if request is authenticated
	 */
	public static boolean isAuthenticated() {
		UserContext ctx = userContextThreadLocal.get();
		return ctx != null && ctx.getUserId() != null;
	}
	
	/**
	 * Clear context (called by TrustHeadersFilter after response)
	 * IMPORTANT: Always clean up to prevent thread pool reuse issues
	 */
	public static void clearUserContext() {
		userContextThreadLocal.remove();
		log.debug("User context cleared");
	}

}
