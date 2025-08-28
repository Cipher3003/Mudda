package com.mudda.backend.postgres.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mudda.backend.postgres.models.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // generates a select query to find comments where issueId equals parameter
    // issueId
    List<Comment> findByIssueId(Long issueId);

    Optional<Comment> findByCommentId(Long id);
}
