package com.mudda.backend.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT count(*) FROM CommentLike cl WHERE cl.id.commentId = :commentId")
    long countByCommentId(Long commentId);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.id.commentId = :commentId")
    void deleteByCommentId(long commentId);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.id.userId = :userId")
    void deleteByUserId(long userId);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.id.commentId IN :commentIds")
    void deleteAllByCommentIdIn(List<Long> commentIds);

    @Modifying
    @Query("DELETE FROM CommentLike cl WHERE cl.id.commentId = :commentId AND cl.id.userId = :userId")
    void deleteByCommentIdAndUserId(long commentId, long userId);

    @Modifying
    @Query("""
             DELETE FROM CommentLike cl WHERE cl.id.commentId = :topCommentId OR cl.id.commentId IN
             (SELECT id FROM Comment WHERE parentId = :topCommentId)
            """)
    void deleteFromTopLevelComment(long topCommentId);
}
