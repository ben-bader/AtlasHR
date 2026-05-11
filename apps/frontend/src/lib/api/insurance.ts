import api from "@/lib/api/axios";
import type { AddInsurancePayload, EmployeeInsurance } from "@/lib/types";

export const insuranceAPI = {
  add: (body: AddInsurancePayload) =>
    api
      .post<EmployeeInsurance>("/employees/insurances", body)
      .then((r) => r.data),
  byEmployee: (employeeId: string) =>
    api
      .get<EmployeeInsurance[]>(
        `/employees/insurances/employee/${encodeURIComponent(employeeId)}`
      )
      .then((r) => r.data),
  activeByEmployee: (employeeId: string) =>
    api
      .get<EmployeeInsurance[]>(
        `/employees/insurances/employee/${encodeURIComponent(employeeId)}/active`
      )
      .then((r) => r.data),
  byType: (employeeId: string, insuranceType: string) =>
    api
      .get<EmployeeInsurance[]>(
        `/employees/insurances/employee/${encodeURIComponent(employeeId)}/type/${encodeURIComponent(insuranceType)}`
      )
      .then((r) => r.data),
  byPolicy: (policyNumber: string) =>
    api
      .get<EmployeeInsurance>(
        `/employees/insurances/policy/${encodeURIComponent(policyNumber)}`
      )
      .then((r) => r.data),
  updateStatus: (insuranceId: string, status: string) =>
    api
      .put<EmployeeInsurance>(
        `/employees/insurances/${encodeURIComponent(insuranceId)}/status`,
        null,
        { params: { status } }
      )
      .then((r) => r.data),
  fileClaim: (
    insuranceId: string,
    claimAmount: number,
    claimDetails: string
  ) =>
    api
      .post<EmployeeInsurance>(
        `/employees/insurances/${encodeURIComponent(insuranceId)}/claim`,
        null,
        { params: { claimAmount, claimDetails } }
      )
      .then((r) => r.data),
  delete: (insuranceId: string) =>
    api.delete(`/employees/insurances/${encodeURIComponent(insuranceId)}`),
};
