"use client"

import Link from "next/link"
import Image from "next/image"
import { GalleryVerticalEndIcon } from "lucide-react"
import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuthStore } from "@/lib/store/auth"
import { Button } from "@/app/components/ui/button"

export default function RegisterPage() {
  const router = useRouter()
  const { isAuthenticated } = useAuthStore()

  // Redirect to dashboard if already logged in
  useEffect(() => {
    if (isAuthenticated) {
      router.push("/dashboard")
    }
  }, [isAuthenticated, router])

  return (
    <div className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
          <Link href="/" className="flex items-center gap-2 font-medium">
            <div className="flex size-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <GalleryVerticalEndIcon className="size-4" />
            </div>
            AtlasHR
          </Link>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs space-y-4 text-center">
            <div>
              <h1 className="text-2xl font-bold">Registration Disabled</h1>
              <p className="text-sm text-muted-foreground mt-2">
                Public registration is no longer available. Users are now onboarded by administrators.
              </p>
            </div>
            <div className="space-y-2">
              <p className="text-sm text-muted-foreground">
                If you need access to AtlasHR, please contact your administrator.
              </p>
              <p className="text-sm text-muted-foreground">
                If you already have credentials, please log in below.
              </p>
            </div>
            <Button 
              onClick={() => router.push("/login")}
              className="w-full"
            >
              Go to Login
            </Button>
          </div>
        </div>
      </div>
      <div className="relative hidden bg-muted lg:block">
        <Image
          src="/placeholder.svg"
          alt="Illustration"
          fill
          className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
          sizes="100vw"
        />
      </div>
    </div>
  )
}

