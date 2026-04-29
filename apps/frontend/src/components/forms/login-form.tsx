"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { z } from "zod";
import { useToast } from "@/components/ui/toast";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Form } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useAuthStore } from "@/lib/store/auth.store";

const loginSchema = z.object({
  username: z.string().min(2, "Enter your username."),
  password: z.string().min(6, "Password must be at least 6 characters."),
});

type LoginFormState = z.infer<typeof loginSchema>;

export function LoginForm() {
  const router = useRouter();
  const login = useAuthStore((state) => state.login);
  const { toast } = useToast();
  const [values, setValues] = useState<LoginFormState>({ username: "", password: "" });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const parseResult = loginSchema.safeParse(values);
    if (!parseResult.success) {
      toast({
        title: "Invalid login",
        description: parseResult.error.errors[0].message,
        variant: "error",
      });
      return;
    }

    setIsSubmitting(true);

    try {
      await login(values);
      toast({
        title: "Login successful",
        description: "Redirecting to your dashboard.",
        variant: "success",
      });
      router.push("/dashboard");
    } catch (error) {
      toast({
        title: "Authentication failed",
        description: "Please check your credentials and try again.",
        variant: "error",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Card className="w-full max-w-xl rounded-[2rem] border-slate-200 bg-white p-8 shadow-soft sm:p-10">
      <CardHeader>
        <CardTitle>Sign in to AtlasHR</CardTitle>
        <CardDescription>Use your HRMS account credentials to access the team dashboard.</CardDescription>
      </CardHeader>
      <CardContent>
        <Form onSubmit={handleSubmit}>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                name="username"
                value={values.username}
                onChange={(event) => setValues({ ...values, username: event.target.value })}
                placeholder="Enter username"
                autoComplete="username"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                name="password"
                type="password"
                value={values.password}
                onChange={(event) => setValues({ ...values, password: event.target.value })}
                placeholder="Enter password"
                autoComplete="current-password"
              />
            </div>
          </div>
          <Button type="submit" className="mt-6 w-full" disabled={isSubmitting}>
            {isSubmitting ? "Signing in..." : "Sign in"}
          </Button>
        </Form>
      </CardContent>
    </Card>
  );
}
