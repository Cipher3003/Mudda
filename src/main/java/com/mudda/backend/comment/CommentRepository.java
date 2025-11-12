package com.mudda.backend.comment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByIssueIdAndParentIdIsNull(long issueId, Pageable pageable);

    List<Comment> findByIssueId(long issueId);

    // Find all replies for a specific parent comment
    List<Comment> findByParentId(long parentId);

    Page<Comment> findByParentId(long parentId, Pageable pageable);

    List<Comment> findByParentIdIn(List<Long> parentId);

    long countByParentId(long parentId);

    void deleteByParentId(long parentId);
}
