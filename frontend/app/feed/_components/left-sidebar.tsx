import {
  Home,
  Bell,
  Bookmark,
  User,
  TrendingUp,
  CheckCircle,
  Map,
} from "lucide-react";
import ProfileMiniCard from "./profile-mini-card";

export default function LeftSidebar() {
  return (
    <aside className="hidden md:flex col-span-3 flex-col sticky top-0 h-screen py-6 overflow-y-auto">
      {/* 1. Branding */}
      <div className="px-4 mb-8 flex items-center gap-2 text-blue-700">
        <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold text-xl">
          C
        </div>
        <span className="text-xl font-bold tracking-tight text-slate-900">
          CivicVoice
        </span>
      </div>

      {/* 2. Navigation List (Clean, not blocky) */}
      <nav className="flex-1 space-y-1 px-4">
        <NavItem icon={<Home size={20} />} text="Feed" active />
        <NavItem icon={<Map size={20} />} text="Explore Map" />
        <NavItem icon={<Bell size={20} />} text="Notifications" badge="3" />
        <NavItem icon={<Bookmark size={20} />} text="Saved Issues" />
        <NavItem icon={<User size={20} />} text="Profile" />

        <div className="pt-4 mt-4 border-t border-slate-200">
          <p className="px-2 text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">
            Community
          </p>
          <NavItem icon={<TrendingUp size={20} />} text="Neighborhood Stats" />
        </div>
      </nav>

      {/* 3. "Your Impact" Widget (You liked this) */}
      <div className="mx-4 mt-6 bg-linear-to-br from-blue-600 to-blue-700 rounded-2xl p-5 text-white shadow-lg shadow-blue-200">
        <div className="flex justify-between items-start mb-2">
          <div className="p-2 bg-white/10 rounded-lg backdrop-blur-sm">
            <CheckCircle size={20} className="text-blue-100" />
          </div>
          <span className="text-xs font-medium bg-white/20 px-2 py-1 rounded text-blue-50">
            This Month
          </span>
        </div>
        <div className="text-3xl font-bold mb-1">12</div>
        <p className="text-sm text-blue-100 font-medium">
          Issues you helped resolve.
        </p>
      </div>

      <div className="mt-4 px-4">
        <ProfileMiniCard />
      </div>
    </aside>
  );
}

function NavItem({ icon, text, badge, active }: any) {
  return (
    <div
      className={`flex items-center justify-between px-4 py-3 rounded-xl cursor-pointer transition group ${
        active
          ? "bg-white shadow-sm text-blue-700 font-bold"
          : "text-slate-600 hover:bg-white hover:shadow-sm hover:text-slate-900"
      }`}
    >
      <div className="flex items-center gap-3">
        <span
          className={
            active
              ? "text-blue-600"
              : "text-slate-400 group-hover:text-slate-600"
          }
        >
          {icon}
        </span>
        <span>{text}</span>
      </div>
      {badge && (
        <span className="bg-red-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">
          {badge}
        </span>
      )}
    </div>
  );
}
