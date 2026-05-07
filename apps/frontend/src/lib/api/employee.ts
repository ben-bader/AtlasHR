/**
 * Employee API client
 * Handles all employee-related API calls
 */

import api from "@/lib/api/axios";
import {
  Employee,
  EmployeeListResponse,
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeeSearchParams,
} from "@/lib/types";

export const employeeAPI = {
  /**
   * List all employees with optional filtering and pagination
   */
  listEmployees: async (
    params?: EmployeeSearchParams
  ): Promise<EmployeeListResponse> => {
    const response = await api.get<EmployeeListResponse>("/employees", {
      params: {
        page: params?.page || 1,
        pageSize: params?.pageSize || 10,
        ...(params?.search && { search: params.search }),
        ...(params?.department && { department: params.department }),
        ...(params?.status && { status: params.status }),
        ...(params?.sortBy && { sortBy: params.sortBy }),
        ...(params?.sortOrder && { sortOrder: params.sortOrder }),
      },
    });
    return response.data;
  },

  /**
   * Get a single employee by ID
   */
  getEmployee: async (id: string): Promise<Employee> => {
    const response = await api.get<Employee>(`/employees/${id}`);
    return response.data;
  },

  /**
   * Create a new employee
   */
  createEmployee: async (data: CreateEmployeeRequest): Promise<Employee> => {
    const response = await api.post<Employee>("/employees", data);
    return response.data;
  },

  /**
   * Update an existing employee
   */
  updateEmployee: async (
    id: string,
    data: UpdateEmployeeRequest
  ): Promise<Employee> => {
    const response = await api.put<Employee>(`/employees/${id}`, data);
    return response.data;
  },

  /**
   * Delete an employee
   */
  deleteEmployee: async (id: string): Promise<void> => {
    await api.delete(`/employees/${id}`);
  },

  /**
   * Search employees by query
   */
  searchEmployees: async (query: string): Promise<Employee[]> => {
    const response = await api.get<Employee[]>("/employees/search", {
      params: { q: query },
    });
    return response.data;
  },

  /**
   * Get employees by department
   */
  getEmployeesByDepartment: async (department: string): Promise<Employee[]> => {
    const response = await api.get<Employee[]>(
      `/employees/department/${department}`
    );
    return response.data;
  },
};
