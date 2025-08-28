package com.mudda.backend.postgres.services;

import com.mudda.backend.postgres.models.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<Location> findAllLocations();

    Optional<Location> findLocationById(Long id);

    Location createLocation(Location location);

    Location updateLocation(Long id, Location location);

    void deleteById(Long id);
}
