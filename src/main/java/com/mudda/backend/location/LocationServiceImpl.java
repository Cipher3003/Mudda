package com.mudda.backend.location;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public List<LocationResponse> findAllLocations() {
        return locationRepository.findAll().stream().map(LocationMapper::toResponse).toList();
    }

    @Override
    public Optional<LocationResponse> findById(Long id) {
        return locationRepository.findById(id).map(LocationMapper::toResponse);
    }

    // #endregion
    // #region Commands (Write Operations)

    @Override
    public LocationResponse createLocation(CreateLocationRequest locationRequest) {
        Location location = LocationMapper.toLocation(locationRequest);
        Location saved = locationRepository.save(location);
        return LocationMapper.toResponse(saved);
    }

    @Override
    public List<Long> createLocations(List<CreateLocationRequest> locationRequests) {
        List<Location> locations = locationRepository.saveAll(
                locationRequests
                        .stream()
                        .map(LocationMapper::toLocation)
                        .toList()
        );

        return locations.stream().map(Location::getLocationId).toList();
    }

    @Override
    public LocationResponse updateLocation(Long id, UpdateLocationRequest locationRequest) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> notFound(id));

        existing.updateDetails(
                locationRequest.addressLine(),
                locationRequest.pinCode(),
                locationRequest.city(),
                locationRequest.state()
        );

        Location updated = locationRepository.save(existing);
        return LocationMapper.toResponse(updated);
    }

    @Override
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id))
            throw notFound(id);

        locationRepository.deleteById(id);
    }

    // #endregion

    //    ------------------------------
    //    Helpers
    //    ------------------------------
    private EntityNotFoundException notFound(Long id) {
        return new EntityNotFoundException("Location not found with id: %d".formatted(id));
    }
}
