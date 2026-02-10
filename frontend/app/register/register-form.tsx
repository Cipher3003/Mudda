"use client";

import { SubmitButton } from "../components/submit-btn";
// TODO: add confirm password field
export function RegisterForm({
  action,
  children,
}: {
  action: (formData: FormData) => Promise<void>;
  children: React.ReactNode;
}) {
  return (
    <form
      action={action}
      className="flex flex-col space-y-4 bg-gray-50 px-4 py-8 sm:px-16"
    >
      <InputGroup
        id="username"
        label="Username"
        placeholder="iamuser"
        required
      />
      <InputGroup
        id="name"
        label="Full Name"
        placeholder="John Doe"
        autoComplete="name"
        required
      ></InputGroup>
      <InputGroup
        id="email"
        label="Email Address"
        placeholder="user@acme.com"
        autoComplete="email"
        required
      ></InputGroup>
      <InputGroup
        id="date_of_birth"
        label="Date of Birth"
        type="date"
        placeholder="1990-01-01"
        autoComplete="bday"
        required
      ></InputGroup>
      <InputGroup
        id="phone"
        label="Phone Number"
        type="tel"
        placeholder="(123) 456-7890"
        autoComplete="tel"
        required
      ></InputGroup>
      <InputGroup
        id="password"
        label="Password"
        type="password"
        minLength={8}
        maxLength={64}
        required
      ></InputGroup>
      <InputGroup
        id="profile_picture"
        label="Profile Picture URL"
        placeholder="https://example.com/profile.jpg"
        autoComplete="url"
        type="url"
      ></InputGroup>

      <SubmitButton>Sign Up</SubmitButton>
      {children}
    </form>
  );
}

function InputGroup({ label, id, ...props }: any) {
  return (
    <div>
      <label htmlFor={id} className="block text-xs text-gray-600 uppercase">
        {label}
      </label>
      <input
        id={id}
        name={id}
        className="mt-1 block w-full appearance-none rounded-md border border-gray-300 
        px-3 py-2 placeholder-gray-400 shadow-sm focus:border-black focus:outline-none
        focus:ring-black sm:text-sm"
        {...props}
      />
    </div>
  );
}
