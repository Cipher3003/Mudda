package com.mudda.backend.postgres.controllers;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.IssueStatus;
import com.mudda.backend.postgres.services.IssueService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {
    // TODO: find issues by location feature

    private final IssueService issueService;

    @GetMapping
    public ResponseEntity<List<Issue>> getAllIssues() {
        return ResponseEntity.ok(issueService.findAllIssues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long id) {
        return issueService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Issue>> getIssuesByStatus(@PathVariable IssueStatus status) {
        return ResponseEntity.ok(issueService.findByStatus(status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Issue>> getIssuesByUserName(@PathVariable Long userId) {
        return ResponseEntity.ok(issueService.findByUserId(userId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Issue>> getIssuesByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(issueService.findByCategoryId(categoryId));
    }

    // TODO: Validate input
    @PostMapping
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue) {
        Issue created = issueService.createIssue(issue);
        return ResponseEntity.ok(created);
    }

    // TODO: Validate input
    @PutMapping("/{id}")
    public ResponseEntity<Issue> updateIssue(@PathVariable Long id, @RequestBody Issue issue) {
        return ResponseEntity.ok(issueService.updateIssue(id, issue));
    }

    // TODO: not found check
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
