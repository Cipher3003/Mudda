//package com.mudda.backend.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//
//@RestController
//@RequestMapping("/api/locations")
//@RequiredArgsConstructor
//public class LocationController {
//    private final LocationService locationService;
//
//    @PostMapping
//    public ResponseEntity<Location> create(@RequestBody Location location) {
//        return ResponseEntity.ok(locationService.createLocation(location));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Location> getById(@PathVariable Long id) {
//        return locationService.findLocationById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Location>> getAll() {
//        return ResponseEntity.ok(locationService.findAllLocations());
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        locationService.deleteLocation(id);
//        return ResponseEntity.noContent().build();
//    }
//}
