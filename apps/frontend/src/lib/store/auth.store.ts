import { create } from "zustand";
import { loginRequest, meRequest } from "@/lib/api/auth.api";
import { clearTokens, setTokens } from "@/lib/utils/token";
import type { User } from "@/lib/types/auth.types";

type AuthState = {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: { username: string; password: string }) => Promise<void>;
  logout: () => void;
  fetchMe: () => Promise<void>;
};

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  login: async ({ username, password }: { username: string; password: string }) => {
    set({ isLoading: true });

    const tokens = await loginRequest({ username, password });
    setTokens(tokens.accessToken, tokens.refreshToken);

    try {
      const currentUser = await meRequest();
      set({ user: currentUser, isAuthenticated: true, isLoading: false });
    } catch {
      clearTokens();
      set({ user: null, isAuthenticated: false, isLoading: false });
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
    }
  },
  logout: () => {
    clearTokens();
    set({ user: null, isAuthenticated: false });
    if (typeof window !== "undefined") {
      window.location.href = "/login";
    }
  },
  fetchMe: async () => {
    set({ isLoading: true });
    try {
      const currentUser = await meRequest();
      set({ user: currentUser, isAuthenticated: true });
    } catch {
      clearTokens();
      set({ user: null, isAuthenticated: false });
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
    } finally {
      set({ isLoading: false });
    }
  },
}));
