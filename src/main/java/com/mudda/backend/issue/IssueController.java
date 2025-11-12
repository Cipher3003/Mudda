package com.mudda.backend.issue;

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

    // #region Queries (Read Operations)

    @Operation(summary = "List issues with filters, sorting and pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of issues"),
            @ApiResponse(responseCode = "400", description = "Invalid Input")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Page<IssueSummaryResponse>> getAllIssues(
            @ModelAttribute(name = "filters") IssueFilterRequest filterRequest,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "CREATED_AT") IssueSortBy sort,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction,
            @PathVariable long userId
    ) {

        Pageable pageable = PageRequest.of(
                page, size,
                direction.equalsIgnoreCase("desc")
                        ? Sort.by(sort.getFieldName()).descending()
                        : Sort.by(sort.getFieldName()).ascending()
        );

        return ResponseEntity.ok(issueService.findAllIssues(filterRequest, pageable, userId));
    }

    @GetMapping("/{userId}/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable long userId,
                                                      @PathVariable(name = "id") long id) {
        return issueService.findById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // #endregion

    // #region Commands (Write Operations)

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody CreateIssueRequest issueRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.createIssue(issueRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueUpdateResponse> updateIssue(@PathVariable(name = "id") long id,
                                                           @Valid @RequestBody UpdateIssueRequest issueRequest) {
        return ResponseEntity.ok(issueService.updateIssue(id, issueRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable(name = "id") long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }

    // #endregion
}
