package com.mudda.backend.comment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByIssueIdAndParentIdIsNull(long issueId, Pageable pageable);

    //    TODO: directly return ID where entity is not used
    List<Comment> findByUserId(long userId);

    List<Comment> findByIssueId(long issueId);

    List<Comment> findByIssueIdIn(List<Long> issueIds);

    // Find all replies for a specific parent comment
    List<Comment> findByParentId(long parentId);

    List<Comment> findByParentIdIn(List<Long> parentId);

    Page<Comment> findByParentId(long parentId, Pageable pageable);

    long countByParentId(long parentId);

    void deleteByParentId(long parentId);
}
