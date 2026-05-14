import api from "@/lib/api/axios";
import type { AddEmployeeSkillPayload, EmployeeSkill } from "@/lib/types";

export const skillsAPI = {
  add: (body: AddEmployeeSkillPayload) =>
    api.post<EmployeeSkill>("/v1/skills", body).then((r) => r.data),
  byEmployee: (employeeId: string) =>
    api
      .get<EmployeeSkill[]>(
        `/v1/skills/employee/${encodeURIComponent(employeeId)}`
      )
      .then((r) => r.data),
  bySkillName: (skillName: string) =>
    api
      .get<EmployeeSkill[]>(
        `/v1/skills/search/${encodeURIComponent(skillName)}`
      )
      .then((r) => r.data),
  update: (skillId: string, body: AddEmployeeSkillPayload) =>
    api.put<EmployeeSkill>(`/v1/skills/${skillId}`, body).then((r) => r.data),
  remove: (skillId: string) => api.delete(`/v1/skills/${skillId}`),
};
