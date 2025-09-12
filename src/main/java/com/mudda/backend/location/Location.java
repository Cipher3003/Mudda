package com.mudda.backend.location;

import jakarta.persistence.*;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

import org.locationtech.jts.geom.Point;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private String addressLine;

    @Column(nullable = false)
    private String pinCode;

    @Column(name = "locality_id", nullable = false)
    private Long localityId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    /**
     * PostGIS geometry column for storing coordinates (longitude/latitude).
     * SRID 4326 = WGS84 standard for GPS coordinates.
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordinate;

    public Location(String addressLine, String pinCode, Long localityId, Point coordinate) {
        this.addressLine = addressLine;
        this.pinCode = pinCode;
        this.localityId = localityId;
        this.coordinate = coordinate;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}