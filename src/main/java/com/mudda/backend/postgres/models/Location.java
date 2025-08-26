package com.mudda.backend.postgres.models;


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
    private Long id;

    private String addressLine;
    private String city;
    private String state;
    private String pincode;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();


    /**
     * PostGIS geometry column for storing coordinates (longitude/latitude).
     * SRID 4326 = WGS84 standard for GPS coordinates.
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordinates;
}