import {
  Home,
  Map,
  Bell,
  Bookmark,
  User,
  Image as ImageIcon,
  MapPin,
  Send,
  TrendingUp,
  AlertTriangle,
  CheckCircle,
} from "lucide-react";
import FeedStream from "./_components/feed-stream";
import { getIssueFeed } from "./adapter";

export default async function Feed() {
  const issues = await getIssueFeed();

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900 font-sans selection:bg-blue-100">
      <div className="max-w-7xl mx-auto grid grid-cols-12 gap-6 px-4">
        {/* --- LEFT COLUMN: NAVIGATION & PERSONAL --- */}
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
          <nav className="flex-1 space-y-1 px-2">
            <NavItem icon={<Home size={20} />} text="Feed" active />
            <NavItem icon={<Map size={20} />} text="Explore Map" />
            <NavItem icon={<Bell size={20} />} text="Notifications" badge="3" />
            <NavItem icon={<Bookmark size={20} />} text="Saved Issues" />
            <NavItem icon={<User size={20} />} text="Profile" />

            <div className="pt-4 mt-4 border-t border-slate-200">
              <p className="px-4 text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">
                Community
              </p>
              <NavItem
                icon={<TrendingUp size={20} />}
                text="Neighborhood Stats"
              />
            </div>
          </nav>

          {/* 3. "Your Impact" Widget (You liked this) */}
          <div className="mx-2 mt-6 bg-linear-to-br from-blue-600 to-blue-700 rounded-2xl p-5 text-white shadow-lg shadow-blue-200">
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

          {/* 4. Profile Mini-Card (Restored) */}
          <div className="mt-6 mx-2 flex items-center gap-3 p-3 hover:bg-white rounded-xl cursor-pointer transition border border-transparent hover:border-slate-200">
            <div className="w-10 h-10 bg-slate-300 rounded-full" />
            <div className="flex-1 min-w-0">
              <p className="text-sm font-bold text-slate-800 truncate">
                Alex Johnson
              </p>
              <p className="text-xs text-slate-500 truncate">@alex_civic</p>
            </div>
          </div>
        </aside>

        {/* --- CENTER COLUMN: THE FEED --- */}
        <main className="col-span-12 md:col-span-9 lg:col-span-6 min-h-screen border-x border-slate-200/60 bg-slate-50/50">
          {/* 1. Header & Sticky Tabs */}
          <div className="sticky top-0 z-20 bg-slate-100/95 backdrop-blur-md border-b border-slate-200">
            <div className="px-4 py-3">
              <h2 className="text-lg font-bold text-slate-800">Home Feed</h2>
            </div>

            {/* The Tabs You Liked */}
            <div className="flex w-full px-2">
              <TabButton text="Nearby" active />
              <TabButton text="Trending" />
              <TabButton text="Resolved" />
            </div>
          </div>

          {/* 2. Post Input (Restored Buttons) */}
          <div className="p-4 border-b border-slate-200 bg-white">
            <div className="flex gap-3">
              <div className="w-10 h-10 bg-slate-200 rounded-full shrink-0" />
              <div className="w-full">
                <textarea
                  placeholder="Spot an issue? Describe it here..."
                  className="w-full bg-transparent outline-none text-slate-700 text-lg resize-none h-20 placeholder:text-slate-400"
                />

                {/* Action Bar */}
                <div className="flex justify-between items-center mt-2 pt-2 border-t border-slate-100">
                  <div className="flex gap-2">
                    <IconButton
                      icon={<ImageIcon size={20} />}
                      tooltip="Add Photo"
                    />
                    <IconButton
                      icon={<MapPin size={20} />}
                      tooltip="Add Location"
                    />
                  </div>
                  <button className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-full font-bold text-sm transition flex items-center gap-2 shadow-sm">
                    Post <Send size={16} />
                  </button>
                </div>
              </div>
            </div>
          </div>

          {/* 3. The Feed Stream */}
          <FeedStream issues={issues} />
        </main>

        {/* --- RIGHT COLUMN: COMMUNITY CONTEXT --- */}
        <aside className="hidden lg:block col-span-3 sticky top-0 h-screen py-6 space-y-6 overflow-y-auto pr-2">
          {/* 1. Search (Simple) */}
          <div className="relative group">
            <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none text-slate-400 group-focus-within:text-blue-500">
              <svg
                className="w-4 h-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                ></path>
              </svg>
            </div>
            <input
              type="text"
              placeholder="Search issues..."
              className="w-full bg-white border border-slate-200 rounded-full py-2.5 pl-10 pr-4 text-sm focus:ring-2 focus:ring-blue-100 focus:border-blue-400 outline-none transition shadow-sm"
            />
          </div>

          {/* 2. Neighborhood Stats (Restored from Layout 2) */}
          <Widget title="Your Neighborhood">
            <div className="flex items-center justify-between py-2 border-b border-slate-50">
              <span className="text-slate-500 text-sm">Issues Resolved</span>
              <span className="text-green-600 font-bold bg-green-50 px-2 py-0.5 rounded text-sm">
                +12%
              </span>
            </div>
            <div className="flex items-center justify-between py-2">
              <span className="text-slate-500 text-sm">Avg Fix Time</span>
              <span className="text-slate-900 font-bold">3 Days</span>
            </div>
          </Widget>

          {/* 3. City Updates (Restored from Layout 2) */}
          <Widget title="City Updates">
            <div className="space-y-4">
              <div className="flex gap-3 items-start">
                <div className="w-8 h-8 rounded bg-orange-100 text-orange-600 flex items-center justify-center shrink-0 mt-0.5">
                  <AlertTriangle size={16} />
                </div>
                <div>
                  <p className="text-sm font-bold text-slate-800 leading-tight">
                    Heavy Rain Alert
                  </p>
                  <p className="text-xs text-slate-500 mt-1">
                    Check drains in Sector 4 to prevent logging.
                  </p>
                </div>
              </div>

              <div className="flex gap-3 items-start">
                <div className="w-8 h-8 rounded bg-blue-100 text-blue-600 flex items-center justify-center shrink-0 mt-0.5">
                  <TrendingUp size={16} />
                </div>
                <div>
                  <p className="text-sm font-bold text-slate-800 leading-tight">
                    New Road Project
                  </p>
                  <p className="text-xs text-slate-500 mt-1">
                    Mayor approved budget for Main St repairs.
                  </p>
                </div>
              </div>
            </div>
          </Widget>

          {/* 4. Top Citizens (Gamification) */}
          <Widget title="Top Citizens">
            <div className="space-y-3">
              <TopCitizen rank="1" name="Priya S." points="1,240" />
              <TopCitizen rank="2" name="David K." points="980" />
              <TopCitizen rank="3" name="You" points="850" highlight />
            </div>
          </Widget>
        </aside>
      </div>
    </div>
  );
}

// --- SUB-COMPONENTS FOR CLEANER CODE ---

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

function TabButton({ text, active }: any) {
  return (
    <div
      className={`flex-1 text-center py-3 cursor-pointer relative text-sm font-medium transition ${
        active
          ? "text-blue-600"
          : "text-slate-500 hover:text-slate-800 hover:bg-slate-200/50 rounded-lg"
      }`}
    >
      {text}
      {active && (
        <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-8 h-1 bg-blue-600 rounded-t-full" />
      )}
    </div>
  );
}

function IconButton({ icon, tooltip }: any) {
  return (
    <button
      className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-full transition"
      title={tooltip}
    >
      {icon}
    </button>
  );
}

function Widget({ title, children }: any) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-4">
      <h3 className="font-bold text-slate-800 mb-3 text-sm uppercase tracking-wide opacity-80">
        {title}
      </h3>
      {children}
    </div>
  );
}

function TopCitizen({ rank, name, points, highlight }: any) {
  return (
    <div
      className={`flex items-center justify-between p-2 rounded-lg ${highlight ? "bg-blue-50 border border-blue-100" : ""}`}
    >
      <div className="flex items-center gap-3">
        <span
          className={`w-6 h-6 flex items-center justify-center text-xs font-bold rounded-full ${
            rank === "1"
              ? "bg-yellow-100 text-yellow-700"
              : "bg-slate-100 text-slate-500"
          }`}
        >
          {rank}
        </span>
        <span
          className={`text-sm font-medium ${highlight ? "text-blue-700" : "text-slate-700"}`}
        >
          {name}
        </span>
      </div>
      <span className="text-xs font-bold text-slate-400">{points} pts</span>
    </div>
  );
}
