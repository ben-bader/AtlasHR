import type { Metadata } from "next";
import { Providers } from "./providers";
import "./globals.css";

export const metadata: Metadata = {
  title: "AtlasHR Dashboard",
  description: "HRMS microservices dashboard for AtlasHR",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className="min-h-screen bg-slate-50 text-slate-950 antialiased">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
