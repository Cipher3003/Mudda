package com.mudda.backend.postgres.services.impl;

import com.mudda.backend.postgres.models.Location;
import com.mudda.backend.postgres.repositories.LocationRepository;
import com.mudda.backend.postgres.services.LocationService;

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
    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Optional<Location> findLocationById(Long id) {
        return locationRepository.findById(id);
    }

    @Override
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public Location updateLocation(Long id, Location location) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id " + id));

        // copy fields - keep id
        existing.setAddressLine(location.getAddressLine());
        existing.setCity(location.getCity());
        existing.setState(location.getState());
        existing.setPincode(location.getPincode());
        existing.setCoordinates(location.getCoordinates());

        return locationRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location not found with id " + id);
        }
        locationRepository.deleteById(id);
    }
}
