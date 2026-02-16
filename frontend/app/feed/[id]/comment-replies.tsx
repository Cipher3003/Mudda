import useSWR from "swr";
import { adaptReplyDTO } from "../adapter";
import { ReplyPageResponse } from "../type";
import CommentList from "./comment-list";
import { apiClient } from "@/app/lib/api-client";

interface CommentRepliesProps {
  indentation: string;
  parentId: number;
  onCollapse: () => void;
}

export default function CommentReplies(props: CommentRepliesProps) {
  const replyUrl = `api/v1/comments/${props.parentId}/replies`;

  const { data, isLoading, error } = useSWR(
    replyUrl,
    apiClient.get<ReplyPageResponse>,
    {
      revalidateIfStale: false,
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
    },
  );

  if (isLoading)
    return (
      <div className="py-2 text-xs text-slate-400 flex items-center gap-2">
        <div className="w-3 h-3 border-2 border-slate-300 border-t-blue-500 rounded-full animate-spin" />
        Loading replies...
      </div>
    );

  if (error)
    return (
      <div className="py-2 text-xs text-red-500">
        Failed to load replies.{" "}
        <button onClick={() => window.location.reload()} className="underline">
          Retry
        </button>
      </div>
    );

  const replies = data?.content.map(adaptReplyDTO);
  if (!replies || replies.length === 0) return null;

  return (
    <div
      className={`${props.indentation} relative animate-in fade-in slide-in-from-top-2`}
    >
      <div className="absolute -left-4 top-0 bottom-4 w-0.5 bg-slate-200 rounded-full" />
      {/* Reply List after data fetching */}
      {replies && (
        <CommentList
          comments={replies}
          isCompact={true}
          isNested={true}
          onCollapse={props.onCollapse}
        />
      )}
    </div>
  );
}
