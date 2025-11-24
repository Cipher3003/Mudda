package com.mudda.backend.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    List<Issue> findByUserId(long userId);

    @Query(value = """
            SELECT
                FLOOR(ST_X(L.coordinate) / :cellSize) AS cellX,
                FLOOR(ST_Y(L.coordinate) / :cellSize) AS cellY,
                C.name AS category,
                COUNT(*) AS count,
                ST_Y(ST_Centroid(ST_Collect(L.coordinate))) AS centerLatitude,
                ST_X(ST_Centroid(ST_Collect(L.coordinate))) AS centerLongitude
            FROM issues I
            JOIN categories C ON I.issue_category_id = C.category_id
            JOIN locations L ON I.location_id = L.location_id
            WHERE L.coordinate && ST_MakeEnvelope(:minLng, :minLat, :maxLng, :maxLat, 4326)
            GROUP BY cellX, cellY, C.name
            """, nativeQuery = true)
    List<IssueClusterQueryResult> getIssueClusters(
            @Param("minLat") double minLatitude,
            @Param("maxLat") double maxLatitude,
            @Param("minLng") double minLongitude,
            @Param("maxLng") double maxLongitude,
            @Param("cellSize") double cellSize);
}