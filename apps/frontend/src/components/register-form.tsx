import { useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { AxiosError } from "axios"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
  FieldSeparator,
} from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { registerSchema, type RegisterFormValues } from "@/lib/validators/auth"
import { useAuth } from "@/hooks/useAuth"

export function RegisterForm({
  className,
  ...props
}: React.ComponentProps<"form">) {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { register: registerUser, isLoading, error: authError, setError } = useAuth()
  const [formError, setFormError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    mode: "onBlur",
  })

  const onSubmit = async (data: RegisterFormValues) => {
    setFormError(null)
    setError(null)

    try {
      await registerUser({
        username: data.username,
        email: data.email,
        password: data.password,
      })
      // Redirect to dashboard on successful registration
      const redirect = searchParams.get("redirect") || "/dashboard"
      router.push(redirect)
    } catch (err) {
      const errorMessage = err instanceof Error
        ? err.message
        : "Registration failed. Please try again."
      setFormError(errorMessage)
    }
  }

  const isSubmittingForm = isSubmitting || isLoading
  const displayError = formError || authError

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className={cn("flex flex-col gap-6", className)}
      {...props}
    >
      <FieldGroup>
        <div className="flex flex-col items-center gap-1 text-center">
          <h1 className="text-2xl font-bold">Create an account</h1>
          <p className="text-sm text-balance text-muted-foreground">
            Enter your details below to sign up
          </p>
        </div>

        {displayError && (
          <div className="p-3 bg-red-50 border border-red-200 rounded text-sm text-red-800">
            {displayError}
          </div>
        )}

        <Field>
          <FieldLabel htmlFor="username">Username</FieldLabel>
          <Input
            id="username"
            type="text"
            placeholder="john.doe"
            disabled={isSubmittingForm}
            {...register("username")}
          />
          {errors.username && (
            <p className="text-sm text-red-500">{errors.username.message}</p>
          )}
        </Field>

        <Field>
          <FieldLabel htmlFor="email">Email</FieldLabel>
          <Input
            id="email"
            type="email"
            placeholder="john@example.com"
            disabled={isSubmittingForm}
            {...register("email")}
          />
          {errors.email && (
            <p className="text-sm text-red-500">{errors.email.message}</p>
          )}
        </Field>

        <Field>
          <FieldLabel htmlFor="password">Password</FieldLabel>
          <Input
            id="password"
            type="password"
            placeholder="••••••••"
            disabled={isSubmittingForm}
            {...register("password")}
          />
          {errors.password && (
            <p className="text-sm text-red-500">{errors.password.message}</p>
          )}
          <FieldDescription>
            Password must be at least 8 characters
          </FieldDescription>
        </Field>

        <Field>
          <FieldLabel htmlFor="confirmPassword">Confirm Password</FieldLabel>
          <Input
            id="confirmPassword"
            type="password"
            placeholder="••••••••"
            disabled={isSubmittingForm}
            {...register("confirmPassword")}
          />
          {errors.confirmPassword && (
            <p className="text-sm text-red-500">
              {errors.confirmPassword.message}
            </p>
          )}
        </Field>

        <Field>
          <Button
            type="submit"
            disabled={isSubmittingForm}
            className="w-full"
          >
            {isSubmittingForm ? "Creating account..." : "Sign up"}
          </Button>
        </Field>

        <FieldSeparator>Already have an account?</FieldSeparator>

        <Field>
          <Button
            variant="outline"
            type="button"
            disabled={isSubmittingForm}
            className="w-full"
            onClick={() => router.push("/")}
          >
            Back to login
          </Button>
          <FieldDescription className="text-center">
            By clicking Sign up, you agree to our Terms of Service
          </FieldDescription>
        </Field>
      </FieldGroup>
    </form>
  )
}
