package com.mudda.backend.community;

import com.mudda.backend.community.announcement.AnnouncementService;
import com.mudda.backend.community.announcement.dto.AnnouncementResponse;
import com.mudda.backend.community.announcement.dto.CreateAnnouncementRequest;
import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.DashboardStatsResponse;
import com.mudda.backend.community.dto.UpdateIssueStatusRequest;
import com.mudda.backend.initiative.InitiativeService;
import com.mudda.backend.initiative.dto.CreateInitiativeRequest;
import com.mudda.backend.initiative.dto.InitiativeResponse;
import com.mudda.backend.issue.IssueResponse;
import com.mudda.backend.security.SecurityUtil;
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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/communities")
public class AdminCommunityController {

    private final AdminCommunityService adminService;
    private final InitiativeService initiativeService;
    private final AnnouncementService announcementService;

    public AdminCommunityController(AdminCommunityService adminService,
                                    InitiativeService initiativeService,
                                    AnnouncementService announcementService) {
        this.adminService = adminService;
        this.initiativeService = initiativeService;
        this.announcementService = announcementService;
    }

    // ----------- ADMIN READ -----------------
    // region Queries

    @Operation(summary = "Admin dashboard — aggregate community stats")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard stats returned"),
            @ApiResponse(responseCode = "403", description = "Not a community admin"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboard(@PathVariable(name = "id") Long id) {
        log.debug("Admin fetching dashboard for community {}", id);
        return ResponseEntity.ok(adminService.getDashboard(id));
    }

    @Operation(summary = "Admin — paginated issues filed in this community")
    @GetMapping("/{id}/issues")
    public ResponseEntity<Page<IssueResponse>> getCommunityIssues(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("desc")
                        ? Sort.by("createdAt").descending()
                        : Sort.by("createdAt").ascending());

        return ResponseEntity.ok(adminService.getCommunityIssues(id, pageable));
    }

    @Operation(summary = "Admin — paginated announcements for this community")
    @GetMapping("/{id}/announcements")
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncements(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(announcementService.getAnnouncements(id, pageable));
    }

    // endregion

    // ----------- ADMIN COMMANDS -----------------
    // region Commands

    @Operation(summary = "Admin — update issue status and optionally set official response")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Issue status updated"),
            @ApiResponse(responseCode = "404", description = "Issue not found")
    })
    @PatchMapping("/issues/{issueId}/status")
    public ResponseEntity<Void> updateIssueStatus(
            @PathVariable(name = "issueId") Long issueId,
            @Valid @RequestBody UpdateIssueStatusRequest request) {

        Long userId = SecurityUtil.getUserIdOrNull();
        log.info("Admin {} updating issue {} status to {}", userId, issueId, request.status());
        adminService.updateIssueStatus(issueId, userId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Admin — create a new community initiative")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Initiative created"),
            @ApiResponse(responseCode = "403", description = "Not a community admin")
    })
    @PostMapping("/{id}/initiatives")
    public ResponseEntity<InitiativeResponse> createInitiative(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody CreateInitiativeRequest request) {

        log.info("Admin creating initiative '{}' in community {}", request.title(), id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(initiativeService.createInitiative(id, request));
    }

    @Operation(summary = "Admin — broadcast a new announcement (triggers FCM to all verified residents)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Announcement created and broadcast"),
            @ApiResponse(responseCode = "403", description = "Not a community admin")
    })
    @PostMapping("/{id}/announcements")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody CreateAnnouncementRequest request) {

        Long userId = SecurityUtil.getUserIdOrNull();
        log.info("Admin {} creating announcement '{}' in community {}", userId, request.title(), id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(announcementService.createAnnouncement(id, userId, request));
    }

    @Operation(summary = "Admin — accept or reject a pending member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member status updated"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @PatchMapping("/{id}/members/{memberId}/verify")
    public ResponseEntity<CommunityMemberResponse> verifyMember(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "memberId") Long memberId,
            @RequestParam(name = "accept", defaultValue = "true") boolean accept) {

        log.info("Admin {} member {} in community {}", accept ? "accepting" : "rejecting", memberId, id);
        return ResponseEntity.ok(adminService.verifyMember(id, memberId, accept));
    }

    // endregion
}

