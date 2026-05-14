import api from "@/lib/api/axios";
import type { CreateDesignationPayload, Designation } from "@/lib/types";

export const designationAPI = {
  list: () => api.get<Designation[]>("/v1/designations").then((r) => r.data),
  get: (id: string) =>
    api.get<Designation>(`/v1/designations/${id}`).then((r) => r.data),
  create: (body: CreateDesignationPayload) =>
    api.post<Designation>("/v1/designations", body).then((r) => r.data),
  update: (id: string, body: CreateDesignationPayload) =>
    api.put<Designation>(`/v1/designations/${id}`, body).then((r) => r.data),
  delete: (id: string) => api.delete(`/v1/designations/${id}`),
};
