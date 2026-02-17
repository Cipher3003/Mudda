"use client";

import { useUser } from "@/app/context/user-context";
import Avatar from "./avatar";

interface UserAvatarProps {
  size?: "sm" | "md" | "lg";
  className?: string;
}

export default function UserAvatar({ size, className }: UserAvatarProps) {
  const { user } = useUser();

  if (!user)
    return (
      <div className="w-10 h-10 bg-slate-100 animate-pulse rounded-full" />
    );

  return (
    <Avatar
      src={user.profileImage}
      name={user.username}
      size={size}
      className={className}
    />
  );
}
