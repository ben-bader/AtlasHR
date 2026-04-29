import { useQuery } from "@tanstack/react-query";
import { getEmployees } from "@/lib/api/employee.api";

export function useEmployees() {
  return useQuery({
    queryKey: ["employees"],
    queryFn: getEmployees,
    staleTime: 1000 * 60,
    retry: false,
  });
}
