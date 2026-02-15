import { Camera, CheckCircle2 } from "lucide-react";
import IssueUpdateCard from "./issue-update-card";

export default function IssueTimeline({ updates }: any) {
  return (
    <div className="mt-8">
      <h3 className="text-xl font-bold text-slate-900 mb-6 px-2">
        Progress Timeline
      </h3>

      <div className="space-y-0">
        {updates.map((update: any) => (
          <div
            key={update.id}
            id={`update-${update.id}`}
            className="relative pl-16 sm:pl-20 pb-10 group"
          >
            {/* Timeline Line */}
            <div className="absolute left-8 top-0 bottom-0 w-0.5 bg-slate-200 group-last:bottom-auto group-last:h-full" />

            {/* Status Icon */}
            <div
              className={`absolute left-4 top-0 w-8 h-8 rounded-full border-4 border-white ring-1 ring-slate-100 flex items-center justify-center z-10 
                ${update.type === "official" ? "bg-blue-600 text-white" : "bg-slate-900 text-white"}`}
            >
              {update.type === "official" ? (
                <CheckCircle2 size={14} />
              ) : (
                <Camera size={14} />
              )}
            </div>
            <IssueUpdateCard update={update} />
          </div>
        ))}
      </div>
      <div className="pl-16 sm:pl-20">
        <div className="text-xs text-slate-400 font-medium italic">
          End of updates
        </div>
      </div>
    </div>
  );
}
