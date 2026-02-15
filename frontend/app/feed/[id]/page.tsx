import IssueTimeline from "./issue-timeline";
import {
  getCommentsByIssueId,
  getIssueById,
  getIssueUpdatesByIdMock,
} from "../service";
import IssueDetailView from "./issue-detail-view";
import BackButton from "./back-button";
import IssueSidebar from "./issue-sidebar";

interface IssueDetailProps {
  params: Promise<{ id: string }>;
}

export default async function IssueDetailPage(props: IssueDetailProps) {
  const params = await props.params;
  const id = Number(params.id);

  const [issue, updates, comments] = await Promise.all([
    getIssueById(id),
    getIssueUpdatesByIdMock(id),
    getCommentsByIssueId(id),
    // TODO: only shows page 1 comments needs to fetch more on client side
  ]);

  return (
    <div className="flex items-start h-full relative font-sans">
      <main className="flex-1 bg-white min-h-screen border-x border-slate-200 min-w-0">
        <div className="sticky top-0 z-20 bg-slate-100/95 backdrop-blur-md border-b border-slate-100 p-4 flex items-center gap-4">
          <BackButton />
          <h2 className="font-bold text-lg text-slate-800">Issue Details</h2>
        </div>

        <div className="p-4 sm:p-6">
          <IssueDetailView issue={issue} comments={comments} />
          <IssueTimeline updates={updates} />
        </div>
      </main>

      <aside className="hidden xl:block w-80 sticky top-0 h-screen p-6 overflow-y-auto">
        <IssueSidebar updates={updates} />
      </aside>
    </div>
  );
}
