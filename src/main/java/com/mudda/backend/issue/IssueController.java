package com.mudda.backend.issue;

import com.mudda.backend.issue.dto.*;
import com.mudda.backend.user.MuddaUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/issues")
public class IssueController {
    // TODO: find issues by location feature

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @Operation(summary = "List issues with filters, sorting and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of issues"),
            @ApiResponse(responseCode = "400", description = "Invalid Input")
    })
    @GetMapping
    public ResponseEntity<Page<IssueResponse>> getAllIssues(
            @ModelAttribute(name = "filters") IssueFilterRequest filterRequest,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "CREATED_AT") IssueSortBy sort,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        Pageable pageable = PageRequest.of(
                page, size,
                "desc".equalsIgnoreCase(direction)
                        ? Sort.by(sort.getFieldName()).descending()
                        : Sort.by(sort.getFieldName()).ascending());

        Long userId = principal != null ? principal.getUserId() : null;
        log.debug("Finding issues with filters: {}", filterRequest);

        return ResponseEntity.ok(issueService.findAllIssues(filterRequest, pageable, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(
            @PathVariable long id,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        Long userId = principal != null ? principal.getUserId() : null;

        return ResponseEntity.ok(issueService.findById(id, userId));
    }

    @GetMapping("/clusters")
    public ResponseEntity<IssueClusterResponse> getIssueClusters(
            @Valid @ModelAttribute IssueClusterRequest clusterRequest
    ) {
        log.debug("Finding clusters: {}", clusterRequest);
        return ResponseEntity.ok(issueService.findAllIssueClusters(clusterRequest));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Page<IssueDashboardResponse>> getIssueForDashboard(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(issueService.findAllIssuesDashboard(pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<IssueCategory>> getIssueCategories() {
        return ResponseEntity.ok().body(Arrays.stream(IssueCategory.values()).toList());
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(
            @Valid @RequestBody CreateIssueRequest issueRequest,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        log.info("Creating new issue by user with id {}", principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.createIssue(principal.getUserId(), issueRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueUpdateResponse> updateIssue(
            @PathVariable long id,
            @Valid @RequestBody UpdateIssueRequest issueRequest,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        log.info("Updating issue with id {} by user with id {}", id, principal.getUserId());
        return ResponseEntity.ok(issueService.updateIssue(id, principal.getUserId(), issueRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable long id, @AuthenticationPrincipal MuddaUser principal) {
        log.info("Deleting issue with id {} by user with id {}", id, principal.getUserId());
        issueService.deleteIssue(id, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    // endregion
}
