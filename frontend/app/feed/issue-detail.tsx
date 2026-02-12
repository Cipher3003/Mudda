import {
  ArrowLeft,
  MapPin,
  Calendar,
  CheckCircle2,
  Circle,
  MessageSquare,
  Send,
  MoreHorizontal,
} from "lucide-react";

export default function IssueDetailView({ issue, onBack }: any) {
  return (
    <div className="bg-white min-h-screen pb-20">
      {/* 1. Sticky Header */}
      <div className="sticky top-0 z-10 bg-white/80 backdrop-blur-md border-b border-slate-200 px-4 py-3 flex items-center gap-4">
        <button
          onClick={onBack}
          className="p-2 -ml-2 hover:bg-slate-100 rounded-full transition-colors"
        >
          <ArrowLeft size={20} className="text-slate-700" />
        </button>
        <div>
          <h2 className="text-lg font-bold text-slate-900 leading-none">
            Issue #{issue.id}
          </h2>
          <span className="text-xs text-slate-500">
            Posted in {issue.category}
          </span>
        </div>
      </div>

      <div className="p-4 sm:p-6">
        {/* 2. Meta Data (User & Severity) */}
        <div className="flex justify-between items-start mb-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 bg-slate-200 rounded-full flex items-center justify-center text-lg font-bold text-slate-500">
              {issue.user.charAt(0)}
            </div>
            <div>
              <h1 className="font-bold text-slate-900 text-lg">{issue.user}</h1>
              <p className="text-sm text-slate-500">
                {issue.userRole || "Resident"}
              </p>
            </div>
          </div>

          <div className="flex flex-col items-end gap-2">
            {/* Reusing your Severity Logic here */}
            <span className="px-3 py-1 rounded-full bg-red-100 text-red-700 text-xs font-bold border border-red-200">
              High Severity
            </span>
            <span className="text-xs text-slate-400">{issue.time}</span>
          </div>
        </div>

        {/* 3. Main Content */}
        <h1 className="text-2xl sm:text-3xl font-bold text-slate-900 mb-4">
          {issue.title}
        </h1>

        <p className="text-slate-700 text-base leading-7 whitespace-pre-wrap mb-6">
          {issue.desc}
          {/* This is where the FULL description goes. No truncation. */}
        </p>

        {/* 4. Large Evidence Image */}
        {issue.imageUrl && (
          <div className="rounded-xl overflow-hidden border border-slate-200 mb-6 bg-slate-50">
            <img
              src={issue.imageUrl}
              alt="Evidence"
              className="w-full h-auto max-h-125 object-contain mx-auto"
            />
          </div>
        )}

        {/* 5. Location Context */}
        {issue.address && (
          <div className="flex items-center gap-2 text-slate-600 mb-8 p-3 bg-slate-50 rounded-lg border border-slate-100">
            <MapPin size={18} className="shrink-0 text-blue-500" />
            <span className="text-sm font-medium">{issue.address}</span>
            <button className="ml-auto text-xs text-blue-600 font-bold hover:underline">
              View on Map
            </button>
          </div>
        )}

        <hr className="border-slate-100 my-6" />

        {/* 6. Complaint Timeline (Crucial for awareness apps) */}
        <div className="mb-8">
          <h3 className="font-bold text-slate-900 mb-4">Status History</h3>
          <div className="space-y-4 pl-2">
            {/* Step 1: Current Status */}
            <div className="flex gap-4">
              <div className="flex flex-col items-center">
                <div className="w-3 h-3 bg-blue-500 rounded-full ring-4 ring-blue-100" />
                <div className="w-0.5 h-full bg-slate-200 my-1" />
              </div>
              <div className="pb-4">
                <p className="text-sm font-bold text-slate-900">
                  Investigation Started
                </p>
                <p className="text-xs text-slate-500">
                  Today, 10:00 AM • By Admin
                </p>
              </div>
            </div>

            {/* Step 2: Past Status */}
            <div className="flex gap-4">
              <div className="flex flex-col items-center">
                <div className="w-3 h-3 bg-slate-300 rounded-full" />
              </div>
              <div>
                <p className="text-sm font-medium text-slate-500">
                  Complaint Received
                </p>
                <p className="text-xs text-slate-400">Yesterday, 4:30 PM</p>
              </div>
            </div>
          </div>
        </div>

        <hr className="border-slate-100 my-6" />

        {/* 7. Comments Section */}
        <div>
          <h3 className="font-bold text-slate-900 mb-4 flex items-center gap-2">
            <MessageSquare size={18} />
            Discussion <span className="text-slate-400 font-normal">(12)</span>
          </h3>

          {/* Comment Input */}
          <div className="flex gap-3 mb-6">
            <div className="w-8 h-8 bg-slate-200 rounded-full shrink-0" />
            <div className="flex-1 relative">
              <input
                type="text"
                placeholder="Add a comment or update..."
                className="w-full bg-slate-50 border border-slate-200 rounded-full py-2 px-4 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all"
              />
              <button className="absolute right-2 top-1.5 p-1 text-blue-600 hover:bg-blue-50 rounded-full">
                <Send size={16} />
              </button>
            </div>
          </div>

          {/* Comment List (Placeholder) */}
          <div className="space-y-6">
            <div className="flex gap-3">
              <div className="w-8 h-8 bg-purple-100 text-purple-600 rounded-full flex items-center justify-center text-xs font-bold shrink-0">
                M
              </div>
              <div>
                <div className="flex items-center gap-2 mb-0.5">
                  <span className="text-sm font-bold text-slate-800">
                    Mayor's Office
                  </span>
                  <span className="text-[10px] bg-blue-100 text-blue-700 px-1 rounded font-bold">
                    OFFICIAL
                  </span>
                  <span className="text-xs text-slate-400">2h ago</span>
                </div>
                <p className="text-sm text-slate-600">
                  We have received this report and dispatched a team to inspect
                  the pipe.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
