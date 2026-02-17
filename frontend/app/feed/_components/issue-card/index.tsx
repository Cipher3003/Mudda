import IssueImageGrid from "../issue-image-grid";
import { IssueCardProps } from "../../type";
import CardHeader from "./header";
import CardFooter from "./footer";
import Link from "next/link";

// TODO: add share button in footer, and implement share functionality (copy link to clipboard)

export default function IssueCard(props: IssueCardProps) {
  const { isDetail = false } = props;

  return (
    <div
      className={`relative bg-white border border-slate-200 rounded-xl overflow-hidden mb-6 transition-all duration-200 ${
        !isDetail
          ? "hover:shadow-md cursor-pointer max-w-2xl mx-auto" // Feed Mode: Constrained width, hover effects
          : "w-full shadow-sm" // Detail Mode: Full width of parent, subtle shadow
      }`}
    >
      {!isDetail && (
        <Link
          href={`/feed/${props.id}`}
          className="absolute inset-0 z-0"
          aria-label="View issue details"
        />
      )}
      <CardHeader
        authorId={props.authorId}
        authorName={props.authorName}
        authorImageUrl={props.authorImageUrl}
        createdAt={props.createdAt}
        category={props.category}
        isResolved={props.isResolved}
        severity={props.severity}
        status={props.status}
      />
      <CardContent title={props.title} desc={props.desc} isDetail={isDetail} />
      <IssueImageGrid images={props.images} />
      <CardFooter
        votes={props.votes}
        comments={props.comments}
        isResolved={props.isResolved}
        address={props.address}
        hasVoted={props.hasVoted}
        canVote={props.canVote}
        onCommentClick={props.onCommentClick}
        issueId={props.id}
      />
    </div>
  );
}

const CardContent = ({ title, desc, isDetail }: any) => {
  return (
    <div className="px-4 pb-3">
      <h3 className="text-xl font-bold text-slate-900 mb-2 leading-tight">
        {title}
      </h3>
      <p
        className={`text-slate-600 text-sm leading-relaxed ${
          isDetail ? "" : "line-clamp-2"
        }`}
      >
        {desc}
      </p>
    </div>
  );
};
