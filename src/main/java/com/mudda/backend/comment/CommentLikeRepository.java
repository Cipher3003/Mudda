package com.mudda.backend.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    long countByCommentId(Long commentId);

    List<CommentLike> findByUserIdAndCommentIdIn(long userId, List<Long> ids);

    boolean existsByCommentIdAndUserId(long commentId, long userId);

    void deleteByCommentId(long commentId);

    void deleteByUserId(long userId);

    //    TODO: maybe use modifying query to directly delete instead of default fetch -> delete
    void deleteAllByCommentIdIn(List<Long> commentIds);

    void deleteByCommentIdAndUserId(long commentId, long userId);

}
