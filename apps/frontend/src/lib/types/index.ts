/**
 * Centralized type definitions for the application
 */

// ============ Authentication Types ============
export interface User {
  id: string;
  username: string;
  email?: string;
  enabled?: boolean;
  roles?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  userId: string;
  username: string;
  message: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword?: string;
}

// ============ Employee Types ============
export interface Employee {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  department?: string;
  designation?: string;
  joinDate?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  employmentType?: "FULL_TIME" | "PART_TIME" | "CONTRACT" | "TEMPORARY";
  status?: "ACTIVE" | "INACTIVE" | "ON_LEAVE" | "TERMINATED";
  manager?: string;
  salary?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateEmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  department?: string;
  designation?: string;
  joinDate?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  employmentType?: string;
  status?: string;
  manager?: string;
  salary?: number;
}

export type UpdateEmployeeRequest = Partial<CreateEmployeeRequest>

export interface EmployeeListResponse {
  data: Employee[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface EmployeeSearchParams {
  page?: number;
  pageSize?: number;
  search?: string;
  department?: string;
  status?: string;
  sortBy?: string;
  sortOrder?: "asc" | "desc";
}

// ============ API Response Types ============
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
  errors?: Record<string, string[]>;
}

export interface ApiErrorResponse {
  message: string;
  error: string;
  status: number;
  timestamp?: string;
  errors?: Record<string, string[]>;
}

// ============ UI State Types ============
export interface LoadingState {
  isLoading: boolean;
  isSubmitting: boolean;
}

export interface ErrorState {
  error: string | null;
  errors?: Record<string, string>;
}

export interface AsyncState<T> extends LoadingState, ErrorState {
  data: T | null;
}

// ============ Pagination Types ============
export interface PaginationParams {
  page: number;
  pageSize: number;
  total: number;
  totalPages: number;
}

// ============ Form Types ============
export interface FormError {
  field: string;
  message: string;
}
