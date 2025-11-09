package com.mudda.backend.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    long countByCommentId(Long commentId);

    List<CommentLike> findByUserIdAndCommentIdIn(long userId, List<Long> ids);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    void deleteByCommentId(Long commentId);

    void deleteAllByCommentId(List<Long> commentIds);

    void deleteByCommentIdAndUserId(Long commentId, Long userId);


}
