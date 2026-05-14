"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { useQuery } from "@tanstack/react-query"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Form, FormGroup, FormLabel, FormDescription, FormMessage } from "@/components/ui/form"
import { useCreateEmployee, useUpdateEmployee, useEmployee } from "@/hooks/useEmployee"
import { employeeCreateSchema, type EmployeeCreateFormValues } from "@/lib/validators/employee"
import type { CreateEmployeeRequest } from "@/lib/types"
import { departmentAPI } from "@/lib/api/department"
import { designationAPI } from "@/lib/api/designation"
import { cn } from "@/lib/utils"

const selectClassName = cn(
  "h-9 w-full min-w-0 rounded-md border border-input bg-transparent px-2.5 py-1 text-base shadow-xs outline-none",
  "focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 md:text-sm dark:bg-input/30"
)

interface EmployeeFormProps {
  employeeId?: string
  onSuccess?: () => void
}

function toPayload(data: EmployeeCreateFormValues): CreateEmployeeRequest {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    email: data.email,
    phone: data.phone,
    departmentId: data.departmentId ? Number(data.departmentId) : undefined,
    designationId: data.designationId || undefined,
    reportingManagerId: data.reportingManagerId || undefined,
    joinDate: data.joinDate,
    dateOfBirth: data.dateOfBirth,
    address: data.address,
    city: data.city,
    state: data.state,
    zipCode: data.zipCode,
    country: data.country,
  }
}

export function EmployeeFormView({ employeeId, onSuccess }: EmployeeFormProps) {
  const router = useRouter()
  const isEditMode = !!employeeId

  const { employee: existingEmployee, isLoading: isLoadingEmployee } = useEmployee(employeeId)

  const { data: departments = [] } = useQuery({
    queryKey: ["departments"],
    queryFn: () => departmentAPI.list(),
  })

  const { data: designations = [] } = useQuery({
    queryKey: ["designations"],
    queryFn: () => designationAPI.list(),
  })

  const { createEmployee, isLoading: isCreating, error: createError, setError: setCreateError } =
    useCreateEmployee()
  const { updateEmployee, isLoading: isUpdating, error: updateError, setError: setUpdateError } =
    useUpdateEmployee(employeeId || "")

  const isLoading = isCreating || isUpdating || (isEditMode && isLoadingEmployee)
  const error = createError || updateError

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<EmployeeCreateFormValues>({
    resolver: zodResolver(employeeCreateSchema),
    mode: "onBlur",
  })

  useEffect(() => {
    if (isEditMode && existingEmployee) {
      reset({
        firstName: existingEmployee.firstName,
        lastName: existingEmployee.lastName,
        email: existingEmployee.email,
        phone: existingEmployee.phone,
        departmentId: existingEmployee.departmentId?.toString() ?? "",
        designationId: existingEmployee.designationId ?? "",
        reportingManagerId: existingEmployee.reportingManagerId ?? "",
        joinDate: existingEmployee.joinDate?.slice(0, 10),
        dateOfBirth: existingEmployee.dateOfBirth?.slice(0, 10),
        address: existingEmployee.address,
        city: existingEmployee.city,
        state: existingEmployee.state,
        zipCode: existingEmployee.zipCode,
        country: existingEmployee.country,
      })
    }
  }, [isEditMode, existingEmployee, reset])

  const onSubmit = async (data: EmployeeCreateFormValues) => {
    setCreateError(null)
    setUpdateError(null)

    try {
      const payload = toPayload(data)
      if (isEditMode && employeeId) {
        updateEmployee(payload)
      } else {
        createEmployee(payload)
      }

      setTimeout(() => {
        if (onSuccess) {
          onSuccess()
        } else {
          router.push("/dashboard/employees")
        }
      }, 500)
    } catch (err) {
      console.error("Failed to save employee:", err)
    }
  }

  if (isLoadingEmployee) {
    return <div className="flex items-center justify-center h-96">Loading...</div>
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">
          {isEditMode ? "Edit Employee" : "Onboard Employee"}
        </h1>
        <p className="text-muted-foreground mt-1">
          {isEditMode ? "Update employee information" : "Maps to POST /employees/onboard"}
        </p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded text-sm text-red-800">
          {error}
        </div>
      )}

      <Form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="firstName">First Name *</FormLabel>
            <Input id="firstName" placeholder="John" disabled={isLoading} {...register("firstName")} />
            {errors.firstName && <FormMessage>{errors.firstName.message}</FormMessage>}
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="lastName">Last Name *</FormLabel>
            <Input id="lastName" placeholder="Doe" disabled={isLoading} {...register("lastName")} />
            {errors.lastName && <FormMessage>{errors.lastName.message}</FormMessage>}
          </FormGroup>
        </div>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="email">Email *</FormLabel>
            <Input id="email" type="email" placeholder="john@example.com" disabled={isLoading} {...register("email")} />
            {errors.email && <FormMessage>{errors.email.message}</FormMessage>}
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="phone">Phone</FormLabel>
            <Input id="phone" type="tel" placeholder="+212..." disabled={isLoading} {...register("phone")} />
          </FormGroup>
        </div>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="departmentId">Department</FormLabel>
            <select id="departmentId" className={selectClassName} disabled={isLoading} {...register("departmentId")}>
              <option value="">Select department</option>
              {departments.map((d) => (
                <option key={d.departmentId} value={d.departmentId}>
                  {d.departmentName}
                </option>
              ))}
            </select>
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="designationId">Designation</FormLabel>
            <select id="designationId" className={selectClassName} disabled={isLoading} {...register("designationId")}>
              <option value="">Select designation</option>
              {designations.map((d) => (
                <option key={d.designationId} value={d.designationId}>
                  {d.designationName}
                </option>
              ))}
            </select>
          </FormGroup>
        </div>

        <FormGroup>
          <FormLabel htmlFor="reportingManagerId">Reporting manager (employee ID)</FormLabel>
          <Input id="reportingManagerId" placeholder="EMP001" disabled={isLoading} {...register("reportingManagerId")} />
          <FormDescription>Optional — must match an existing employee id</FormDescription>
        </FormGroup>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="joinDate">Join date</FormLabel>
            <Input id="joinDate" type="date" disabled={isLoading} {...register("joinDate")} />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="dateOfBirth">Date of birth</FormLabel>
            <Input id="dateOfBirth" type="date" disabled={isLoading} {...register("dateOfBirth")} />
          </FormGroup>
        </div>

        <FormGroup>
          <FormLabel htmlFor="address">Address</FormLabel>
          <Input id="address" placeholder="Street" disabled={isLoading} {...register("address")} />
        </FormGroup>

        <div className="grid grid-cols-3 gap-6">
          <FormGroup>
            <FormLabel htmlFor="city">City</FormLabel>
            <Input id="city" disabled={isLoading} {...register("city")} />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="state">Province / state</FormLabel>
            <Input id="state" disabled={isLoading} {...register("state")} />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="zipCode">Postal code</FormLabel>
            <Input id="zipCode" disabled={isLoading} {...register("zipCode")} />
          </FormGroup>
        </div>

        <FormGroup>
          <FormLabel htmlFor="country">Country</FormLabel>
          <Input id="country" disabled={isLoading} {...register("country")} />
        </FormGroup>

        <div className="flex gap-4 pt-6">
          <Button type="submit" disabled={isLoading || isSubmitting} className="flex-1">
            {isLoading ? "Saving..." : isEditMode ? "Update employee" : "Onboard employee"}
          </Button>
          <Button variant="outline" type="button" onClick={() => router.push("/dashboard/employees")} disabled={isLoading}>
            Cancel
          </Button>
        </div>
      </Form>
    </div>
  )
}
