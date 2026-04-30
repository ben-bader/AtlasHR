import { create } from "zustand";
import { authAPI, AuthResponse, UserDTO } from "@/lib/api/auth";
import { setTokens, clearTokens } from "@/lib/utils/token";

export interface AuthState {
  user: UserDTO | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  fetchCurrentUser: () => Promise<void>;
  setError: (error: string | null) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  login: async (username: string, password: string) => {
    set({ isLoading: true, error: null });
    try {
      const response: AuthResponse = await authAPI.login({ username, password });
      
      // Save tokens to localStorage
      setTokens(response.token, response.refreshToken);

      set({
        token: response.token,
        user: {
          id: response.userId,
          username: response.username,
        },
        isAuthenticated: true,
        isLoading: false,
      });
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || "Login failed";
      set({
        error: errorMessage,
        isLoading: false,
        isAuthenticated: false,
      });
      throw error;
    }
  },

  logout: () => {
    clearTokens();
    authAPI.logout();
    set({
      user: null,
      token: null,
      isAuthenticated: false,
      error: null,
    });
  },

  fetchCurrentUser: async () => {
    set({ isLoading: true });
    try {
      const user = await authAPI.getCurrentUser();
      set({
        user,
        isAuthenticated: true,
        isLoading: false,
      });
    } catch (error: any) {
      set({
        error: "Failed to fetch user",
        isLoading: false,
        isAuthenticated: false,
      });
    }
  },

  setError: (error: string | null) => {
    set({ error });
  },
}));
