import { MuddaUserInfo, UserProvider } from "../context/user-context";
import { ApiError } from "../lib/api-error";
import { apiServer } from "../lib/api-server";
import LeftSidebar from "./_components/left-sidebar";

async function getInitialUser(): Promise<MuddaUserInfo> {
  // TODO: update with more user info
  const guestUser: MuddaUserInfo = {
    username: "GUEST",
    profileImage: "/guest-avatar.svg",
    role: "GUEST",
  };

  try {
    const response = await apiServer.get<{ username: string }>("account/me");
    return {
      username: response.username,
      profileImage: "",
      role: "CITIZEN",
    };
  } catch (error) {
    if (error instanceof ApiError) {
      if (error.status === 401) return guestUser;
    }

    console.error("Failed to fetch initial user", error);
    return guestUser;
  }
}

export default async function FeedLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const user = await getInitialUser();
  return (
    <UserProvider user={user}>
      <div className="min-h-screen bg-slate-100 font-sans selection:bg-blue-100">
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-12 gap-6 px-4">
          <LeftSidebar />
          <div className="col-span-1 md:col-span-12 lg:col-span-9">
            {children}
          </div>
        </div>
      </div>
    </UserProvider>
  );
}
