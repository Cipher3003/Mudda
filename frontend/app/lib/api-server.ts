import { cookies } from "next/headers";
import { api, BASE_URL } from "./api";

const getServerHeaders = async () => {
  const cookieStore = await cookies();
  return { Cookie: cookieStore.toString() };
};

export const apiServer = {
  get: async <T>(endpoint: string): Promise<T> =>
    api<T>(`${BASE_URL}${endpoint}`, {
      headers: await getServerHeaders(),
    }),

  post: async <T>(endpoint: string, body?: unknown): Promise<T> =>
    api<T>(`${BASE_URL}${endpoint}`, {
      method: "POST",
      body,
      headers: await getServerHeaders(),
    }),

  delete: async <T>(endpoint: string): Promise<T> =>
    api<T>(`${BASE_URL}${endpoint}`, {
      method: "DELETE",
      headers: await getServerHeaders(),
    }),
};
