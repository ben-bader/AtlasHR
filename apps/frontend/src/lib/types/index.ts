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
  departmentId?: number;
  designation?: string;
  designationId?: string;
  reportingManagerId?: string;
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
  /** Backend onboarding uses numeric department id when set */
  departmentId?: number;
  designationId?: string;
  reportingManagerId?: string;
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

export interface TerminateEmployeeRequest {
  employeeId: string;
  lastWorkingDate: string;
  terminationReason?: string;
  comments?: string;
}

export interface PromoteEmployeeRequest {
  employeeId: string;
  newDesignationId: string;
  newGrade?: string;
  effectiveDate?: string;
  reason?: string;
  triggerSalaryRevision?: boolean;
}

export interface TransferEmployeeRequest {
  employeeId: string;
  newDepartmentId: number;
  newDesignationId?: string;
  effectiveDate?: string;
  reason?: string;
}

export interface Department {
  departmentId: number;
  departmentName: string;
  description?: string;
  departmentCode?: string;
  departmentHead?: string;
  parentDepartmentId?: number | null;
  status?: string;
}

export interface CreateDepartmentPayload {
  departmentName: string;
  description?: string;
  parentDepartmentId?: number | null;
  departmentCode?: string;
  departmentHead?: string;
}

export interface Designation {
  designationId: string;
  designationName: string;
  description?: string;
  designationCode?: string;
  hierarchyLevel?: number;
  reportingDesignation?: string;
  status?: string;
}

export interface CreateDesignationPayload {
  designationName: string;
  description?: string;
  designationCode?: string;
  hierarchyLevel?: number;
  reportingDesignation?: string;
}

export interface OrgChartNode {
  id: string;
  hierarchyLevel: number;
  createdAt?: string;
  updatedAt?: string;
  employee?: {
    id?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
  };
  manager?: {
    id?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
  } | null;
}

export interface EmployeeSkill {
  skillId: string;
  employeeId: string;
  skillName: string;
  competencyLevel?: string;
  certification?: string;
  status?: string;
}

export interface AddEmployeeSkillPayload {
  employeeId: string;
  skillName: string;
  competencyLevel?: string;
  certification?: string;
}

export interface EmployeeInsurance {
  insuranceId: string;
  employeeId: string;
  policyNumber?: string;
  insuranceType?: string;
  providerName?: string;
  coverageAmount?: number;
  policyStartDate?: string;
  policyEndDate?: string;
  premiumAmount?: number;
  beneficiaryName?: string;
  beneficiaryRelationship?: string;
  beneficiaryPhone?: string;
  beneficiaryEmail?: string;
  status?: string;
  claimDetails?: string;
  claimDate?: string;
  claimAmount?: number;
}

export interface AddInsurancePayload {
  employeeId: string;
  policyNumber?: string;
  insuranceType?: string;
  providerName?: string;
  coverageAmount?: number;
  policyStartDate?: string;
  policyEndDate?: string;
  premiumAmount?: number;
  beneficiaryName?: string;
  beneficiaryRelationship?: string;
  beneficiaryPhone?: string;
  beneficiaryEmail?: string;
}

export interface Attendance {
  id: number;
  employeeId?: string;
  date?: string;
  checkIn?: string;
  checkOut?: string;
  status?: string;
  shiftId?: number;
  shiftName?: string;
  method?: string;
  workedHours?: number;
  isLate?: boolean;
  lateMinutes?: number;
  isOvertime?: boolean;
  overtimeMinutes?: number;
}

export interface DailyAttendance {
  id: number;
  date?: string;
  totalPresent?: number;
  totalAbsent?: number;
  totalLate?: number;
  totalOnLeave?: number;
  attendances?: Attendance[];
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
