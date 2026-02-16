"use client";

import { useState } from "react";
import IssueCard from "../_components/issue-card";
import CommentSection from "./comment-section";
import { CommentData, IssueDetailData } from "../type";

export interface IssueDetailViewProps {
  issue: IssueDetailData;
  comments: CommentData[];
}

export default function IssueDetailView(props: IssueDetailViewProps) {
  const [showComments, setShowComments] = useState(false);

  const handleCommentClick = () => {
    setShowComments(!showComments);
  };

  return (
    <>
      {/* ISSUE CARD WRAPPER */}
      <div className="relative z-10">
        <IssueCard
          {...props.issue}
          isDetail={true}
          onCommentClick={handleCommentClick}
        />
        {/* Timeline Connector Line */}
        <div className="absolute left-8 -bottom-8 w-0.5 h-8 bg-slate-200 z-0" />
      </div>

      {/* Comment Section */}
      <div
        className={`grid transition-[grid-template-rows] duration-500 ease-out relative z-20
            ${showComments ? "grid-rows-[1fr] opacity-100 mb-8 mt-4" : "grid-rows-[0fr] opacity-0"}`}
      >
        {/* The inner div MUST have overflow-hidden for the animation to work */}
        <div className="overflow-hidden">
          <CommentSection
            isCompact={false}
            comments={props.comments}
            onCollapse={handleCommentClick}
          />
        </div>
      </div>
    </>
  );
}
