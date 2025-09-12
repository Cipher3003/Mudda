package com.mudda.backend.location;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Location Management",
        description = "Works with default postgres geocoordinate object will modify it later to be lightweight")
// TODO: normalize to not use a lot fields in requests, validation, DTO and error handling
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAll() {
        return ResponseEntity.ok(locationService.findAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getById(@PathVariable Long id) {
        return locationService.findLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: Validate input
    @PostMapping
    public ResponseEntity<LocationResponse> create(@RequestBody @Valid CreateLocationRequest locationRequest) {
        LocationResponse saved = locationService.createLocation(locationRequest);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // TODO: Validate input
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> update(@PathVariable Long id, @RequestBody UpdateLocationRequest locationRequest) {
        try {
            LocationResponse updated = locationService.updateLocation(id, locationRequest);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // TODO: not found check
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            locationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
