package com.mudda.backend.issue;

import com.mudda.backend.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/issues")
public class IssueController {
    // TODO: find issues by location feature

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    // ----------- PUBLIC READ -----------------
    // #region Queries (Read Operations)

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
                        : Sort.by(sort.getFieldName()).ascending());

        Long userId = SecurityUtil.getUserIdOrNull();

        return ResponseEntity.ok(issueService.findAllIssues(filterRequest, pageable, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable(name = "id") long id) {
        Long userId = SecurityUtil.getUserIdOrNull();

        return issueService
                .findById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clusters")
    public ResponseEntity<IssueClusterResponse> getIssueClusters(
            @Valid @ModelAttribute IssueClusterRequest clusterRequest) {

        return ResponseEntity.ok(issueService.findAllIssueClusters(clusterRequest));
    }

    // #endregion

    // ----------- AUTH COMMANDS -----------------
    // #region Commands (Write Operations)

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody CreateIssueRequest issueRequest) {
        Long userId = SecurityUtil.getUserIdOrNull();

        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.createIssue(userId, issueRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueUpdateResponse> updateIssue(
            @PathVariable(name = "id") long id,
            @Valid @RequestBody UpdateIssueRequest issueRequest) {
        Long userId = SecurityUtil.getUserIdOrNull();

        return ResponseEntity.ok(issueService.updateIssue(id, userId, issueRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable(name = "id") long id) {
        Long userId = SecurityUtil.getUserIdOrNull();

        issueService.deleteIssue(id, userId);
        return ResponseEntity.noContent().build();
    }

    // #endregion
}
