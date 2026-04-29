import api from "@/lib/api/axios";

export type LoginPayload = {
  username: string;
  password: string;
};

export type AuthTokens = {
  accessToken: string;
  refreshToken: string;
};

export type MeResponse = {
  id: string;
  username: string;
  email?: string;
  role?: string;
};

export async function loginRequest(payload: LoginPayload) {
  const response = await api.post<AuthTokens>("/auth/login", payload);
  return response.data;
}

export async function refreshRequest(refreshToken: string) {
  const response = await api.post<AuthTokens>("/auth/refresh", { refreshToken });
  return response.data;
}

export async function meRequest() {
  const response = await api.get<MeResponse>("/auth/me");
  return response.data;
}
