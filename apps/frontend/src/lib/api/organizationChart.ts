import api from "@/lib/api/axios";
import type { OrgChartNode } from "@/lib/types";

export const organizationChartAPI = {
  byManager: (managerId: string) =>
    api
      .get<OrgChartNode[]>(
        `/v1/organization-chart/manager/${encodeURIComponent(managerId)}`
      )
      .then((r) => r.data),
  byDepartment: (departmentId: number) =>
    api
      .get<OrgChartNode[]>(
        `/v1/organization-chart/department/${departmentId}`
      )
      .then((r) => r.data),
  byEmployee: (employeeId: string) =>
    api
      .get<OrgChartNode>(
        `/v1/organization-chart/employee/${encodeURIComponent(employeeId)}`
      )
      .then((r) => r.data),
};
