"use client"

import { RegisterForm } from "@/components/register-form"
import { GalleryVerticalEndIcon } from "lucide-react"
import { Suspense, useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuthStore } from "@/lib/store/auth"
import Image from "next/image"

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
    <div className="flex h-svh flex-col  justify-center items-center">
      <div className="flex flex-col gap-4 p-6 md:p-10 w-full">
        <div className="flex justify-center gap-2 md:justify-start">
          <a href="#" className="flex items-center gap-2 font-medium">
            <div className="flex items-center justify-center">
                  <Image src={"/logo.png"} alt="AtlasHR" width={200} height={200} className="-my-10" />
            </div>
          </a>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-2xl ">
            <Suspense fallback={<div className="text-sm text-muted-foreground">Loading...</div>}>
              <RegisterForm />
            </Suspense>
          </div>
        </div>
      </div>
    </div>
  )
}
