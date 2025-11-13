package com.mudda.backend.location;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    List<LocationResponse> findAllLocations();

    Optional<LocationResponse> findById(Long id);

    LocationResponse createLocation(CreateLocationRequest locationRequest);

    LocationResponse updateLocation(Long id, UpdateLocationRequest locationRequest);

    void deleteLocation(Long id);
}
