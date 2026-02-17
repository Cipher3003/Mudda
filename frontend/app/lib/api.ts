import { ApiError } from "./api-error";

export const BASE_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080/";

export type FetchOptions = {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  headers?: HeadersInit;
  body?: unknown;
  credentials?: RequestCredentials;
  cache?: RequestCache;
};

export async function api<T>(
  url: string,
  options: FetchOptions = {},
): Promise<T> {
  const response = await fetch(url, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    credentials: options.credentials,
    cache: options.cache ?? "no-store",
  });

  if (!response.ok) {
    const body = response.json();
    throw new ApiError(response.status, body);
  }

  if (response.status === 204) return {} as T;

  return response.json();
}
