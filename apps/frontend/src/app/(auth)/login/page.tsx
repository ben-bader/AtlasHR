import { LoginForm } from "@/components/forms/login-form";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <p className="text-sm uppercase tracking-[0.3em] text-brand-600">AtlasHR</p>
          <h1 className="mt-4 text-3xl font-semibold text-slate-950">Sign in to your workspace</h1>
          <p className="mt-2 text-sm leading-6 text-slate-600">Secure access to employee, attendance, and payroll workflows.</p>
        </div>
        <LoginForm />
      </div>
    </main>
  );
}
