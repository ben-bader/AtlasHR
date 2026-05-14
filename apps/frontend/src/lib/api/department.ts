import api from "@/lib/api/axios";
import type { CreateDepartmentPayload, Department } from "@/lib/types";

export const departmentAPI = {
  list: () => api.get<Department[]>("/v1/departments").then((r) => r.data),
  get: (id: number) =>
    api.get<Department>(`/v1/departments/${id}`).then((r) => r.data),
  create: (body: CreateDepartmentPayload) =>
    api.post<Department>("/v1/departments", body).then((r) => r.data),
  update: (id: number, body: CreateDepartmentPayload) =>
    api.put<Department>(`/v1/departments/${id}`, body).then((r) => r.data),
  delete: (id: number) => api.delete(`/v1/departments/${id}`),
  subDepartments: (parentId: number) =>
    api
      .get<Department[]>(`/v1/departments/${parentId}/sub-departments`)
      .then((r) => r.data),
};
