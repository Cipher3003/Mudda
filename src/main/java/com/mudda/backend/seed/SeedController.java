package com.mudda.backend.seed;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/seed")
public class SeedController {

    private final SeedService seedService;
    private final FastSeedService fastSeedService;

    public SeedController(SeedService seedService, FastSeedService fastSeedService) {
        this.seedService = seedService;
        this.fastSeedService = fastSeedService;
    }

    @GetMapping(value = "/users", produces = "text/csv")
    public void seedUsers(@RequestParam @Positive int count, HttpServletResponse response) throws IOException {
        log.debug("Seeding database with {} users directly", count);

        List<String[]> data = fastSeedService.seedUsers(count);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");

        PrintWriter writer = response.getWriter();
        writer.println("username,name,phone_number,email,password");

        log.debug("Writing response csv data, name: {}, count: {}", "users.csv", count);

        for (String[] row : data)
            writer.println(String.join(",", row));
    }

    @PostMapping
    public ResponseEntity<List<String>> generateTestData(@RequestBody @Valid CreateSeedRequest seedRequest) {
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
