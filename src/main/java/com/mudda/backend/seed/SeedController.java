package com.mudda.backend.seed;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seed")
public class SeedController {

    private final SeedService seedService;

    public SeedController(SeedService seedService) {
        this.seedService = seedService;
    }

    @PostMapping
    public ResponseEntity<List<String>> generateTestData(
            @RequestBody @Valid CreateSeedRequest seedRequest
    ) {
        return ResponseEntity.ok(seedService.seedDatabase(seedRequest));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<List<String>> clearDatabase() {
        return ResponseEntity.ok(seedService.clearDatabase());
    }
}
