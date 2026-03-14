package com.mudda.backend.location;

import com.mudda.backend.location.dto.CreateLocationRequest;
import com.mudda.backend.location.dto.LocationResponse;
import com.mudda.backend.location.dto.UpdateLocationRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // region Queries (Read Operations)

    public List<LocationResponse> findAllLocations() {
        return locationRepository.findAll().stream().map(LocationMapper::toResponse).toList();
    }

    public Optional<LocationResponse> findById(Long id) {
        return locationRepository.findById(id).map(LocationMapper::toResponse);
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    public LocationResponse createLocation(CreateLocationRequest locationRequest) {
        Location location = LocationMapper.toLocation(locationRequest);
        Location saved = locationRepository.save(location);
        log.info("Created location with id {}", saved.getLocationId());
        return LocationMapper.toResponse(saved);
    }

    @Transactional
    public List<Long> createLocations(List<CreateLocationRequest> locationRequests) {
        List<Location> locations = locationRepository.saveAll(
                locationRequests
                        .stream()
                        .map(LocationMapper::toLocation)
                        .toList()
        );

        log.info("Created {} locations", locations.size());
        return locations.stream().map(Location::getLocationId).toList();
    }

//    TODO: multiple functions saving is not pretty

    @Transactional
    public void saveLocations(List<Location> locations) {
        locationRepository.saveAll(locations);
    }

    @Transactional
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
        log.info("Updated location with id {}", updated.getLocationId());
        return LocationMapper.toResponse(updated);
    }

    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id))
            throw notFound(id);

        locationRepository.deleteById(id);
        log.info("Deleted location with id {}", id);
    }

    // endregion

    private EntityNotFoundException notFound(Long id) {
        return new EntityNotFoundException("Location not found with id: %d".formatted(id));
    }
}
