package com.mudda.backend.location;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<LocationResponse> findAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationResponse::from)
                .toList();
    }

    @Override
    public Optional<LocationResponse> findLocationById(Long id) {
        return locationRepository.findById(id)
                .map(LocationResponse::from);
    }

    @Override
    public LocationResponse createLocation(CreateLocationRequest locationRequest) {
        CoordinateDTO coordinateDTO = locationRequest.coordinate();
        Coordinate coordinate = new Coordinate(coordinateDTO.x(), coordinateDTO.y());

        Location location = new Location(
                locationRequest.addressLine(),
                locationRequest.pinCode(),
                locationRequest.city(),
                locationRequest.state(),
                PointFactory.createPoint(coordinate));

        Location saved = locationRepository.save(location);
        return LocationResponse.from(saved);
    }

    @Override
    public LocationResponse updateLocation(Long id, UpdateLocationRequest locationRequest) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id " + id));

        existing.setAddressLine(locationRequest.addressLine());
        existing.setPinCode(locationRequest.pinCode());
        existing.setCity(locationRequest.city());
        existing.setState(locationRequest.state());

        Location updated = locationRepository.save(existing);
        return LocationResponse.from(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!locationRepository.existsById(id))
            throw new EntityNotFoundException("Location not found with id " + id);

        locationRepository.deleteById(id);
    }
}
