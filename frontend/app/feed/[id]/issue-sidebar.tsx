"use client";

import {
  CheckCircle2,
  MapPin,
  Share2,
  Bell,
  Building2,
  Flag,
  FileText,
  User,
  ArrowUpCircle,
  Clock,
  AlertCircle,
} from "lucide-react";

export default function IssueSidebar({ updates }: any) {
  const fullTimeline = [
    {
      id: "log-1",
      type: "log", // Minor event
      title: "Complaint Registered",
      date: "2 days ago",
      icon: FileText,
      color: "text-slate-400",
      bg: "bg-slate-100",
    },
    {
      id: "log-2",
      type: "log",
      title: "Assigned to Ward 12 Officer",
      date: "2 days ago",
      icon: User,
      color: "text-blue-500",
      bg: "bg-blue-50",
    },
    {
      id: "log-3",
      type: "log",
      title: "Priority changed to High",
      date: "1 day ago",
      icon: ArrowUpCircle,
      color: "text-orange-500",
      bg: "bg-orange-50",
    },
    // ... Spread the real updates here, mapped to a common structure
    ...updates.map((u: any) => ({
      id: u.id,
      type: "update", // Major event (Clickable)
      title: u.title,
      date: u.date,
      isMajor: u.isMajor, // e.g., resolved vs just update
      icon: u.isMajor ? CheckCircle2 : AlertCircle, // Official vs User Update
      color: u.isMajor ? "text-green-600" : "text-slate-600",
      bg: u.isMajor ? "bg-green-50" : "bg-white border border-slate-200",
    })),
    {
      id: "log-4",
      type: "log",
      title: "Official inspection scheduled",
      date: "5 hours ago",
      icon: Clock,
      color: "text-purple-500",
      bg: "bg-purple-50",
    },
  ].sort((a, b) => {
    // Mock sort - in real app use timestamps
    return 0; // Keeping the manual order for demo
  });

  return (
    <div className="space-y-6">
      {/* WIDGET 1: ACTION CENTER */}
      <div className="bg-white border border-slate-200 rounded-xl p-5 shadow-sm">
        <h3 className="font-bold text-slate-900 mb-1 text-sm">Get Notified</h3>
        <p className="text-xs text-slate-500 mb-4">
          Track this issue to get alerts on status changes.
        </p>
        <div className="grid grid-cols-2 gap-3">
          <button className="flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded-lg text-xs font-bold transition-colors">
            <Bell size={14} /> Follow
          </button>
          <button className="flex items-center justify-center gap-2 bg-slate-100 hover:bg-slate-200 text-slate-700 py-2 px-4 rounded-lg text-xs font-bold transition-colors">
            <Share2 size={14} /> Share
          </button>
        </div>
      </div>

      {/* WIDGET 2: ACTIVITY TIMELINE (The "Log") */}
      <div className="relative">
        <h3 className="font-bold text-slate-900 uppercase text-xs tracking-wider mb-4 px-1">
          Activity Log
        </h3>

        {/* Vertical Connector Line */}
        <div className="absolute left-3.75 top-8 bottom-4 w-0.5 bg-slate-200 -z-10" />

        <div className="space-y-5">
          {fullTimeline.map((event: any, index) => {
            // Is this a clickable update or a static log?
            const isClickable = event.type === "update";

            // Render content wrapper
            const Content = (
              <div className="flex gap-3 items-start relative w-full">
                {/* ICON BOX */}
                <div
                  className={`mt-0.5 relative z-10 w-8 h-8 rounded-full flex items-center justify-center shrink-0 ring-4 ring-slate-50 ${event.bg}`}
                >
                  <event.icon size={14} className={event.color} />
                </div>

                {/* TEXT CONTENT */}
                <div className="pt-1">
                  <p
                    className={`text-sm leading-tight ${isClickable ? "font-bold text-slate-800 group-hover:text-blue-600" : "font-medium text-slate-600"}`}
                  >
                    {event.title}
                  </p>
                  <p className="text-xs text-slate-400 mt-1">{event.date}</p>
                </div>
              </div>
            );

            // If clickable, wrap in button. If log, use div.
            return isClickable ? (
              <button
                key={event.id}
                onClick={() =>
                  document
                    .getElementById(`update-${event.id}`)
                    ?.scrollIntoView({ behavior: "smooth", block: "center" })
                }
                className="block w-full text-left group hover:bg-slate-50 -ml-2 p-2 rounded-lg transition-colors"
              >
                {Content}
              </button>
            ) : (
              <div key={event.id} className="pl-0 py-1">
                {Content}
              </div>
            );
          })}
        </div>
      </div>

      {/* WIDGET 3: AUTHORITY DETAILS */}
      <div className="bg-slate-50 border border-slate-200 rounded-xl p-5">
        <h3 className="font-bold text-slate-900 mb-4 uppercase text-xs tracking-wider">
          Details
        </h3>

        <div className="space-y-4">
          <div className="flex gap-3">
            <div className="w-8 h-8 rounded-full bg-white border border-slate-200 flex items-center justify-center text-slate-400 shrink-0">
              <MapPin size={14} />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-500 uppercase">
                Location
              </p>
              <p className="text-sm font-semibold text-slate-800">
                Sector 45, Main Market
              </p>
            </div>
          </div>

          <div className="flex gap-3">
            <div className="w-8 h-8 rounded-full bg-white border border-slate-200 flex items-center justify-center text-slate-400 shrink-0">
              <Building2 size={14} />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-500 uppercase">
                Department
              </p>
              <p className="text-sm font-semibold text-slate-800">
                Public Works Dept
              </p>
            </div>
          </div>

          <div className="pt-3 mt-3 border-t border-slate-200">
            <button className="flex items-center gap-2 text-xs font-bold text-slate-400 hover:text-red-600 transition-colors w-full">
              <Flag size={12} /> Report Issue
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
