package com.mudda.backend.mongodb.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.mongodb.models.Location;
import org.springframework.stereotype.Service;

@Service
public interface LocationService {
    Location createLocation(Location location);
    Optional<Location> findLocationById(Long id);
    List<Location> findAllLocations();
    void deleteLocation(Long id);
}

