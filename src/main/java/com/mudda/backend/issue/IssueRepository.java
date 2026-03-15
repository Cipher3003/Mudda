package com.mudda.backend.issue;

import com.mudda.backend.issue.dto.IssueClusterQueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    List<Issue> findByUserId(long userId);

    Page<Issue> findByUserId(long userId, Pageable pageable);

    @Query(value = """
            SELECT
                FLOOR(ST_X(I.coordinate) / :cellSize) AS cellX,
                FLOOR(ST_Y(I.coordinate) / :cellSize) AS cellY,
                I.issue_category AS category,
                COUNT(*) AS count,
                ST_Y(ST_Centroid(ST_Collect(I.coordinate))) AS centerLatitude,
                ST_X(ST_Centroid(ST_Collect(I.coordinate))) AS centerLongitude
            FROM issues I
            WHERE I.coordinate && ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)
            GROUP BY cellX, cellY, category
            """, nativeQuery = true)
    List<IssueClusterQueryResult> getIssueClusters(
            @Param("minLat") double minLatitude,
            @Param("maxLat") double maxLatitude,
            @Param("minLng") double minLongitude,
            @Param("maxLng") double maxLongitude,
            @Param("cellSize") double cellSize
    );

    @Modifying
    @Query("UPDATE Issue i SET i.voteCount = i.voteCount + 1 WHERE i.id = :issueId")
    void incrementVoteCount(long issueId);

    @Modifying
    @Query("UPDATE Issue i SET i.voteCount = i.voteCount - 1 WHERE i.id = :issueId")
    void decrementVoteCount(long issueId);

    @Query("SELECT i.voteCount FROM Issue i WHERE i.id = :issueId")
    Optional<Long> getCountById(long issueId);

    @Query("UPDATE Issue i SET i.commentCount = i.commentCount + 1 WHERE i.id = :issueId")
    @Modifying
    void incrementCommentCount(long issueId);

    @Query("UPDATE Issue i SET i.commentCount = i.commentCount - 1 WHERE i.id = :issueId")
    @Modifying
    void decrementCommentCount(long issueId);

}