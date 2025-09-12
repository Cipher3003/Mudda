package com.mudda.backend.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // generates a select query to find comments where issueId equals parameter
    // issueId
    List<Comment> findByParentIdIsNull();

    List<Comment> findByIssueIdAndParentIdIsNull(Long issueId);

    // Find all replies for a specific parent comment
    List<Comment> findByParentId(Long parentId);

    void deleteByParentId(Long parentId);
}
