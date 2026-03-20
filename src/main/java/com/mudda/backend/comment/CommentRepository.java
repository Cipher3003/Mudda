package com.mudda.backend.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByIssueIdAndParentIdIsNull(long issueId, Pageable pageable);

    @Query("""
             SELECT c.id, c.text, c.issueId, c.parentId, c.likeCount, c.replyCount, c.createdAt,
             c.userId ,u.username, u.profileImageUrl, u.deletedAt AS authorDeletedAt,
             FALSE AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.issueId = :issueId
            \s""")
    Page<CommentProjection> findCommentFeed(long issueId, Pageable pageable);

    @Query("""
             SELECT c.id, c.text, c.issueId, c.parentId, c.likeCount, c.replyCount, c.createdAt,
             c.userId, u.username, u.profileImageUrl, u.deletedAt AS authorDeletedAt,
             EXISTS (SELECT 1 FROM CommentLike cl WHERE cl.id.commentId = c.id AND cl.id.userId = :userId) AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId  WHERE c.issueId = :issueId
            \s""")
    Page<CommentProjection> findCommentFeed(long userId, long issueId, Pageable pageable);

    @Query("""
             SELECT c.id, c.text, c.issueId, c.parentId, c.likeCount, c.replyCount, c.createdAt,
             c.userId, u.username, u.profileImageUrl, u.deletedAt AS authorDeletedAt,
             FALSE AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.id = :commentId
            \s""")
    Optional<CommentProjection> findCommentById(long commentId);

    @Query("""
             SELECT c.id, c.text, c.issueId, c.parentId, c.likeCount, c.replyCount, c.createdAt,
             c.userId, u.username, u.profileImageUrl, u.deletedAt AS authorDeletedAt,
             EXISTS (SELECT 1 FROM CommentLike cl WHERE cl.id.commentId = c.id AND cl.id.userId = :userId) AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.id = :commentId
            \s""")
    Optional<CommentProjection> findCommentById(long userId, long commentId);

    @Query("""
             SELECT c.id, c.text, c.issueId, c.parentId, c.likeCount, c.replyCount, c.createdAt,
             c.userId, u.username, u.profileImageUrl, u.deletedAt AS authorDeletedAt,
             FALSE AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.parentId = :parentId
            \s""")
    Page<CommentProjection> findReplyFeed(long parentId, Pageable pageable);

    @Query("""
             SELECT c.id, c.text, c.issueId, c.parentId, c.likeCount, c.replyCount, c.createdAt,
             c.userId, u.username, u.profileImageUrl, u.deletedAt AS authorDeletedAt,
             EXISTS (SELECT 1 FROM CommentLike cl WHERE cl.id.commentId = c.id AND cl.id.userId = :userId) AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.parentId = :parentId
            \s""")
    Page<CommentProjection> findReplyFeed(long userId, long parentId, Pageable pageable);

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

    @Modifying
    @Query("""
            DELETE FROM Comment WHERE id = :topCommentId OR parentId = :topCommentId
            """)
    void deleteAllFromTopLevelComment(long topCommentId);

    @Modifying
    @Query("UPDATE Comment C SET C.replyCount = C.replyCount + 1 WHERE C.id = :commentId")
    void incrementReplyCount(long commentId);

    @Modifying
    @Query("UPDATE Comment C SET C.replyCount = C.replyCount - 1 WHERE C.id = :commentId")
    void decrementReplyCount(long commentId);

    @Modifying
    @Query("UPDATE Comment C SET C.likeCount = C.likeCount + 1 WHERE C.id = :commentId")
    void incrementLikeCount(long commentId);

    @Modifying
    @Query("UPDATE Comment C SET C.likeCount = C.likeCount - 1 WHERE C.id = :commentId")
    void decrementLikeCount(long commentId);
}
