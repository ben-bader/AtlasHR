import { useEffect } from "react";
import { useAuthStore } from "@/lib/store/auth";
import { getAccessToken } from "@/lib/utils/token";
import { RegisterRequest } from "@/lib/types";

/**
 * Custom hook for authentication logic
 * Provides login/logout and auth state
 */
export function useAuth() {
  const { user, token, isAuthenticated, isLoading, error, login, register, logout, setError } = useAuthStore();

  // Check if user is logged in on mount
  useEffect(() => {
    const token = getAccessToken();
    if (token && !isAuthenticated) {
      // Optionally fetch current user here if needed
    }
  }, [isAuthenticated]);

  return {
    user,
    token,
    isAuthenticated,
    isLoading,
    error,
    login,
    register: (data: RegisterRequest) => register(data),
    logout,
    setError,
  };
}
