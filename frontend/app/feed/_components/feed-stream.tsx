// "use client";

import Link from "next/link";
import { IssueCardProps } from "../type";
import IssueCard from "./issue-card";

export default function FeedStream({ issues }: { issues: IssueCardProps[] }) {
  // TODO: stop event propagation on card buttons (upvote, comment) so that it doesn't trigger the onClick for the whole card

  return (
    <div className="py-2 space-y-2">
      {issues.map((issue) => (
        <Link key={issue.id} href={`/feed/${issue.id}`}>
          <IssueCard key={issue.id} {...issue} />
        </Link>
      ))}
    </div>
  );
}
