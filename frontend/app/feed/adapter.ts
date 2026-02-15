import {
  CommentData,
  CommentDTO,
  IssueCardData,
  IssueDetailData,
  IssueDetailDTO,
  IssueFeedDTO,
  ReplyDTO,
} from "./type";

const getStableRandom = (seed: number) => {
  const x = Math.sin(seed) * 10000;
  return x - Math.floor(x);
};

export function adaptIssueFeedDTO(dto: IssueFeedDTO): IssueCardData {
  const seed = getStableRandom(dto.id);
  // Randomly pick a severity based on ID
  const severities = ["LOW", "MEDIUM", "HIGH", "CRITICAL"];
  const severity = severities[Math.floor(seed * severities.length)];

  // Randomly pick a category
  const categories = [
    "INFRASTRUCTURE",
    "SANITATION",
    "ELECTRICITY",
    "PUBLIC SAFETY",
  ];
  const category = categories[Math.floor(seed * categories.length)];

  const mockAddress =
    seed > 0.5 ? "Sector 20, Gurugram" : "Cyber City, DLF Phase 2";
  const mockDesc = `This is a reported issue regarding ${dto.title.toLowerCase()}... (ID #${dto.id})`;
  const mockComments = Math.floor(seed * 20);

  return {
    id: dto.id,
    authorId: dto.author_id,
    title: dto.title,
    desc: mockDesc,
    category: category,
    authorName: dto.author_name,
    authorImageUrl: dto.author_image_url,
    createdAt: dto.created_at,
    status: dto.status,
    isResolved: dto.status === "RESOLVED",
    severity: severity,
    votes: dto.vote_count,
    comments: mockComments,
    images: dto.media_urls,
    address: mockAddress,
    hasVoted: dto.has_user_voted,
    canVote: dto.can_user_vote,
  };
}

export function adaptIssueDetailDTO(dto: IssueDetailDTO): IssueDetailData {
  const seed = getStableRandom(dto.id);
  const mockComments = Math.floor(seed * 20);
  return {
    id: dto.id,
    authorId: dto.author_id,
    title: dto.title,
    authorName: dto.author_name,
    authorImageUrl: dto.author_image_url,
    createdAt: dto.created_at,
    status: dto.status,
    isResolved: dto.status === "RESOLVED",
    votes: dto.vote_count,
    images: dto.media_urls,
    hasVoted: dto.has_user_voted,
    canVote: dto.can_user_vote,

    desc: dto.description,
    category: dto.category,
    severity: dto.severity_score,
    address: `${dto.locationSummary.city}, ${dto.locationSummary.state}`,
    comments: mockComments,

    updatedAt: dto.updated_at,
    canComment: dto.can_user_comment,
    canEdit: dto.can_user_edit,
    canDelete: dto.can_user_delete,
  };
}

export function adaptCommentDTO(dto: CommentDTO): CommentData {
  const mockAuthorNames = [
    "Ravi Kumar",
    "Anita Desai",
    "System",
    "Arjun Singh",
  ];
  const authorId = Math.floor(
    getStableRandom(dto.author_id) / mockAuthorNames.length,
  );
  const mockAuthorName = mockAuthorNames[authorId];
  const mockReplies: CommentData[] =
    dto.reply_count === 0
      ? []
      : Array.from({ length: dto.reply_count }, () => ({
          id: 8,
          authorName: "Arjun Singh",
          content: "Thanks! Please share it.",
          createdAt: "4h ago",
          likeCount: 1,
          replyCount: 0,
          replies: [],
          hasLiked: false,
          canLike: false,
          canUpdate: false,
          canDelete: false,
        }));

  return {
    id: dto.comment_id,
    authorName: mockAuthorName,
    content: dto.text,
    likeCount: dto.like_count,
    replyCount: dto.reply_count,
    replies: mockReplies,
    createdAt: dto.created_at,
    hasLiked: dto.has_user_liked,
    canLike: dto.can_user_like,
    canUpdate: dto.can_user_update,
    canDelete: dto.can_user_delete,
  };
}

export function adaptReplyDTO(dto: ReplyDTO): CommentData {
  const mockAuthorNames = [
    "Ravi Kumar",
    "Anita Desai",
    "System",
    "Arjun Singh",
  ];
  const authorId = Math.floor(
    getStableRandom(dto.author_id) / mockAuthorNames.length,
  );
  const mockAuthorName = mockAuthorNames[authorId];

  return {
    id: dto.reply_id,
    authorName: mockAuthorName,
    content: dto.text,
    likeCount: dto.like_count,
    replyCount: 0,
    replies: [],
    createdAt: dto.created_at,
    hasLiked: dto.has_user_liked,
    canLike: dto.can_user_like,
    canUpdate: dto.can_user_update,
    canDelete: dto.can_user_delete,
  };
}
