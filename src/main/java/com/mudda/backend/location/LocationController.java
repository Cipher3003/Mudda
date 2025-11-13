package com.mudda.backend.location;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Tag(name = "Location Management",
        description = "Works with default postgres geocoordinate object will modify it later to be lightweight")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // ----------- PUBLIC READ -----------------
    // #region Queries (Read Operations)

    //    TODO: maybe add filter request
    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAll() {
        return ResponseEntity.ok(locationService.findAllLocations());
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponse> getById(@PathVariable(name = "locationId") long locationId) {
        return locationService.findById(locationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // #endregion

    // ----------- AUTH COMMANDS -----------------
    // #region Commands (Write Operations)

    @Transactional
    @PostMapping
    public ResponseEntity<LocationResponse> create(@RequestBody @Valid CreateLocationRequest locationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.createLocation(locationRequest));
    }

    @Transactional
    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponse> update(@PathVariable(name = "locationId") long locationId,
                                                   @RequestBody UpdateLocationRequest locationRequest) {
        return ResponseEntity.ok(locationService.updateLocation(locationId, locationRequest));
    }

    //    NOTE: for developer use only
    @Transactional
    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> delete(@PathVariable(name = "locationId") long locationId) {
        locationService.deleteLocation(locationId);
        return ResponseEntity.noContent().build();
    }

    // #endregion
}
