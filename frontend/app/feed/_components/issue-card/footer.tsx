import {
  MapPin,
  MessageSquare,
  MoreHorizontal,
  TrendingUp,
} from "lucide-react";

const CardFooter = ({
  votes,
  comments,
  isResolved,
  address,
  hasVoted,
  canVote,
  onCommentClick,
}: any) => {
  return (
    <div className="px-4 py-3 flex items-center justify-between">
      {/* Left Side: Actions */}
      <div className="flex items-center gap-4 sm:gap-6">
        {/* Vote / Support Button */}
        <button
          className={`flex items-center gap-2 transition-colors group ${
            hasVoted ? "text-blue-600" : "text-slate-600 hover:text-blue-600"
          }`}
        >
          <div className="p-2 rounded-full group-hover:bg-blue-50 transition-colors">
            <TrendingUp
              size={20}
              className={
                isResolved
                  ? "text-green-600"
                  : hasVoted
                    ? "text-blue-600"
                    : "group-hover:-translate-y-0.5 transition-transform"
              }
            />
          </div>
          <span className="font-semibold text-sm">
            {votes} <span className="hidden sm:inline">Support</span>
          </span>
        </button>

        {/* Comment Button */}
        <button
          className="flex items-center gap-2 text-slate-600 hover:text-blue-600 transition-colors group"
          onClick={onCommentClick}
        >
          <div className="p-2 rounded-full group-hover:bg-blue-50 transition-colors">
            <MessageSquare size={20} />
          </div>
          <span className="font-semibold text-sm">
            {comments} <span className="hidden sm:inline">Comments</span>
          </span>
        </button>
      </div>

      {/* Right Side: Location & More */}
      <div className="flex items-center gap-2 sm:gap-4">
        {/* Location Display */}
        <div
          className="flex items-center gap-1 text-slate-400 hover:text-slate-700 transition-colors cursor-pointer"
          title="View on map"
        >
          <MapPin size={16} />
          <span className="text-xs font-medium max-w-20 sm:max-w-30 truncate">
            {address}
          </span>
        </div>

        {/* More Options */}
        <button className="text-slate-400 hover:text-slate-600 p-2">
          <MoreHorizontal size={20} />
        </button>
      </div>
    </div>
  );
};

export default CardFooter;
