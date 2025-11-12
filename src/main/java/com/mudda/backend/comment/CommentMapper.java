package com.mudda.backend.comment;

public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getIssueId(),
                comment.getCreatedAt());
    }

    public static CommentDetailResponse toCommentResponse(Comment comment,
                                                          long likeCount,
                                                          long replyCount,
                                                          boolean hasUserLiked) {
        return new CommentDetailResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getIssueId(),
                likeCount,
                replyCount,
                comment.getCreatedAt(),
                hasUserLiked);
    }

    public static ReplyResponse toReplyResponse(Comment comment, long likeCount, boolean hasUserLiked) {
        return new ReplyResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getParentId(),
                likeCount,
                comment.getCreatedAt(),
                hasUserLiked);
    }

    public static Comment toComment(CreateCommentRequest commentRequest, long issueId) {
        return new Comment(
                commentRequest.text(),
                issueId,
                commentRequest.userId());
    }

    public static Comment toReply(CreateCommentRequest commentRequest, long issueId, long parentId) {
        return new Comment(
                commentRequest.text(),
                parentId,
                issueId,
                commentRequest.userId());
    }

}
