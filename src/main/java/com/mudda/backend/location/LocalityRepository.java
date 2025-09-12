package com.mudda.backend.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, Long> {

    Optional<Locality> findByCityAndState(String city, String state);
}
