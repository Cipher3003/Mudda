import { ChevronDown, ChevronUp } from "lucide-react";
import { useState } from "react";
import { CommentData } from "../type";
import CommentItem from "./comment-item";

export interface CommentListProps {
  comments: CommentData[];
  isCompact: boolean;
  onCollapse?: () => void;
  isNested: boolean;
}

// TODO: add pagination fetch for comments and replies
export default function CommentList(props: CommentListProps) {
  if (props.comments.length === 0) return null;

  const INITIAL_COUNT = props.isNested || props.isCompact ? 2 : 4;
  const INCREMENT = 2;

  const [visibleCount, setVisibleCount] = useState(INITIAL_COUNT);

  const visibleComments = props.comments.slice(0, visibleCount);
  const remainingComments = props.comments.length - visibleCount;

  const handleCollapse = () => {
    setVisibleCount(INITIAL_COUNT);
    if (props.onCollapse) props.onCollapse();
    // Optional: Scroll back to top of list if it's very long
    // e.g. listRef.current?.scrollIntoView({ behavior: 'smooth' })
  };

  return (
    <div className={props.isCompact ? "space-y-4" : "space-y-6"}>
      {visibleComments.map((comment: CommentData) => (
        <CommentItem
          key={comment.id}
          comment={comment}
          isCompact={props.isCompact}
        />
      ))}

      {remainingComments > 0 && (
        <button
          onClick={() => setVisibleCount((prev) => prev + INCREMENT)}
          className="flex items-center gap-1 text-blue-600 font-bold text-xs hover:underline mt-2 pl-1"
        >
          View {remainingComments} more comments <ChevronDown size={14} />
        </button>
      )}

      {remainingComments <= 0 && props.comments.length > INITIAL_COUNT && (
        <button
          onClick={handleCollapse}
          className="flex items-center gap-1 text-slate-400 font-bold text-xs hover:text-slate-600 hover:underline mt-2 pl-1 group"
        >
          <span>Hide comments</span>
          <ChevronUp
            size={14}
            className="group-hover:-translate-y-0.5 transition-transform"
          />
        </button>
      )}
    </div>
  );
}
