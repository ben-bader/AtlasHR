import { useState } from "react"
import { useRouter } from "next/navigation"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { cn } from "@/lib/utils"
import { Button } from "@/app/components/ui/button"
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from "@/app/components/ui/field"
import { Input } from "@/app/components/ui/input"
import { registerSchema, type RegisterFormValues } from "@/lib/validators/auth"
import { useAuth } from "@/hooks/useAuth"

export function RegisterForm({
  className,
  ...props
}: React.ComponentProps<"form">) {
  const router = useRouter()
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
      router.push("/dashboard")
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
  className={cn("flex flex-col gap-4 w-full", className)}
  {...props}
>
  <FieldGroup className="space-y-4">
    <div className="flex flex-col items-center gap-1 text-center">
      <h1 className="text-2xl font-bold">Create an account</h1>
      <p className="text-sm text-muted-foreground">
        Enter your details below to sign up
      </p>
    </div>

    {displayError && (
      <div className="rounded-md border border-red-200 bg-red-50 p-3 text-sm text-red-800">
        {displayError}
      </div>
    )}

    {/* 2 Column Grid */}
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
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
          <p className="text-sm text-red-500">
            {errors.username.message}
          </p>
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
          <p className="text-sm text-red-500">
            {errors.email.message}
          </p>
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
          <p className="text-sm text-red-500">
            {errors.password.message}
          </p>
        )}
        <FieldDescription>
          Minimum 8 characters
        </FieldDescription>
      </Field>

      <Field>
        <FieldLabel htmlFor="confirmPassword">
          Confirm Password
        </FieldLabel>
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
    </div>

    {/* Actions */}
    <div className="flex items-center w-full justify-between gap-3 pt-2">
      
      <Button
        variant="outline"
        type="button"
        disabled={isSubmittingForm}
        className="w-[50%]"
        onClick={() => router.push("/")}
      >
        Back to login
      </Button>
      <Button
        type="submit"
        disabled={isSubmittingForm}
        className="w-[50%]"
      >
        {isSubmittingForm
          ? "Creating account..."
          : "Sign up"}
      </Button>


     
    </div>
     <FieldDescription className="text-center text-xs">
        By clicking Sign up, you agree to our Terms of Service
      </FieldDescription>
  </FieldGroup>
</form>
  )
}
