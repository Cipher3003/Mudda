"use client";

import { useState } from "react";
import Link from "next/link";
import { LogOut, MoreHorizontal, User, LogIn } from "lucide-react";
import { useUser } from "@/app/context/user-context";
import { apiClient } from "@/app/lib/api-client";
import { useRouter } from "next/navigation";
import UserAvatar from "./user-avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

interface MenuContentProps {
  onLogout: () => Promise<void>;
}

export default function ProfileMiniCard() {
  const { user, logout } = useUser();
  const router = useRouter();
  const [isOpen, setIsOpen] = useState(false);

  const isGuest = user.role === "GUEST";
  const displayName = user.username || "Guest User";
  const handle = `@${displayName.toLowerCase().replace(/\s+/g, "")}`;

  async function handleLogout() {
    try {
      const response = await apiClient.logout();
      if (response.ok) {
        logout();
        router.refresh();
      }
    } catch (error) {
      console.error("Logout failed", error);
    }
  }

  return (
    <div className="flex items-center gap-3 p-2 hover:bg-slate-200 rounded-xl transition-all duration-200 group w-full">
      <UserAvatar size="md" />

      <div className="flex-1 min-w-0 leading-tight">
        <p className="text-sm font-semibold text-slate-900 truncate">
          {displayName}
        </p>
        <p className="text-xs text-slate-500 truncate">
          {isGuest ? "Welcome to Mudda" : handle}
        </p>
      </div>

      <DropdownMenu onOpenChange={setIsOpen}>
        <DropdownMenuTrigger asChild>
          <button
            className={`p-2 hover:bg-slate-200 rounded-lg transition-opacity outline-none 
            ${isOpen ? "opacity-100 bg-slate-200" : "opacity-0 group-hover:opacity-100"}`}
          >
            <MoreHorizontal size={18} className="text-slate-600" />
            <span className="sr-only">User settings</span>
          </button>
        </DropdownMenuTrigger>

        <DropdownMenuContent
          align="end"
          side="top"
          className="w-48 p-1 shadow-xl"
        >
          {isGuest ? <GuestMenu /> : <UserMenu onLogout={handleLogout} />}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}

function UserMenu({ onLogout }: MenuContentProps) {
  return (
    <>
      <DropdownMenuItem asChild className="cursor-pointer gap-2 py-2">
        <Link href="/profile">
          <User size={16} />
          <span>My Profile</span>
        </Link>
      </DropdownMenuItem>

      <DropdownMenuSeparator />

      <DropdownMenuItem
        onClick={onLogout}
        className="cursor-pointer gap-2 py-2 text-red-600 focus:text-red-600 focus:bg-red-50"
      >
        <LogOut size={16} />
        <span>Log out</span>
      </DropdownMenuItem>
    </>
  );
}

function GuestMenu() {
  return (
    <DropdownMenuItem
      asChild
      className="cursor-pointer gap-2 py-2 text-blue-600 focus:text-blue-600"
    >
      <Link href="/login">
        <LogIn size={16} />
        <span>Sign In</span>
      </Link>
    </DropdownMenuItem>
  );
}
