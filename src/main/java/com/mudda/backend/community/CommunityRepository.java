package com.mudda.backend.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    /**
     * Finds all ACTIVE communities whose geofence polygon contains the given GPS point.
     * Uses PostGIS ST_Contains — the Polygon and Point must share the same SRID (4326).
     */
    @Query(value = """
            SELECT * FROM communities c
            WHERE ST_Contains(c.location_geofence, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326))
            AND c.community_status = 'ACTIVE'
            """, nativeQuery = true)
    List<Community> findCommunitiesContainingPoint(@Param("lat") double lat, @Param("lon") double lon);

    /**
     * Radius-based fallback: finds communities whose geofence centroid is within
     * {@code radiusMeters} of the given GPS point. Useful when no geofence directly
     * contains the point (e.g., the user is just outside a boundary).
     */
    @Query(value = """
            SELECT * FROM communities c
            WHERE ST_DWithin(
                c.location_geofence::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                :radiusMeters
            )
            AND c.community_status = 'ACTIVE'
            ORDER BY ST_Distance(
                ST_Centroid(c.location_geofence)::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            )
            """, nativeQuery = true)
    List<Community> findNearbyCommunities(@Param("lat") double lat,
                                         @Param("lon") double lon,
                                         @Param("radiusMeters") double radiusMeters);

    Optional<Community> findByIdAndStatus(Long id, CommunityStatus status);

    List<Community> findByOwnerId(Long ownerId);
}
