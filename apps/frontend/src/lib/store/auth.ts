import { create } from "zustand";
import { authAPI, AuthResponse, UserDTO } from "@/lib/api/auth";
import { setTokens, clearTokens } from "@/lib/utils/token";
import { RegisterRequest } from "@/lib/types";
import { AxiosError } from "axios";

export interface AuthState {
  user: UserDTO | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (username: string, password: string) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
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
    } catch (error) {
      const errorMessage = (error as AxiosError)?.message || "Login failed";
      set({
        error: errorMessage,
        isLoading: false,
        isAuthenticated: false,
      });
      throw error;
    }
  },

  register: async (data: RegisterRequest) => {
    set({ isLoading: true, error: null });
    try {
      const response = await authAPI.register(data);
      // After successful registration, auto-login if endpoint returns token
      if (response.token) {
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
      } else {
        // If no token returned, registration was successful but needs manual login
        set({ isLoading: false });
      }
    } catch (error) {
      const errorMessage = (error as AxiosError)?.message || "Registration failed";
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
    } catch {
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
