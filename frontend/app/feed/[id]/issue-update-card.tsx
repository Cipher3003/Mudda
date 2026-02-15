"use client";

import { ChevronDown, ChevronUp, Reply, ThumbsUp } from "lucide-react";
import { useState } from "react";
import CommentInput from "./comment-input";
import CommentList from "./comment-list";

export default function IssueUpdateCard({ update }: any) {
  return (
    <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm hover:shadow-md transition-shadow relative">
      <div className="absolute top-3 -left-[6.5px] w-3 h-3 bg-white border-l border-b border-slate-200 transform rotate-45" />
      <CardHeader update={update} />

      {/* Content */}
      <p className="text-slate-700 text-sm leading-relaxed mb-3">
        {update.desc}
      </p>

      {update.images.length > 0 && (
        <img
          src={update.images[0]}
          className="mb-3 rounded-lg h-32 w-full object-cover border border-slate-100"
        />
      )}
      <CardFooter update={update} />
    </div>
  );
}

const CardHeader = ({ update }: any) => {
  return (
    <div className="flex justify-between items-start mb-2">
      <div>
        <div className="flex items-center gap-2">
          <h4 className="font-bold text-slate-900 text-sm">{update.title}</h4>
          {update.type === "official" && (
            <span className="bg-blue-50 text-blue-700 text-[10px] px-1.5 py-0.5 rounded font-bold uppercase">
              Official
            </span>
          )}
        </div>
        <p className="text-xs text-slate-500">{update.author}</p>
      </div>
      <div className="text-xs text-slate-400 whitespace-nowrap">
        {update.date}
      </div>
    </div>
  );
};

const CardFooter = ({ update }: any) => {
  const [showInput, setShowInput] = useState(false);
  const [showList, setShowList] = useState(false);

  const comments = update.comments || [];
  const commentCount = comments.length;

  return (
    <>
      <div className="flex items-center gap-4 pt-3 border-t border-slate-50">
        {/* Left Actions (likes & comment) */}
        <div className="flex items-center gap-4">
          <button className="flex items-center gap-1.5 text-xs font-bold text-slate-500 hover:text-blue-600">
            <ThumbsUp size={14} /> {update.likes} Helpful
          </button>
          <button
            onClick={() => setShowInput(!showInput)}
            className={`flex items-center gap-1.5 text-xs font-bold transition-colors 
            ${showInput ? "text-blue-600" : "text-slate-500 hover:text-blue-600"}`}
          >
            <Reply size={14} /> Comment
          </button>
        </div>

        {/* Toggle Comment List (Right Actions) */}
        {commentCount > 0 && (
          <button
            onClick={() => setShowList(!showList)}
            className="flex items-center gap-1 text-xs font-bold text-slate-400 hover:text-slate-600 transition-colors"
          >
            {commentCount} Comments
            {showList ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
          </button>
        )}
      </div>

      {/* Comment Input */}
      {showInput && (
        <div className="mt-3 pt-3 border-t border-slate-50 animate-in fade-in slide-in-from-top-1">
          <CommentInput isCompact={true} />
        </div>
      )}

      {/* 2. Comment List (Bottom of the expansion) */}
      {showList && (
        <div
          className={`
           ${showInput ? "" : "pt-3 border-t border-slate-50"} 
           mt-0 -mx-4 px-4 pb-4 rounded-b-xl animate-in fade-in
        `}
        >
          <div className="pt-4">
            <CommentList
              comments={comments}
              isCompact={true}
              isNested={false}
              onCollapse={() => setShowList(!showList)}
            />
          </div>
        </div>
      )}
    </>
  );
};
