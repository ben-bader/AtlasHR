package com.hrms.common.security;

/**
 * Thread-local holder for the authenticated user context.
 * Populated by GatewayTrustFilter from gateway-injected headers.
 *
 * Usage in a service or controller:
 *   UserContext ctx = HrmsRequestContextHolder.getUserContext();
 */
public final class HrmsRequestContextHolder {

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    private HrmsRequestContextHolder() {}

    public static void setUserContext(UserContext ctx) {
        HOLDER.set(ctx);
    }

    public static UserContext getUserContext() {
        return HOLDER.get();
    }

    public static String getUserId() {
        UserContext ctx = HOLDER.get();
        return ctx != null ? ctx.getUserId() : null;
    }

    public static String getUsername() {
        UserContext ctx = HOLDER.get();
        return ctx != null ? ctx.getUsername() : null;
    }

    public static boolean isAuthenticated() {
        UserContext ctx = HOLDER.get();
        return ctx != null && ctx.getUserId() != null;
    }

    /** Always call this in a finally block to prevent thread-pool leaks. */
    public static void clear() {
        HOLDER.remove();
    }
}
