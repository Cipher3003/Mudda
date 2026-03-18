package com.mudda.backend.comment;

import com.mudda.backend.comment.dto.CommentResponse;
import com.mudda.backend.comment.dto.CommentCreatedResponse;
import com.mudda.backend.comment.dto.CommentUpdateResponse;
import com.mudda.backend.comment.dto.CreateCommentRequest;

public class CommentMapper {

    public static CommentResponse toCommentResponseFromProj(CommentProjection p) {
        return new CommentResponse(
                p.getId(),
                p.getText(),
                p.getUserId(),
                p.getIssueId(),
                p.getParentId(),
                p.getLikeCount(),
                p.getReplyCount(),
                p.getCreatedAt(),
                p.getHasLiked(),
                false, false, false
        );
    }

    public static CommentResponse toCommentResponseFromProj(CommentProjection p, long userId) {
        return new CommentResponse(
                p.getId(),
                p.getText(),
                p.getUserId(),
                p.getIssueId(),
                p.getParentId(),
                p.getLikeCount(),
                p.getReplyCount(),
                p.getCreatedAt(),
                p.getHasLiked(),
                true,
                userId == p.getUserId(),
                userId == p.getUserId()
        );
    }

    public static CommentResponse toCommentResponseFromComment(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getUserId(),
                comment.getIssueId(),
                comment.getParentId(),
                comment.getLikeCount(),
                comment.getReplyCount(),
                comment.getCreatedAt(),
                false,
                false,
                false,
                false
        );
    }

    public static CommentCreatedResponse toCommentCreated(Comment comment) {
        return new CommentCreatedResponse(comment.getId(), comment.getCreatedAt());
    }

    public static CommentUpdateResponse toCommentUpdated(Comment comment) {
        return new CommentUpdateResponse(comment.getId(), comment.getText(), comment.getUpdatedAt());
    }

    public static Comment toComment(CreateCommentRequest commentRequest, long userId) {
        return new Comment(
                commentRequest.text(),
                commentRequest.issueId(),
                userId
        );
    }

    public static Comment toReply(
            CreateCommentRequest commentRequest,
            long issueId, long userId, long parentId
    ) {
        return new Comment(
                commentRequest.text(),
                parentId,
                issueId,
                userId
        );
    }

}
