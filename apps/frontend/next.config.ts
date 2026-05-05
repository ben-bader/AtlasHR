import type { NextConfig } from "next";
const nextConfig : NextConfig = {
  output: "standalone",
  reactStrictMode: true,
  env: {
    NEXT_PUBLIC_APP_NAME: "HRMS",
  },
};

export default nextConfig;
