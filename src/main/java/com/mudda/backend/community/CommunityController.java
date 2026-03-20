package com.mudda.backend.community;

import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.CommunityResponse;
import com.mudda.backend.community.dto.CreateCommunityRequest;
import com.mudda.backend.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/communities")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @Operation(summary = "Find communities near a GPS coordinate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of nearby communities"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates")
    })
    @GetMapping("/nearby")
    public ResponseEntity<List<CommunityResponse>> getNearbyCommunities(
            @RequestParam(name = "lat") double lat,
            @RequestParam(name = "lon") double lon,
            @RequestParam(name = "radiusMeters", defaultValue = "5000") double radiusMeters) {

        log.debug("Finding communities near ({}, {}) within {} m", lat, lon, radiusMeters);
        return ResponseEntity.ok(communityService.findNearbyCommunities(lat, lon, radiusMeters));
    }

    @Operation(summary = "Get community details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponse> getCommunityById(@PathVariable(name = "id") Long id) {
        return communityService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List members of a community")
    @GetMapping("/{id}/members")
    public ResponseEntity<Page<CommunityMemberResponse>> getMembers(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communityService.getMembers(id, pageable));
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @Operation(summary = "Create a new community")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Community created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<CommunityResponse> createCommunity(
            @Valid @RequestBody CreateCommunityRequest request) {

        Long userId = SecurityUtil.getUserIdOrNull();
        log.info("Creating new community '{}' by user {}", request.name(), userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityService.createCommunity(userId, request));
    }

    @Operation(summary = "Request to join a community")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Join request submitted (or existing membership returned)"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    @PostMapping("/{id}/join")
    public ResponseEntity<CommunityMemberResponse> joinCommunity(@PathVariable(name = "id") Long id) {
        Long userId = SecurityUtil.getUserIdOrNull();
        log.info("User {} requesting to join community {}", userId, id);
        return ResponseEntity.ok(communityService.joinCommunity(id, userId));
    }

    // endregion
}
