import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function HomePage() {
  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-12 sm:px-6 lg:px-8">
      <Card className="w-full max-w-3xl overflow-hidden bg-white shadow-soft">
        <div className="grid gap-10 p-8 sm:p-12 md:grid-cols-[1.2fr_0.8fr]">
          <section className="space-y-6">
            <CardHeader>
              <CardTitle>AtlasHR SaaS Dashboard</CardTitle>
              <CardDescription>
                A lightweight HR management frontend built for microservices, JWT auth, and Spring Boot API gateway.
              </CardDescription>
            </CardHeader>
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="rounded-3xl border border-slate-200 bg-slate-50 p-5 shadow-sm">
                <p className="text-sm font-semibold text-slate-700">Authentication Layer</p>
                <p className="mt-2 text-sm text-slate-600">JWT login + refresh tokens with /api/auth endpoints.</p>
              </div>
              <div className="rounded-3xl border border-slate-200 bg-slate-50 p-5 shadow-sm">
                <p className="text-sm font-semibold text-slate-700">Dashboard Shell</p>
                <p className="mt-2 text-sm text-slate-600">Responsive sidebar, navbar, and modular dashboard layout.</p>
              </div>
            </div>
          </section>
          <section className="flex flex-col items-start justify-between gap-6 rounded-3xl border border-slate-200 bg-slate-950 p-8 text-slate-50 shadow-soft sm:p-10">
            <div className="space-y-4">
              <p className="text-sm uppercase tracking-[0.24em] text-slate-400">Ready for production</p>
              <h2 className="text-3xl font-semibold tracking-tight">Secure microservice-ready HR platform.</h2>
              <p className="text-sm leading-7 text-slate-300">
                Connects to Spring Boot API gateway on <span className="font-semibold">/api</span>. Built with Next.js App Router,
                Tailwind, Axios, React Query, Zod, and Zustand.
              </p>
            </div>
            <Link href="/login" className="w-full sm:w-auto">
              <Button className="w-full sm:w-auto">Go to Login</Button>
            </Link>
          </section>
        </div>
      </Card>
    </main>
  );
}
