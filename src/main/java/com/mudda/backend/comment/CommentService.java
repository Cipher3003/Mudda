package com.mudda.backend.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Page<CommentDetailResponse> findCommentsWithLikes(long issueId, Pageable pageable, long userId);

    Page<ReplyResponse> findAllReplies(long parentId, Pageable pageable, long userId);

    Optional<CommentDetailResponse> findById(long id, long userId);

    CommentResponse createComment(long issueId, CreateCommentRequest createCommentRequest);

    CommentResponse createReply(long commentId, CreateCommentRequest createCommentRequest);

    CommentResponse updateComment(long id, String text);

    void deleteComment(long id);

    void deleteAllCommentsByUserId(long userId);

    void deleteAllCommentsByIssueId(long issueId);

    void deleteAllCommentsByIssueIds(List<Long> issueIds);

    CommentLikeResponse likeComment(long commentId, long userId);

    CommentLikeResponse deleteLikeComment(long commentId, long userId);

}
