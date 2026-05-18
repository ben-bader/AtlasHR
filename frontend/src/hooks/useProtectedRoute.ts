import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useAuthStore } from "@/lib/store/auth";
import { getAccessToken } from "@/lib/utils/token";

/**
 * Custom hook for route protection
 * Redirects to login if no token
 */
export function useProtectedRoute() {
  const router = useRouter();
  const { isAuthenticated, fetchCurrentUser } = useAuthStore();

  useEffect(() => {
    const token = getAccessToken();

    if (!token && !isAuthenticated) {
      router.push("/");
      return;
    }

    // Fetch current user if authenticated
    if (token && !isAuthenticated) {
      fetchCurrentUser();
    }
  }, [isAuthenticated, router, fetchCurrentUser]);

  return { isAuthenticated };
}
