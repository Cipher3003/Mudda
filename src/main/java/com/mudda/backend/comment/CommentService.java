package com.mudda.backend.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Page<CommentDetailResponse> findCommentsWithLikes(long issueId, Pageable pageable, Long userId);

    Page<ReplyResponse> findAllReplies(long parentId, Pageable pageable, Long userId);

    Optional<CommentDetailResponse> findById(long id, Long userId);

    CommentResponse createComment(long issueId, Long userId, CreateCommentRequest createCommentRequest);

    List<Long> createComments(List<Long> issueIds, List<Long> userIds, List<CreateCommentRequest> createCommentRequests);

    CommentResponse createReply(long commentId, Long userId, CreateCommentRequest createCommentRequest);

    List<Long> createReplies(List<Long> parentIds, List<Long> userIds, List<Long> issueIds, List<CreateCommentRequest> createCommentRequests);

    void saveComments(List<Comment> comments);

    CommentResponse updateComment(long id, String text);

    void deleteComment(long id);

    void deleteAllCommentsByUserId(long userId);

    void deleteAllCommentsByIssueId(long issueId);

    void deleteAllCommentsByIssueIds(List<Long> issueIds);

    CommentLikeResponse likeComment(long commentId, Long userId);

    CommentLikeResponse deleteLikeComment(long commentId, Long userId);

}
