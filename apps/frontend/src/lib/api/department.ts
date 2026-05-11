import api from "@/lib/api/axios";
import type { CreateDepartmentPayload, Department } from "@/lib/types";

export const departmentAPI = {
  list: () => api.get<Department[]>("/departments").then((r) => r.data),
  get: (id: number) =>
    api.get<Department>(`/departments/${id}`).then((r) => r.data),
  create: (body: CreateDepartmentPayload) =>
    api.post<Department>("/departments", body).then((r) => r.data),
  update: (id: number, body: CreateDepartmentPayload) =>
    api.put<Department>(`/departments/${id}`, body).then((r) => r.data),
  delete: (id: number) => api.delete(`/departments/${id}`),
  subDepartments: (parentId: number) =>
    api
      .get<Department[]>(`/departments/${parentId}/sub-departments`)
      .then((r) => r.data),
};
