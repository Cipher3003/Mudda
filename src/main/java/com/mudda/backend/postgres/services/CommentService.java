package com.mudda.backend.postgres.services;

import com.mudda.backend.postgres.models.Comment;
import com.mudda.backend.postgres.models.Reply;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<Comment> findAllComments();

    List<Comment> findByIssueId(Long issueId);

    List<Reply> findAllReplies(Long commentId);

    Optional<Comment> findById(Long id);

    Comment createComment(Comment comment);

    Comment updateComment(Long id, String text);

    void deleteComment(Long id);

    void deleteAllCommentsByIssueId(Long issueId);

}
