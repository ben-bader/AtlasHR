"use client";

import { QueryClientProvider, QueryClient } from "@tanstack/react-query";
import { ReactNode, useEffect } from "react";
import { useAuthStore } from "@/lib/store/auth";
import { getAccessToken } from "@/lib/utils/token";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,
      gcTime: 1000 * 60 * 10,
    },
  },
});

function AuthHydrator() {
  const { isAuthenticated, fetchCurrentUser } = useAuthStore();

  useEffect(() => {
    const token = getAccessToken();
    if (token && !isAuthenticated) {
      fetchCurrentUser();
    }
  }, [isAuthenticated, fetchCurrentUser]);

  return null;
}

export function Providers({ children }: { children: ReactNode }) {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthHydrator />
      {children}
    </QueryClientProvider>
  );
}
