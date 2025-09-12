package com.mudda.backend.location;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocalityRepository localityRepository;

    public LocationServiceImpl(LocationRepository locationRepository,
                               LocalityRepository localityRepository) {
        this.locationRepository = locationRepository;
        this.localityRepository = localityRepository;
    }

    @Override
    public List<LocationResponse> findAllLocations() {
        return locationRepository.findAll().stream().map(location -> {
            Locality locality = localityRepository.findById(location.getLocalityId())
                    .orElseThrow(EntityNotFoundException::new);
            return LocationResponse.from(location, locality);
        }).toList();
    }

    @Override
    public Optional<LocationResponse> findLocationById(Long id) {
        return locationRepository.findById(id)
                .flatMap(location -> localityRepository.findById(location.getLocalityId())
                        .map(locality -> LocationResponse.from(location, locality)));
    }

    @Override
    public LocationResponse createLocation(CreateLocationRequest locationRequest) {
        Locality locality = findOrCreateLocality(locationRequest.city(), locationRequest.state());
        CoordinateDTO coordinateDTO = locationRequest.coordinate();

        Coordinate coordinate = new Coordinate(coordinateDTO.x(), coordinateDTO.y());

        Location location = new Location(
                locationRequest.addressLine(),
                locationRequest.pinCode(),
                locality.getLocalityId(),
                PointFactory.createPoint(coordinate));

        Location saved = locationRepository.save(location);
        return LocationResponse.from(saved, locality);
    }

    @Override
    public LocationResponse updateLocation(Long id, UpdateLocationRequest locationRequest) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id " + id));

        existing.setAddressLine(locationRequest.addressLine());
        existing.setPinCode(locationRequest.pinCode());

        Location updated = locationRepository.save(existing);
        Locality locality = localityRepository.findById(updated.getLocalityId())
                .orElseThrow(EntityNotFoundException::new);
        return LocationResponse.from(updated, locality);
        // TODO: refactor and add logic for locality, make a single controller layer for locationRequest + locality service layers
        // TODO: even for locality update, we must check if any records of the locality are even getting referenced in the locationRequest table
        // if yes ( > 1) , add a new locality record and update the locationRequest record
        // if no ( = 1), we can change the locality record
    }

    @Override
    public void deleteById(Long id) {
        if (!locationRepository.existsById(id))
            throw new EntityNotFoundException("Location not found with id " + id);

        locationRepository.deleteById(id);
    }

    //    Helper functions
    Locality findOrCreateLocality(String city, String state) {
        Optional<Locality> locality = localityRepository.findByCityAndState(city, state);
        if (locality.isPresent()) return locality.get();
        Locality newLocality = new Locality(city, state);
        return localityRepository.save(newLocality);
    }
}
