import api from "@/lib/api/axios";

export type Employee = {
  id: string;
  name: string;
  role?: string;
};

export async function getEmployees() {
  const response = await api.get<Employee[]>("/employees");
  return response.data;
}
