package com.mudda.backend.initiative;

import com.mudda.backend.initiative.dto.HubResponse;
import com.mudda.backend.initiative.dto.InitiativeResponse;
import com.mudda.backend.initiative.dto.ParticipateRequest;
import com.mudda.backend.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class InitiativeController {

    private final InitiativeService initiativeService;

    public InitiativeController(InitiativeService initiativeService) {
        this.initiativeService = initiativeService;
    }

    // ----------- PUBLIC READ -----------------

    @Operation(summary = "Get community hub — active initiatives and aggregate progress")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hub data returned"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    @GetMapping("/communities/{id}/hub")
    public ResponseEntity<HubResponse> getCommunityHub(@PathVariable(name = "id") Long id) {
        log.debug("Fetching hub for community {}", id);
        return ResponseEntity.ok(initiativeService.getCommunityHub(id));
    }

    // ----------- AUTH COMMANDS -----------------

    @Operation(summary = "RSVP or pledge to an initiative")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participation recorded (or existing returned)"),
            @ApiResponse(responseCode = "404", description = "Initiative not found")
    })
    @PostMapping("/initiatives/{id}/participate")
    public ResponseEntity<InitiativeResponse> participate(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody ParticipateRequest request) {

        Long userId = SecurityUtil.getUserIdOrNull();
        log.info("User {} participating in initiative {}", userId, id);
        return ResponseEntity.ok(initiativeService.participate(id, userId, request));
    }
}
