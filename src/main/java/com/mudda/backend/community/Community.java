package com.mudda.backend.community;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Polygon;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "communities")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "communities_seq")
    @SequenceGenerator(name = "communities_seq", sequenceName = "communities_id_seq", allocationSize = 50)
    @Column(name = "community_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * PostGIS polygon defining the community's physical boundary.
     * SRID 4326 = WGS84 GPS coordinates — same convention as {@link com.mudda.backend.location.Location}.
     */
    @Column(name = "location_geofence", columnDefinition = "geometry(Polygon, 4326)", nullable = false)
    private Polygon locationGeofence;

    /**
     * Soft FK to users.user_id — the user who owns/administers this community.
     * Uses a plain Long rather than @ManyToOne to keep queries predictable and avoid N+1.
     */
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "community_status", nullable = false)
    private CommunityStatus status = CommunityStatus.TRIAL;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public Community(String name, String description, Polygon locationGeofence, Long ownerId) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Community name cannot be empty");
        if (locationGeofence == null || locationGeofence.isEmpty())
            throw new IllegalArgumentException("Community geofence cannot be empty");
        if (ownerId == null)
            throw new IllegalArgumentException("Community owner must be provided");

        this.name = name.trim();
        this.description = (description != null) ? description.trim() : null;
        this.locationGeofence = locationGeofence;
        this.ownerId = ownerId;
    }

    // ----- Domain Behaviour -------

    public void updateDetails(String name, String description) {
        if (name != null && !name.isBlank())
            setName(name.trim());
        if (description != null)
            setDescription(description.trim());
    }

    public void activate() {
        this.status = CommunityStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CommunityStatus.INACTIVE;
    }
}
