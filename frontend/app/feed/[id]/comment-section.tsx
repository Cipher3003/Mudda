"use client";

import { CommentData } from "../type";
import CommentInput from "./comment-input";
import CommentList from "./comment-list";

export interface CommentSectionProps {
  comments: CommentData[];
  isCompact: boolean;
  onCollapse?: () => void;
}

export default function CommentSection(props: CommentSectionProps) {
  return (
    <div className={props.isCompact ? "mt-3" : "mt-6"}>
      <CommentInput isCompact={props.isCompact} />
      <CommentList
        comments={props.comments}
        isCompact={props.isCompact}
        onCollapse={props.onCollapse}
        isNested={false}
      />
    </div>
  );
}
