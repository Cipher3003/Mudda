package com.mudda.backend.comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<Comment> findAllComments();

    List<Comment> findCommentsByIssueId(Long issueId);

    List<Comment> findAllReplies(Long parentId);

    Optional<Comment> findById(Long id);

    Comment createComment(Comment comment);

    Comment updateComment(Long id, String text);

    void deleteComment(Long id);

    void deleteAllCommentsByIssueId(Long issueId);

}
