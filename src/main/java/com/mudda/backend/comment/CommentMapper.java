package com.mudda.backend.comment;

public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getIssueId(),
                comment.getCreatedAt()
        );
    }

    public static CommentDetailResponse toCommentResponse(
            Comment comment,
            long likeCount,
            long replyCount,
            boolean hasUserLiked,
            boolean canUserLike,
            boolean canUserUpdate,
            boolean canUserDelete
    ) {
        return new CommentDetailResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getIssueId(),
                likeCount,
                replyCount,
                comment.getCreatedAt(),
                hasUserLiked,
                canUserLike,
                canUserUpdate,
                canUserDelete
        );
    }

    public static ReplyResponse toReplyResponse(
            Comment comment,
            long likeCount,
            boolean hasUserLiked,
            boolean canUserLike,
            boolean canUserUpdate,
            boolean canUserDelete
    ) {
        return new ReplyResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getParentId(),
                likeCount,
                comment.getCreatedAt(),
                hasUserLiked,
                canUserLike,
                canUserUpdate,
                canUserDelete
        );
    }

    public static Comment toComment(CreateCommentRequest commentRequest, long issueId, long userId) {
        return new Comment(
                commentRequest.text(),
                issueId,
                userId
        );
    }

    public static Comment toReply(CreateCommentRequest commentRequest, long issueId, long userId, long parentId) {
        return new Comment(
                commentRequest.text(),
                parentId,
                issueId,
                userId
        );
    }

}
