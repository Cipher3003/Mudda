package com.mudda.backend.seed;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/seed")
public class SeedController {

    private final SeedService seedService;

    public SeedController(SeedService seedService) {
        this.seedService = seedService;
    }

    @PostMapping
    public ResponseEntity<List<String>> generateTestData(
            @RequestBody @Valid CreateSeedRequest seedRequest) {
        log.debug("Received request for seed {}", seedRequest);
        return ResponseEntity.ok(seedService.seedDatabase(seedRequest));
    }

    @PostMapping("/json")
    public ResponseEntity<List<String>> seedJsonData() {
        return ResponseEntity.ok(seedService.seedDatabaseFromJson());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<List<String>> clearDatabase() {
        return ResponseEntity.ok(seedService.clearDatabase());
    }
}
