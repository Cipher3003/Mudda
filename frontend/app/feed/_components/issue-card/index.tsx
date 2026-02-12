import IssueImageGrid from "../issue-image-grid";
import { IssueCardProps } from "../../type";
import CardHeader from "./header";
import CardFooter from "./footer";

export default function IssueCard(props: IssueCardProps) {
  return (
    <div
      onClick={props.onClick}
      className="bg-white border border-slate-200 rounded-xl overflow-hidden hover:shadow-md transition-shadow duration-200 max-w-2xl mx-auto mb-6"
    >
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
      <CardContent title={props.title} desc={props.desc} />
      <IssueImageGrid images={props.images} />
      <CardFooter
        votes={props.votes}
        comments={props.comments}
        isResolved={props.isResolved}
        address={props.address}
        hasVoted={props.hasVoted}
        canVote={props.canVote}
      />
    </div>
  );
}

const CardContent = ({ title, desc }: any) => {
  return (
    <div className="px-4 pb-3">
      <h3 className="text-xl font-bold text-slate-900 mb-2 leading-tight">
        {title}
      </h3>
      <p className="text-slate-600 text-sm leading-relaxed line-clamp-2">
        {desc}
      </p>
    </div>
  );
};
