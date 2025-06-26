//package com.mudda.backend.services.impl;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class LocationServiceImpl implements LocationService {
//
//    @Autowired
//    private LocationRepository locationRepository;
//
//    @Override
//    public Location createLocation(Location location) {
//        return locationRepository.save(location);
//    }
//
//    @Override
//    public Optional<Location> findLocationById(Long id) {
//        return locationRepository.findById(id);
//    }
//
//    @Override
//    public List<Location> findAllLocations() {
//        return locationRepository.findAll();
//    }
//
//    @Override
//    public void deleteLocation(Long id) {
//        locationRepository.deleteById(id);
//    }
//}
