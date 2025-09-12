package com.mudda.backend.comment;

public class CommentMapper {
    public static CommentResponse toCommentResponse(Comment comment, Long likeCount) {
        return new CommentResponse(
                comment.getCommentId(),
                comment.getText(),
                likeCount
        );
    }

    public static ReplyResponse toReplyResponse(Comment comment, Long likeCount) {
        return new ReplyResponse(
                comment.getCommentId(),
                comment.getText(),
                likeCount,
                comment.getParentId()
        );
    }

    public static Comment toComment(CreateCommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setText(commentRequest.text());
        comment.setIssueId(commentRequest.issueId());
        comment.setUserId(commentRequest.userId());
        return comment;
    }

    public static Comment toReply(Long parentId, CreateCommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setText(commentRequest.text());
        comment.setIssueId(commentRequest.issueId());
        comment.setUserId(commentRequest.userId());
        comment.setParentId(parentId);
        return comment;
    }


}
