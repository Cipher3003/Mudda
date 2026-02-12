import { LoginRequest, RegisterRequest } from "../types/auth";

const BASE_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080/";

export const api = {
  register: async (payload: RegisterRequest, xsrfToken: string) => {
    return await fetch(`${BASE_URL}auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": xsrfToken || "",
      },
      credentials: "include",
      body: JSON.stringify(payload),
    });
  },

  login: async (payload: LoginRequest, xsrfToken: string) => {
    const params = new URLSearchParams();
    Object.entries(payload).forEach(([key, value]) => {
      if (value !== undefined) params.append(key, String(value));
    });

    return await fetch(`${BASE_URL}login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "X-XSRF-TOKEN": xsrfToken || "",
      },
      credentials: "include",
      body: params,
      // redirect:"follow"  // redirects to where backend sends us
    });
  },
};

export const apiClient = {
  get: async <T>(endpoint: string, headers: HeadersInit = {}): Promise<T> => {
    const response = await fetch(`${BASE_URL}${endpoint}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        ...headers,
      },
      credentials: "include",
    });

    if (!response.ok)
      throw new Error(`API Error: ${response.status} ${response.statusText}`);

    return response.json();
  },
};
