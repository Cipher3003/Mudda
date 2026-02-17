import getSeverityBadge from "@/app/feed/severity-badge";
import TimeAgo from "../time-ago";
import { AlertTriangle } from "lucide-react";
import Avatar from "../avatar";

const CardHeader = ({
  authorId,
  authorName,
  authorImageUrl,
  createdAt,
  category,
  isResolved,
  severity,
  status,
}: any) => {
  const sevStyle = getSeverityBadge(severity);
  return (
    <div className="p-4 flex justify-between items-start">
      <div className="flex items-center gap-3" data-author-id={authorId}>
        <Avatar name={authorName} src={authorImageUrl} />
        <div>
          <h4 className="font-bold text-slate-900 text-sm">{authorName}</h4>
          <div className="flex items-center gap-2 text-xs text-slate-500">
            <TimeAgo timestamp={createdAt} />
            <span>•</span>
            {/* Category */}
            <span className="font-medium text-slate-700 bg-slate-100 px-1.5 py-0.5 rounded">
              {category}
            </span>
          </div>
        </div>
      </div>

      {/* Status Badge & Severity */}
      <div className="flex items-center gap-2">
        <div
          className={`inline-flex items-center gap-1.5 px-2 py-1 rounded text-xs font-bold ${sevStyle.bg} ${sevStyle.text}`}
        >
          <AlertTriangle size={14} className={sevStyle.icon} />
          Severity: {severity}
        </div>

        <span
          className={`px-2.5 py-1 rounded text-xs font-bold uppercase ${
            isResolved
              ? "bg-green-100 text-green-700"
              : "bg-amber-100 text-amber-700"
          }`}
        >
          {status}
        </span>
      </div>
    </div>
  );
};

export default CardHeader;
