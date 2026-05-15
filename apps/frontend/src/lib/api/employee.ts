/**
 * Employee API client — aligned with employee-service EmployeeController
 */

import api from "@/lib/api/axios";
import type {
  Employee,
  EmployeeListResponse,
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeeSearchParams,
  TerminateEmployeeRequest,
  PromoteEmployeeRequest,
  TransferEmployeeRequest,
} from "@/lib/types";

/** Raw JSON from employee-service EmployeeResponse */
interface EmployeeApiRow {
  employeeId?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  primaryPhone?: string;
  departmentName?: string;
  departmentId?: number;
  designationName?: string;
  designationId?: string;
  joiningDate?: string;
  dateOfBirth?: string;
  currentAddress?: string;
  city?: string;
  province?: string;
  codePostal?: string;
  status?: string;
  reportingManagerName?: string;
  reportingManagerId?: string;
}

function mapRowToEmployee(row: EmployeeApiRow): Employee {
  return {
    id: row.employeeId ?? "",
    firstName: row.firstName ?? "",
    lastName: row.lastName ?? "",
    email: row.email ?? "",
    phone: row.primaryPhone,
    department: row.departmentName,
    departmentId: row.departmentId,
    designation: row.designationName,
    designationId: row.designationId,
    reportingManagerId: row.reportingManagerId,
    joinDate: row.joiningDate,
    dateOfBirth: row.dateOfBirth,
    address: row.currentAddress,
    city: row.city,
    state: row.province,
    zipCode: row.codePostal,
    status: row.status as Employee["status"],
    manager: row.reportingManagerName,
  };
}

function mapFormToOnboardPayload(data: CreateEmployeeRequest): Record<string, unknown> {
  const payload: Record<string, unknown> = {
    firstName: data.firstName,
    lastName: data.lastName,
    email: data.email,
    primaryPhone: data.phone,
    currentAddress: data.address,
    city: data.city,
    province: data.state,
    codePostal: data.zipCode,
    joiningDate: data.joinDate || undefined,
    dateOfBirth: data.dateOfBirth || undefined,
  };
  if (data.departmentId != null) payload.departmentId = data.departmentId;
  if (data.designationId) payload.designationId = data.designationId;
  if (data.reportingManagerId) payload.reportingManagerId = data.reportingManagerId;
  return payload;
}

function mapFormToUpdatePayload(data: UpdateEmployeeRequest): Record<string, unknown> {
  const payload: Record<string, unknown> = {};
  const put = (k: string, v: unknown) => {
    if (v !== undefined && v !== "") payload[k] = v;
  };
  put("firstName", data.firstName);
  put("lastName", data.lastName);
  put("email", data.email);
  put("primaryPhone", data.phone);
  put("currentAddress", data.address);
  put("city", data.city);
  put("province", data.state);
  put("codePostal", data.zipCode);
  put("joiningDate", data.joinDate);
  put("dateOfBirth", data.dateOfBirth);
  if (data.departmentId != null) payload.departmentId = data.departmentId;
  if (data.designationId) payload.designationId = data.designationId;
  if (data.reportingManagerId) payload.reportingManagerId = data.reportingManagerId;
  return payload;
}

export const employeeAPI = {
  listEmployees: async (
    params?: EmployeeSearchParams
  ): Promise<EmployeeListResponse> => {
    const response = await api.get<EmployeeApiRow[]>("/employees/active");
    let rows = response.data.map(mapRowToEmployee);

    if (params?.search) {
      const q = params.search.toLowerCase();
      rows = rows.filter(
        (e) =>
          e.firstName?.toLowerCase().includes(q) ||
          e.lastName?.toLowerCase().includes(q) ||
          e.email?.toLowerCase().includes(q) ||
          e.id?.toLowerCase().includes(q)
      );
    }
    if (params?.department) {
      rows = rows.filter((e) => e.department === params.department);
    }
    if (params?.status) {
      rows = rows.filter((e) => e.status === params.status);
    }

    const page = params?.page ?? 1;
    const pageSize = params?.pageSize ?? 10;
    const total = rows.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize) || 1);
    const start = (page - 1) * pageSize;
    const data = rows.slice(start, start + pageSize);

    return {
      data,
      total,
      page,
      pageSize,
      totalPages,
    };
  },

  getEmployee: async (id: string): Promise<Employee> => {
    const response = await api.get<EmployeeApiRow>(`/employees/${id}`);
    return mapRowToEmployee(response.data);
  },

  /** POST /employees/onboard */
  createEmployee: async (data: CreateEmployeeRequest): Promise<Employee> => {
    const response = await api.post<EmployeeApiRow>(
      "/employees/onboard",
      mapFormToOnboardPayload(data)
    );
    return mapRowToEmployee(response.data);
  },

  updateEmployee: async (
    id: string,
    data: UpdateEmployeeRequest
  ): Promise<Employee> => {
    const response = await api.put<EmployeeApiRow>(
      `/employees/${id}`,
      mapFormToUpdatePayload(data)
    );
    return mapRowToEmployee(response.data);
  },

  terminateEmployee: async (body: TerminateEmployeeRequest): Promise<void> => {
    await api.post("/employees/terminate", {
      employeeId: body.employeeId,
      lastWorkingDate: body.lastWorkingDate,
      terminationReason: body.terminationReason,
      comments: body.comments,
    });
  },

  promoteEmployee: async (body: PromoteEmployeeRequest): Promise<Employee> => {
    const response = await api.post<EmployeeApiRow>("/employees/promote", {
      employeeId: body.employeeId,
      newDesignationId: body.newDesignationId,
      newGrade: body.newGrade,
      effectiveDate: body.effectiveDate,
      reason: body.reason,
      triggerSalaryRevision: body.triggerSalaryRevision,
    });
    return mapRowToEmployee(response.data);
  },

  transferEmployee: async (body: TransferEmployeeRequest): Promise<Employee> => {
    const response = await api.post<EmployeeApiRow>("/employees/transfer", {
      employeeId: body.employeeId,
      newDepartmentId: body.newDepartmentId,
      newDesignationId: body.newDesignationId,
      effectiveDate: body.effectiveDate,
      reason: body.reason,
    });
    return mapRowToEmployee(response.data);
  },

  getEmployeesByDepartment: async (
    departmentId: number
  ): Promise<Employee[]> => {
    const response = await api.get<EmployeeApiRow[]>(
      `/employees/department/${departmentId}`
    );
    return response.data.map(mapRowToEmployee);
  },
};
