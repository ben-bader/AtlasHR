"use client"

import { LoginForm } from "@/components/login-form"
import { GalleryVerticalEndIcon } from "lucide-react"
import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuthStore } from "@/lib/store/auth"
import Image from "next/image"

export default function LoginPage() {
  const router = useRouter()
  const { isAuthenticated } = useAuthStore()

  // Redirect to dashboard if already logged in
  useEffect(() => {
    if (isAuthenticated) {
      router.push("/dashboard")
    }
  }, [isAuthenticated, router])

  return (
    <div className="grid h-dvh lg:grid-cols-2">
      <div className="flex flex-col gap-2 md:p-10">
        <div className="flex justify-center md:justify-start">
            <div className="flex items-center justify-center">
              <Image src={"/logo.png"} alt="AtlasHR" width={200} height={200} className="-my-5"/>
            </div>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
            <LoginForm />
          </div>
        </div>
      </div>
      <div className="relative hidden bg-muted lg:block">
        <img
          src="/placeholder.svg"
          alt="Image"
          className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
        />
      </div>
    </div>
  )
}
