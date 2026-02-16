"use client";

import { api, BASE_URL } from "./api";
import { RegisterRequest, LoginRequest } from "../types/auth";

const getXsrfToken = () =>
  document.cookie
    .split("; ")
    .find((row) => row.startsWith("XSRF-TOKEN="))
    ?.split("=")[1];

export const apiClient = {
  register: async (payload: RegisterRequest) => {
    return await fetch(`${BASE_URL}auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": getXsrfToken() || "",
      },
      credentials: "include",
      body: JSON.stringify(payload),
      cache: "no-store",
    });
  },

  login: async (payload: LoginRequest) => {
    const params = new URLSearchParams();
    Object.entries(payload).forEach(([key, value]) => {
      if (value !== undefined) params.append(key, String(value));
    });

    return await fetch(`${BASE_URL}login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "X-XSRF-TOKEN": getXsrfToken() || "",
      },
      credentials: "include",
      body: params,
      cache: "no-store",
      // redirect:"follow"  // redirects to where backend sends us
    });
  },

  get: <T>(endpoint: string): Promise<T> =>
    api<T>(`${BASE_URL}${endpoint}`, {
      credentials: "include",
    }),

  post: async <T>(endpoint: string, body?: unknown): Promise<T> =>
    api<T>(`${BASE_URL}${endpoint}`, {
      method: "POST",
      body,
      credentials: "include",
      headers: {
        "X-XSRF-TOKEN": getXsrfToken() || "",
      },
    }),

  delete: async <T>(endpoint: string): Promise<T> =>
    api<T>(`${BASE_URL}${endpoint}`, {
      method: "DELETE",
      credentials: "include",
      headers: {
        "X-XSRF-TOKEN": getXsrfToken() || "",
      },
    }),
};
