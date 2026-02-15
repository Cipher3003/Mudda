"use client";

import {
  User,
  Heart,
  MessageSquare,
  MoreHorizontal,
  ChevronUp,
  ChevronDown,
} from "lucide-react";
import { useState } from "react";
import { CommentData } from "../type";
import CommentInput from "./comment-input";
import CommentReplies from "./comment-replies";
import TimeAgo from "../_components/time-ago";

export interface CommentItemProps {
  comment: CommentData;
  isCompact: boolean;
}

export default function CommentItem(props: CommentItemProps) {
  const [isReplying, setIsReplying] = useState(false);
  const [showReplies, setShowReplies] = useState(false);

  const avatarSize = props.isCompact ? "w-8 h-8" : "w-10 h-10";
  const iconSize = props.isCompact ? 16 : 20;
  const indentation = props.isCompact ? "ml-11" : "ml-14";

  const hasReplies = props.comment.replyCount > 0;

  return (
    <div className="group animate-in fade-in duration-300">
      <div className="flex gap-3 mb-3">
        {/* Avatar */}
        <div
          className={`rounded-full bg-slate-100 flex items-center justify-center text-slate-400 shrink-0 border border-slate-100
             ${avatarSize}`}
        >
          <User size={iconSize} />
        </div>

        {/* Content */}
        <div className="flex-1">
          <div className="flex items-baseline gap-2 mb-1">
            <span
              className={`font-bold text-slate-900 
                ${props.isCompact ? "text-sm" : "text-sm sm:text-base"}`}
            >
              {props.comment.authorName}
            </span>
            <span className="text-slate-400 text-xs">
              <TimeAgo timestamp={props.comment.createdAt} />
            </span>
          </div>

          <p
            className={`text-slate-800 text-sm leading-relaxed mb-2 
                ${props.isCompact ? "text-sm" : "text-sm sm:text-base"}`}
          >
            {props.comment.content}
          </p>

          {/* Actions */}
          <div className="flex items-center gap-5">
            <button className="flex items-center gap-1.5 text-slate-500 hover:text-pink-600 transition-colors">
              <Heart size={props.isCompact ? 14 : 16} />
              <span className="text-xs font-bold">
                {props.comment.likeCount}
              </span>
            </button>
            <button
              className={`flex items-center gap-1.5 text-xs font-bold transition-colors 
                ${isReplying ? "text-blue-600" : "text-slate-500 hover:text-blue-600"}`}
              onClick={() => setIsReplying(!isReplying)}
            >
              <MessageSquare size={props.isCompact ? 14 : 16} />
              <span className="text-xs font-bold">Reply</span>
            </button>
            {!props.isCompact && (
              <button className="hover:text-slate-800">
                <MoreHorizontal size={16} />
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Reply Input Section */}
      {isReplying && (
        <div
          className={`mb-4 ${indentation} animate-in fade-in slide-in-from-top-1`}
        >
          <CommentInput
            isCompact={true}
            autoFocus={true}
            placeholder={`Reply to ${props.comment.authorName}`}
            onSubmit={() => setIsReplying(false)}
            // TODO: revalidate the SWR cache after submit
          />
        </div>
      )}

      {/* Show Replies Toggle */}
      {hasReplies && (
        <div className={`${indentation} mb-2`}>
          <button
            onClick={() => setShowReplies(!showReplies)}
            className="flex items-center gap-2 text-xs font-bold text-blue-600 hover:bg-blue-50 px-2 py-1 rounded-md transition-colors w-fit"
          >
            {/* Dynamic Icon */}
            {showReplies ? (
              <ChevronUp size={14} />
            ) : (
              <div className="rotate-90">
                <div className="-rotate-90">
                  <ChevronDown size={14} />
                </div>
              </div>
            )}
            {showReplies
              ? "Hide replies"
              : `${props.comment.replyCount} replies`}
          </button>
        </div>
      )}

      {/* --- Replies (Nested Comments) Section --- */}
      {hasReplies && showReplies && (
        <CommentReplies
          indentation={indentation}
          parentId={props.comment.id}
          onCollapse={() => setShowReplies(false)}
        />
      )}
    </div>
  );
}
