"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Form, FormGroup, FormLabel, FormDescription, FormMessage } from "@/components/ui/form"
import { useCreateEmployee, useUpdateEmployee, useEmployee } from "@/hooks/useEmployee"
import { employeeCreateSchema, type EmployeeCreateFormValues } from "@/lib/validators/employee"

interface EmployeeFormProps {
  employeeId?: string
  onSuccess?: () => void
}

export function EmployeeFormView({ employeeId, onSuccess }: EmployeeFormProps) {
  const router = useRouter()
  const isEditMode = !!employeeId

  // Fetch employee data if editing
  const { employee: existingEmployee, isLoading: isLoadingEmployee } = useEmployee(employeeId)

  // Mutations
  const { createEmployee, isLoading: isCreating, error: createError, setError: setCreateError } = useCreateEmployee()
  const { updateEmployee, isLoading: isUpdating, error: updateError, setError: setUpdateError } = useUpdateEmployee(employeeId || "")

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

  // Pre-fill form when editing
  useEffect(() => {
    if (isEditMode && existingEmployee) {
      reset({
        firstName: existingEmployee.firstName,
        lastName: existingEmployee.lastName,
        email: existingEmployee.email,
        phone: existingEmployee.phone,
        department: existingEmployee.department,
        designation: existingEmployee.designation,
        joinDate: existingEmployee.joinDate,
        dateOfBirth: existingEmployee.dateOfBirth,
        address: existingEmployee.address,
        city: existingEmployee.city,
        state: existingEmployee.state,
        zipCode: existingEmployee.zipCode,
        country: existingEmployee.country,
        employmentType: existingEmployee.employmentType,
        status: existingEmployee.status,
      })
    }
  }, [isEditMode, existingEmployee, reset])

  const onSubmit = async (data: EmployeeCreateFormValues) => {
    setCreateError(null)
    setUpdateError(null)

    try {
      if (isEditMode && employeeId) {
        updateEmployee(data)
      } else {
        createEmployee(data)
      }

      // Redirect after a short delay to allow mutation to complete
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
          {isEditMode ? "Edit Employee" : "Add New Employee"}
        </h1>
        <p className="text-muted-foreground mt-1">
          {isEditMode ? "Update employee information" : "Enter the employee's details below"}
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
            <Input
              id="firstName"
              placeholder="John"
              disabled={isLoading}
              {...register("firstName")}
            />
            {errors.firstName && (
              <FormMessage>{errors.firstName.message}</FormMessage>
            )}
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="lastName">Last Name *</FormLabel>
            <Input
              id="lastName"
              placeholder="Doe"
              disabled={isLoading}
              {...register("lastName")}
            />
            {errors.lastName && (
              <FormMessage>{errors.lastName.message}</FormMessage>
            )}
          </FormGroup>
        </div>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="email">Email *</FormLabel>
            <Input
              id="email"
              type="email"
              placeholder="john@example.com"
              disabled={isLoading}
              {...register("email")}
            />
            {errors.email && (
              <FormMessage>{errors.email.message}</FormMessage>
            )}
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="phone">Phone</FormLabel>
            <Input
              id="phone"
              type="tel"
              placeholder="+1 (555) 000-0000"
              disabled={isLoading}
              {...register("phone")}
            />
          </FormGroup>
        </div>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="department">Department</FormLabel>
            <Input
              id="department"
              placeholder="Engineering"
              disabled={isLoading}
              {...register("department")}
            />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="designation">Position</FormLabel>
            <Input
              id="designation"
              placeholder="Senior Developer"
              disabled={isLoading}
              {...register("designation")}
            />
          </FormGroup>
        </div>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="joinDate">Join Date</FormLabel>
            <Input
              id="joinDate"
              type="date"
              disabled={isLoading}
              {...register("joinDate")}
            />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="dateOfBirth">Date of Birth</FormLabel>
            <Input
              id="dateOfBirth"
              type="date"
              disabled={isLoading}
              {...register("dateOfBirth")}
            />
          </FormGroup>
        </div>

        <div className="grid grid-cols-2 gap-6">
          <FormGroup>
            <FormLabel htmlFor="employmentType">Employment Type</FormLabel>
            <Input
              id="employmentType"
              placeholder="FULL_TIME"
              disabled={isLoading}
              {...register("employmentType")}
            />
            <FormDescription>e.g., FULL_TIME, PART_TIME, CONTRACT</FormDescription>
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="status">Status</FormLabel>
            <Input
              id="status"
              placeholder="ACTIVE"
              disabled={isLoading}
              {...register("status")}
            />
            <FormDescription>e.g., ACTIVE, INACTIVE, ON_LEAVE</FormDescription>
          </FormGroup>
        </div>

        <FormGroup>
          <FormLabel htmlFor="address">Address</FormLabel>
          <Input
            id="address"
            placeholder="123 Main St"
            disabled={isLoading}
            {...register("address")}
          />
        </FormGroup>

        <div className="grid grid-cols-3 gap-6">
          <FormGroup>
            <FormLabel htmlFor="city">City</FormLabel>
            <Input
              id="city"
              placeholder="New York"
              disabled={isLoading}
              {...register("city")}
            />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="state">State</FormLabel>
            <Input
              id="state"
              placeholder="NY"
              disabled={isLoading}
              {...register("state")}
            />
          </FormGroup>

          <FormGroup>
            <FormLabel htmlFor="zipCode">Zip Code</FormLabel>
            <Input
              id="zipCode"
              placeholder="10001"
              disabled={isLoading}
              {...register("zipCode")}
            />
          </FormGroup>
        </div>

        <FormGroup>
          <FormLabel htmlFor="country">Country</FormLabel>
          <Input
            id="country"
            placeholder="United States"
            disabled={isLoading}
            {...register("country")}
          />
        </FormGroup>

        <div className="flex gap-4 pt-6">
          <Button
            type="submit"
            disabled={isLoading || isSubmitting}
            className="flex-1"
          >
            {isLoading ? "Saving..." : isEditMode ? "Update Employee" : "Create Employee"}
          </Button>
          <Button
            variant="outline"
            type="button"
            onClick={() => router.push("/dashboard/employees")}
            disabled={isLoading}
          >
            Cancel
          </Button>
        </div>
      </Form>
    </div>
  )
}
