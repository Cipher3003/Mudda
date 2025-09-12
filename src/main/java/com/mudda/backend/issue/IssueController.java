package com.mudda.backend.issue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {
    // TODO: find issues by location feature

    private final IssueService issueService;

    @Operation(summary = "List issues with filters, sorting and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of issues"),
            @ApiResponse(responseCode = "400", description = "Invalid Input")
    })
    @GetMapping
    public ResponseEntity<Page<IssueSummaryResponse>> getAllIssues(
            @ModelAttribute(name = "filters") IssueFilterRequest filterRequest,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "CREATED_AT") IssueSortBy sort,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page, size,
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sort.getFieldName()).descending()
                        : Sort.by(sort.getFieldName()).ascending()
        );

        return ResponseEntity.ok(issueService.findAllIssues(filterRequest, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        return issueService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody CreateIssueRequest issueRequest) {
        return ResponseEntity.ok(issueService.createIssue(issueRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponse> updateIssue(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateIssueRequest issueRequest) {
        return ResponseEntity.ok(issueService.updateIssue(id, issueRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
    // TODO: fix nearby Issues via GeoJSONPoint
    //
    // @GetMapping("/nearby")
    // public ResponseEntity<List<Issue>> getNearbyIssues(
    // @RequestParam double lat,
    // @RequestParam double lon,
    // @RequestParam int radiusInKm) {
    //
    // Point point = new Point(lon, lat); // longitude first!
    // return ResponseEntity.ok(issueService.findByLocation_CoordinatesNear(point,
    // radiusInKm));
    // }
}
