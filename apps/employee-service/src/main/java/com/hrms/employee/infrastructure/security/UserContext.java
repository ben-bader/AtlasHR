package com.hrms.employee.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;

/**
 * User Context - Information about authenticated user
 * 
 * Populated by gateway-injected headers.
 * Available throughout request via RequestContextHolder.
 */
@Getter
@AllArgsConstructor
public class UserContext {
	
	private String userId;
	private String username;
	private String[] roles;
	
	/**
	 * Get roles as list (convenient for Stream operations)
	 */
	public List<String> getRolesList() {
		return Arrays.asList(roles != null ? roles : new String[0]);
	}
	
	/**
	 * Check if user has specific role
	 */
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
