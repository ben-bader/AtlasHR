/**
 * Authentication API client
 */

import api from "@/lib/api/axios";
import { RegisterRequest } from "@/lib/types";

/**
 * Login request DTO
 * Updated to use employeeId instead of username
 */
export interface LoginRequest {
  username: string; // Can be employeeId or username (backend supports both)
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
  /**
   * Login with employeeId or username
   * The backend now accepts both employeeId and username
   */
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>("/auth/login", credentials);
    return response.data;
  },

  /**
   * DEPRECATED: Register endpoint
   * Public registration is no longer supported.
   * Employees are created by admin through Employee Service.
   */
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

  /**
   * Logout user and blacklist token
   */
  logout: async (token: string): Promise<void> => {
    try {
      await api.post("/auth/logout", {}, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
    } catch (error) {
      // Ignore errors on logout - always clear local tokens
      console.warn("Logout request failed", error);
    }
    
    // Clear tokens on logout
    if (typeof window !== "undefined") {
      localStorage.clear();
    }
  },

  // Legacy logout that doesn't require token
  logoutLegacy: () => {
    if (typeof window !== "undefined") {
      localStorage.clear();
    }
  },
};
