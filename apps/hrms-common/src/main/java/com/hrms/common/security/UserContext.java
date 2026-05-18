package com.hrms.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Authenticated user context populated from gateway-injected headers.
 * Available within a request thread via HrmsRequestContextHolder.
 */
@Getter
@AllArgsConstructor
public class UserContext {

    private final String userId;
    private final String username;
    private final String[] roles;

    public List<String> getRolesList() {
        return Arrays.asList(roles != null ? roles : new String[0]);
    }

    public boolean hasRole(String role) {
        if (roles == null) return false;
        return Arrays.asList(roles).contains(role);
    }

    @Override
    public String toString() {
        return String.format("UserContext(userId=%s, username=%s, roles=%s)",
                userId, username, Arrays.toString(roles));
    }
}
