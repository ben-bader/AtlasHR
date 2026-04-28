"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getAccessToken } from "@/lib/utils/token";
import { useAuth } from "@/lib/hooks/useAuth";

export function useProtectedRoute() {
  const router = useRouter();
  const { user, fetchMe, isLoading } = useAuth();
  const [hasChecked, setHasChecked] = useState(false);
  const [validating, setValidating] = useState(true);

  useEffect(() => {
    const token = getAccessToken();
    if (!token) {
      router.replace("/login");
      setValidating(false);
      return;
    }

    const verify = async () => {
      try {
        await fetchMe();
      } catch {
        router.replace("/login");
      } finally {
        setHasChecked(true);
        setValidating(false);
      }
    };

    verify();
  }, [fetchMe, router]);

  return {
    isLoading: validating || isLoading,
    isReady: hasChecked && !validating && !!user,
  };
}
