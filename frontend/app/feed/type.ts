export interface IssueFeedDTO {
  id: number;
  title: string;
  status: "OPEN" | "PENDING" | "RESOLVED" | "CLOSED";
  vote_count: number;
  media_urls: string[];
  created_at: string;
  author_id: number;
  author_name: string;
  author_image_url: string | null;
  has_user_voted: boolean; //missing
  can_user_vote: boolean; //missing
}

export interface IssuePageResponse {
  content: IssueFeedDTO[];
  page: {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface IssueCardProps {
  // Core Content
  title: string;
  desc: string;
  category: string;

  // Metadata
  id: number;
  authorId: number;
  authorName: string;
  authorImageUrl: string | null;
  createdAt: string;
  status: "OPEN" | "PENDING" | "RESOLVED" | "CLOSED";
  isResolved: boolean;
  severity: string | number;

  // Metrics
  votes: number;
  comments: number;

  // Context
  images: string[];
  address: string;
  hasVoted: boolean;
  canVote: boolean;

  // Actions
  onClick: () => void;
}

export type IssueCardData = Omit<IssueCardProps, "onClick">;
