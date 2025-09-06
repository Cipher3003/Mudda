package com.mudda.backend.issue;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {
    // TODO: find issues by location feature

    private final IssueService issueService;

    @GetMapping
    public ResponseEntity<Page<IssueSummaryResponse>> getAllIssues(
            IssueFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") IssueSortBy sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page,
                size,
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

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IssueSummaryResponse>> getIssuesByStatus(@PathVariable IssueStatus status) {
        return ResponseEntity.ok(issueService.findByStatus(status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IssueSummaryResponse>> getIssuesByUserName(@PathVariable Long userId) {
        return ResponseEntity.ok(issueService.findByUserId(userId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<IssueSummaryResponse>> getIssuesByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(issueService.findByCategoryId(categoryId));
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
