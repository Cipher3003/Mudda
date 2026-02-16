"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { RegisterForm } from "./register-form";
import { apiClient } from "../lib/api-client";
import { RegisterRequest } from "../types/auth";

export default function Register() {
  const router = useRouter();

  async function register(formData: FormData) {
    const payload: RegisterRequest = {
      username: formData.get("username") as string,
      name: formData.get("name") as string,
      email: formData.get("email") as string,
      dateOfBirth: formData.get("date_of_birth") as string,
      phoneNumber: formData.get("phone") as string,
      password: formData.get("password") as string,
      role: "CITIZEN",
      profileImageUrl: formData.get("profile_picture") as string,
    };

    try {
      const response = await apiClient.register(payload);

      if (response.ok) router.push("/login");
      else {
        const data = await response.json();
        console.error("Registration failed:", data);
        alert(`Registration failed. ${data.message || "Please try again."}`);
      }
    } catch (error) {
      console.error("Network error:", error);
      alert("Could not connect to backend. Please try again.");
    }
  }

  return (
    <div className="flex h-screen w-screen items-center justify-center bg-gray-50">
      <div className="z-10 w-full max-w-md overflow-hidden rounded-2xl border border-gray-100 shadow-xl">
        {/* Header Section */}
        <div
          className="flex flex-col items-center justify-center space-y-3 border-b 
        border-gray-200 bg-white px-4 py-6 pt-8 text-center sm:px-16"
        >
          <h3 className="text-xl font-semibold">Sign Up</h3>
          <p className="text-sm text-gray-500">Create an account on Mudda</p>
        </div>

        <RegisterForm action={register}>
          <p className="text-center text-sm text-gray-600">
            {"Already have an account? "}
            <Link href="/login" className="font-semibold text-gray-800">
              Sign in
            </Link>
            {" instead."}
          </p>
        </RegisterForm>
      </div>
    </div>
  );
}
