"use client";

import { IssueCardData } from "../type";
import IssueCard from "./issue-card";
import { useRouter } from "next/navigation";

export default function FeedStream({ issues }: { issues: IssueCardData[] }) {
  const router = useRouter();
  // TODO: stop event propagation on card buttons (upvote, comment) so that it doesn't trigger the onClick for the whole card

  return (
    <div className="py-2 space-y-2">
      {issues.map((issue) => (
        <IssueCard
          key={issue.id}
          {...issue}
          onClick={() => router.push(`/feed/${issue.id}`)}
        />
      ))}
    </div>
  );
}
