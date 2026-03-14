package com.mudda.backend.comment;

import com.mudda.backend.comment.dto.CommentDetailResponse;
import com.mudda.backend.comment.dto.CommentResponse;
import com.mudda.backend.comment.dto.CreateCommentRequest;
import com.mudda.backend.comment.dto.ReplyResponse;

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
            boolean hasLiked,
            boolean canLike,
            boolean canUpdate,
            boolean canDelete
    ) {
        return new CommentDetailResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getIssueId(),
                likeCount,
                replyCount,
                comment.getCreatedAt(),
                hasLiked,
                canLike,
                canUpdate,
                canDelete
        );
    }

    public static ReplyResponse toReplyResponse(
            Comment comment,
            long likeCount,
            boolean hasLiked,
            boolean canLike,
            boolean canUpdate,
            boolean canDelete
    ) {
        return new ReplyResponse(
                comment.getCommentId(),
                comment.getText(),
                comment.getUserId(),
                comment.getParentId(),
                likeCount,
                comment.getCreatedAt(),
                hasLiked,
                canLike,
                canUpdate,
                canDelete
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
