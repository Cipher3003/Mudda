//#region Feed types

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
  has_user_voted: boolean;
  can_user_vote: boolean;
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
  onCommentClick?: () => void;
  isDetail?: boolean;
}

//#endregion

//#region Feed/[id] types

export interface IssueDetailDTO extends IssueFeedDTO {
  description: string;
  locationSummary: {
    city: string;
    state: string;
  };
  category: string;
  severity_score: number;
  updated_at: string;
  can_user_comment: boolean;
  can_user_edit: boolean;
  can_user_delete: boolean;
}

export interface IssueDetailData extends IssueCardProps {
  // TODO: Not used
  canComment: boolean;
  canEdit: boolean;
  canDelete: boolean;
  updatedAt: string;
}

export interface CommentPageResponse {
  content: CommentDTO[];
  page: {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

export interface CommentDTO {
  comment_id: number;
  text: string;
  author_id: number;
  issue_id: number; // TODO: not utilized
  like_count: number;
  reply_count: number;
  created_at: string;
  has_user_liked: boolean;
  can_user_like: boolean;
  can_user_update: boolean;
  can_user_delete: boolean;
}

export interface CommentData {
  id: number;
  authorName: string;
  createdAt: string;
  content: string;
  likeCount: number;
  replyCount: number;
  replies: CommentData[];
  hasLiked: boolean;
  canLike: boolean;
  canUpdate: boolean;
  canDelete: boolean;
}

export interface ReplyDTO {
  reply_id: number;
  text: string;
  author_id: number;
  comment_id: number;
  like_count: number;
  created_at: string;
  has_user_liked: boolean;
  can_user_like: boolean;
  can_user_update: boolean;
  can_user_delete: boolean;
}

export interface ReplyPageResponse {
  content: ReplyDTO[];
  page: {
    number: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

//#endregion
