/**
 * Authentication API client
 */

import api from "@/lib/api/axios";
import { RegisterRequest } from "@/lib/types";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  userId: string;
  username: string;
  message: string;
}

export interface UserDTO {
  id: string;
  username: string;
  email?: string;
  enabled?: boolean;
  roles?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export const authAPI = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>("/auth/login", credentials);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>("/auth/register", data);
    return response.data;
  },

  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>("/auth/refresh", { refreshToken });
    return response.data;
  },

  getCurrentUser: async (): Promise<UserDTO> => {
    const response = await api.get<UserDTO>("/auth/me");
    return response.data;
  },

  logout: () => {
    // Clear tokens on logout
    if (typeof window !== "undefined") {
      localStorage.clear();
    }
  },
};
