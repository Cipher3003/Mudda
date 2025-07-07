package com.mudda.backend.mongodb.controllers;

import com.mudda.backend.mongodb.models.Issue;
import com.mudda.backend.mongodb.models.IssueStatus;
import com.mudda.backend.mongodb.services.IssueService;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue) {
        Issue created = issueService.createIssue(issue);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Issue>> getAllIssues() {
        return ResponseEntity.ok(issueService.findAllIssues());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Issue> getIssueById(@PathVariable String id) {
        return issueService.findIssueById(new ObjectId(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ResponseEntity::ok is a method reference to ResponseEntity.ok()
    // The ok() method takes the Issue object (which is the value inside the Optional) as an argument
    // and creates a ResponseEntity with an HTTP status code of 200 OK and the Issue object as its body.

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable String id) {
        issueService.deleteIssue(new ObjectId(id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Issue>> getIssuesByStatus(@PathVariable IssueStatus status) {
        return ResponseEntity.ok(issueService.findByStatus(status));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Issue>> getIssuesByUserName(@PathVariable String userName) {
        return ResponseEntity.ok(issueService.findIssueByUserName(userName));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Issue>> getIssuesByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(issueService.findIssueByCategoryId(categoryId));
    }
// TODO: fix nearby Issues via GeoJSONPoint
//
//    @GetMapping("/nearby")
//    public ResponseEntity<List<Issue>> getNearbyIssues(
//            @RequestParam double lat,
//            @RequestParam double lon,
//            @RequestParam int radiusInKm) {
//
//        Point point = new Point(lon, lat); // longitude first!
//        return ResponseEntity.ok(issueService.findByLocation_CoordinatesNear(point, radiusInKm));
//    }
}
