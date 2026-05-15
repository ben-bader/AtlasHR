/**
 * Custom hook for employee data management
 * Handles fetching, creating, updating, and deleting employees
 */

import { useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { employeeAPI } from "@/lib/api/employee"
import {
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeeSearchParams,
  TerminateEmployeeRequest,
} from "@/lib/types"

export function useEmployeeList(params?: EmployeeSearchParams) {
  const {
    data,
    isLoading,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: ["employees", params],
    queryFn: () => employeeAPI.listEmployees(params),
    staleTime: 1000 * 60 * 5,
  })

  return {
    employees: data?.data || [],
    total: data?.total || 0,
    page: data?.page || 1,
    pageSize: data?.pageSize || 10,
    totalPages: data?.totalPages || 0,
    isLoading,
    isError,
    error,
    refetch,
  }
}

export function useEmployee(id?: string) {
  const {
    data,
    isLoading,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: ["employee", id],
    queryFn: () => employeeAPI.getEmployee(id!),
    enabled: !!id,
    staleTime: 1000 * 60 * 5,
  })

  return {
    employee: data,
    isLoading,
    isError,
    error,
    refetch,
  }
}

export function useCreateEmployee() {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: CreateEmployeeRequest) =>
      employeeAPI.createEmployee(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["employees"] })
      setError(null)
    },
    onError: (err: Error) => {
      const errorMessage =
        err?.message || "Failed to create employee"
      setError(errorMessage)
    },
  })

  return {
    createEmployee: mutateAsync,
    isLoading: isPending,
    error,
    setError,
  }
}

export function useUpdateEmployee(id: string) {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: UpdateEmployeeRequest) =>
      employeeAPI.updateEmployee(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["employees"] })
      queryClient.invalidateQueries({ queryKey: ["employee", id] })
      setError(null)
    },
    onError: (err: Error) => {
      const errorMessage =
        err?.message || "Failed to update employee"
      setError(errorMessage)
    },
  })

  return {
    updateEmployee: mutateAsync,
    isLoading: isPending,
    error,
    setError,
  }
}

export function useTerminateEmployee() {
  const queryClient = useQueryClient()
  const [error, setError] = useState<string | null>(null)

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (body: TerminateEmployeeRequest) =>
      employeeAPI.terminateEmployee(body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["employees"] })
      setError(null)
    },
    onError: (err: Error) => {
      const errorMessage =
        err?.message || "Failed to terminate employee"
      setError(errorMessage)
    },
  })

  return {
    terminateEmployee: mutateAsync,
    isLoading: isPending,
    error,
    setError,
  }
}
