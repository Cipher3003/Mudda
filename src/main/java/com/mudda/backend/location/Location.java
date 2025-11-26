package com.mudda.backend.location;

import jakarta.persistence.*;

import lombok.*;

import java.time.Instant;

import org.locationtech.jts.geom.Point;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_seq")
    @SequenceGenerator(name = "locations_seq", sequenceName = "locations_id_seq", allocationSize = 50)
    private Long locationId;

    @Column(nullable = false)
    private String addressLine;

    @Column(nullable = false)
    private String pinCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private Instant createdAt;

    /**
     * PostGIS geometry column for storing coordinates (longitude/latitude).
     * SRID 4326 = WGS84 standard for GPS coordinates.
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordinate;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public Location(String addressLine, String pinCode, String city, String state, Point coordinate) {

        if (addressLine == null || addressLine.isBlank())
            throw new IllegalArgumentException("Location address line cannot be empty");
        if (pinCode == null || pinCode.isBlank())
            throw new IllegalArgumentException("Location pin code cannot be empty");
        if (state == null || state.isBlank())
            throw new IllegalArgumentException("Location state cannot be empty");
        if (city == null || city.isBlank())
            throw new IllegalArgumentException("Location city cannot be empty");
        if (coordinate == null || coordinate.isEmpty())
            throw new IllegalArgumentException("Location coordinate cannot be empty");

        this.addressLine = addressLine.trim();
        this.pinCode = pinCode.trim();
        this.city = city;
        this.state = state;
        this.coordinate = coordinate;
    }

    // ----- Domain Behaviour -------

    public void updateDetails(String addressLine, String pinCode, String city, String state) {
        if (addressLine != null && !addressLine.isBlank())
            setAddressLine(addressLine);
        if (pinCode != null && !pinCode.isBlank())
            setPinCode(pinCode);
        if (city != null && !city.isBlank())
            setCity(city);
        if (state != null && !state.isBlank())
            setState(state);
    }
}