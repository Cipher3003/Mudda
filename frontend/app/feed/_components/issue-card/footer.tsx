"use client";

import { apiClient } from "@/app/lib/api-client";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import {
  MapPin,
  MessageSquare,
  MoreHorizontal,
  TrendingUp,
} from "lucide-react";
import Link from "next/link";
import React from "react";

const CardFooter = ({
  votes,
  comments,
  isResolved,
  address,
  hasVoted,
  canVote, // TODO: should i get auth flag from server or decide on frontend middleware
  onCommentClick,
  issueId,
}: any) => {
  const handleLike = (event: React.MouseEvent<HTMLButtonElement>) => {
    // TODO: Optimistic UI update the states to reflect update
    event.preventDefault();
    event.stopPropagation();

    if (hasVoted) apiClient.delete(`api/v1/issues/${issueId}/votes`);
    else apiClient.post(`api/v1/issues/${issueId}/votes`);
  };

  return (
    <div className="px-4 py-3 flex items-center justify-between relative z-10">
      {/* Left Side: Actions */}
      <div className="flex items-center gap-4 sm:gap-6">
        {/* Vote / Support Button */}
        <Popover>
          <PopoverTrigger asChild>
            <button
              onClick={canVote ? handleLike : (e) => e.stopPropagation()}
              className={`flex items-center gap-2 transition-colors group 
                ${hasVoted ? "text-blue-600" : "text-slate-600 hover:text-blue-600"}`}
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
          </PopoverTrigger>

          {!canVote && (
            <PopoverContent
              onClick={(e) => e.stopPropagation()}
              align="start"
              className="w-64 p-4 shadow-xl border-slate-200"
            >
              <div className="space-y-3">
                <p className="text-sm font-medium text-slate-800">
                  Join the community to support this issue
                </p>
                <Link
                  href="/login"
                  onClick={(e) => e.stopPropagation()}
                  className="block w-full text-center bg-blue-600 text-white text-sm py-2 rounded-md hover:bg-blue-700 transition-colors"
                >
                  Sign in
                </Link>
              </div>
            </PopoverContent>
          )}
        </Popover>

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
