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

    //    TODO: add alias to queries with projection
    //    TODO: add count query to queries that return Page
    @Query(
            value = """
                     SELECT c.id AS id, c.text AS text, c.issueId AS issueId, c.parentId AS parentId,
                            c.likeCount AS likeCount, c.replyCount AS replyCount, c.createdAt AS createdAt,
                            c.userId AS userId, u.username AS username, u.profileImageUrl AS profileImageUrl,
                            u.deletedAt AS authorDeletedAt, FALSE AS hasLiked
                     FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.issueId = :issueId
                    """,
            countQuery = "SELECT count(c.id) FROM Comment c WHERE c.issueId = :issueId"
    )
    Page<CommentProjection> findCommentFeedPublic(long issueId, Pageable pageable);

    @Query(
            value = """
                     SELECT c.id AS id, c.text AS text, c.issueId AS issueId, c.parentId AS parentId,
                            c.likeCount AS likeCount, c.replyCount AS replyCount, c.createdAt AS createdAt,
                            c.userId AS userId, u.username AS username, u.profileImageUrl AS profileImageUrl,
                            u.deletedAt AS authorDeletedAt,
                            EXISTS (SELECT 1 FROM CommentLike cl WHERE cl.id.commentId = c.id AND cl.id.userId = :userId) AS hasLiked
                     FROM Comment c JOIN MuddaUser u ON c.userId = u.userId  WHERE c.issueId = :issueId
                    """,
            countQuery = "SELECT count(c.id) FROM Comment c WHERE c.issueId = :issueId"
    )
    Page<CommentProjection> findCommentFeedWithUser(long userId, long issueId, Pageable pageable);

    @Query("""
             SELECT c.id AS id, c.text AS text, c.issueId AS issueId,c.parentId AS parentId, c.likeCount AS likeCount,
                    c.replyCount AS replyCount, c.createdAt AS createdAt, c.userId AS userId, u.username AS username,
                    u.profileImageUrl AS profileImageUrl, u.deletedAt AS authorDeletedAt, FALSE AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.id = :commentId
            """)
    Optional<CommentProjection> findCommentById(long commentId);

    @Query("""
             SELECT c.id AS id, c.text AS text, c.issueId AS issueId,c.parentId AS parentId, c.likeCount AS likeCount,
                    c.replyCount AS replyCount, c.createdAt AS createdAt, c.userId AS userId, u.username AS username,
                    u.profileImageUrl AS profileImageUrl, u.deletedAt AS authorDeletedAt,
                    EXISTS (SELECT 1 FROM CommentLike cl WHERE cl.id.commentId = c.id AND cl.id.userId = :userId) AS hasLiked
             FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.id = :commentId
            """)
    Optional<CommentProjection> findCommentById(long userId, long commentId);

    @Query(
            value = """
                     SELECT c.id AS id, c.text AS text, c.issueId AS issueId, c.parentId AS parentId, c.likeCount AS likeCount,
                            c.replyCount AS replyCount, c.createdAt AS createdAt, c.userId AS userId, u.username AS username,
                            u.profileImageUrl AS profileImageUrl, u.deletedAt AS authorDeletedAt, FALSE AS hasLiked
                     FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.parentId = :parentId
                    """,
            countQuery = "SELECT count(c.id) FROM Comment c WHERE c.parentId = :parentId"
    )
    Page<CommentProjection> findReplyFeedPublic(long parentId, Pageable pageable);

    @Query(
            value = """
                     SELECT c.id AS id, c.text AS text, c.issueId AS issueId, c.parentId AS parentId, c.likeCount AS likeCount,
                            c.replyCount AS replyCount, c.createdAt AS createdAt, c.userId AS userId, u.username AS username,
                            u.profileImageUrl AS profileImageUrl, u.deletedAt AS authorDeletedAt,
                            EXISTS (SELECT 1 FROM CommentLike cl WHERE cl.id.commentId = c.id AND cl.id.userId = :userId) AS hasLiked
                     FROM Comment c JOIN MuddaUser u ON c.userId = u.userId WHERE c.parentId = :parentId
                    """,
            countQuery = "SELECT count(c.id) FROM Comment c WHERE c.parentId = :parentId"
    )
    Page<CommentProjection> findReplyFeedWithUser(long userId, long parentId, Pageable pageable);

    //    TODO: directly return ID where entity is not used
    List<Comment> findByUserId(long userId);

    List<Comment> findByIssueId(long issueId);

    List<Comment> findByIssueIdIn(List<Long> issueIds);

    List<Comment> findByParentIdIn(List<Long> parentId);

    long countByParentId(long parentId);

    @Modifying
    @Query("DELETE FROM Comment WHERE id = :topCommentId OR parentId = :topCommentId")
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
