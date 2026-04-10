package com.mudda.backend.seed;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/seed")
public class SeedController {

    private final SeedServiceV2 seedServiceV2;

    public SeedController(SeedServiceV2 seedServiceV2) {
        this.seedServiceV2 = seedServiceV2;
    }

    @PostMapping
    public ResponseEntity<List<String>> seedData(@RequestBody @Valid CreateSeedRequest seedRequest) {
        log.debug("Received request for seed {}", seedRequest);
        return ResponseEntity.ok(seedServiceV2.seedDatabase(seedRequest));
    }

    @PostMapping("/json")
    public ResponseEntity<List<String>> seedJsonData() {
        // TODO: build this service in new seed
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<List<String>> clearDatabase() {
        return ResponseEntity.ok(seedServiceV2.clearDatabase());
    }
}
