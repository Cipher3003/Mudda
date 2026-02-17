"use client";

import { createContext, useContext, useState } from "react";

export type MuddaUserInfo = {
  username: string;
  profileImage: string;
  role: "CITIZEN" | "GOVERNMENT" | "CREATOR" | "GUEST";
};

interface UserContextType {
  user: MuddaUserInfo | null;
  logout: () => void;
}

export interface UserProviderProps {
  user: MuddaUserInfo;
  children: React.ReactNode;
}

const GUEST_USER: MuddaUserInfo = {
  username: "GUEST",
  profileImage: "/guest-avatar.svg",
  role: "GUEST",
};

const UserContext = createContext<UserContextType | undefined>(undefined);

export function UserProvider(props: UserProviderProps) {
  const [user, setUser] = useState<MuddaUserInfo | null>(props.user);

  const logout = () => setUser(null);

  return (
    <UserContext.Provider value={{ user, logout }}>
      {props.children}
    </UserContext.Provider>
  );
}

export const useUser = () => {
  const context = useContext(UserContext);
  if (context === undefined)
    throw new Error("useUser must be used within UserProvider");

  const user = context.user ?? GUEST_USER;
  return {
    user,
    logout: context.logout,
  };
};
