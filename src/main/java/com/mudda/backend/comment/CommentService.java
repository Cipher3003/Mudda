package com.mudda.backend.comment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Page<CommentDetailResponse> findCommentsWithLikes(long issueId, Pageable pageable, long userId);

    Page<ReplyResponse> findAllReplies(long parentId, Pageable pageable, long userId);

    Optional<CommentDetailResponse> findById(long id, long userId);

    CommentResponse createComment(long issueId, CreateCommentRequest createCommentRequest);

    CommentResponse createReply(long commentId, CreateCommentRequest createCommentRequest);

    CommentLikeResponse likeComment(long commentId, long userId);

    CommentResponse updateComment(long id, String text);

    void deleteComment(long id);

    void deleteAllCommentsByIssueId(long issueId);

    CommentLikeResponse deleteLikeComment(long commentId, long userId);

}
