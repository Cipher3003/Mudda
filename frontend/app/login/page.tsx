"use client";

import Link from "next/link";
import { SubmitButton } from "../components/submit-btn";
import { LoginForm } from "./login-form";
import { api } from "../lib/api";
import { useRouter } from "next/navigation";
import { LoginRequest } from "../types/auth";

// TODO: make a failsafe to allow login,register when in incognito mode (without cookies),
// ask backend for cookie or handshake
// TODO: redirect user when logged in from login and register page
// TODO: store user info in context to show in navbar
export default function Login() {
  const router = useRouter();
  async function login(formData: FormData) {
    const xsrfToken = document.cookie
      .split("; ")
      .find((row) => row.startsWith("XSRF-TOKEN="))
      ?.split("=")[1];

    const payload: LoginRequest = {
      username: formData.get("username") as string,
      password: formData.get("password") as string,
      "remember-me": formData.get("remember-me") as string,
    };

    try {
      const response = await api.login(payload, xsrfToken || "");

      if (response.ok) {
        router.refresh();
        router.push("/feed");
      } else if (response.status === 401)
        alert("Invalid username or password. Please try again.");
      else {
        const data = await response.json();
        console.error("Login failed:", data);
        alert(`Login failed. ${data.message || "Please try again."}`);
      }
    } catch (error) {
      console.error("Network error:", error);
      alert("Could not connect to backend. Please try again.");
    }
  }

  return (
    <div className="flex h-screen w-screen items-center justify-center bg-gray-50">
      <div className="z-10 w-full max-w-md overflow-hidden rounded-2xl border border-gray-100 shadow-xl">
        <div
          className="flex flex-col items-center justify-center space-y-3 border-b border-gray-200
         bg-white px-4 py-6 pt-8 text-center sm:px-16"
        >
          <h3 className="text-xl font-semibold">Sign In</h3>
          <p className="text-sm text-gray-500">
            Use your username and password to sign in
          </p>
        </div>
        <LoginForm action={login}>
          <SubmitButton>Sign in</SubmitButton>
          <p className="text-center text-sm text-gray-600">
            {"Don't have an account? "}
            <Link href="/register" className="font-semibold text-gray-800">
              Sign up
            </Link>
            {" for free."}
          </p>
        </LoginForm>
      </div>
    </div>
  );
}
