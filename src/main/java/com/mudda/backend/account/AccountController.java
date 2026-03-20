/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountController
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.issue.IssueService;
import com.mudda.backend.issue.IssueSortBy;
import com.mudda.backend.issue.dto.IssueSummaryResponse;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/account/me")
public class AccountController {

    private final AccountService accountService;
    private final IssueService issueService;
    private final UserService userService;

    public AccountController(AccountService accountService, IssueService issueService, UserService userService) {
        this.accountService = accountService;
        this.issueService = issueService;
        this.userService = userService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping
    public ResponseEntity<AccountInfo> getCurrentUser(@AuthenticationPrincipal MuddaUser principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok(new AccountInfo(
                principal.getUserId(), principal.getUsername(), principal.getName(),
                principal.getEmail(), principal.getPhoneNumber(), principal.getProfileImageUrl(),
                principal.getRole(), principal.getCreatedAt()
        ));
//        TODO: modify it store user quick info in authentication maybe to avoid database usage in JWT
//        TODO: sync logged in user info with DB changes in session
//        TODO: show not found on profile page for deleted user

    }

    @GetMapping("/issues")
    public ResponseEntity<Page<IssueSummaryResponse>> getUserIssues(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "CREATED_AT") IssueSortBy sort,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Pageable pageable = PageRequest.of(page, size, "desc".equalsIgnoreCase(direction)
                ? Sort.by(sort.getFieldName()).descending()
                : Sort.by(sort.getFieldName()).ascending());

        return ResponseEntity.ok(issueService.findAllIssuesByAuthor(pageable, principal));
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @PatchMapping("/profileImage")
    public ResponseEntity<Void> updateProfileImage(
            @RequestBody UpdateAccountRequest updateRequest,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        userService.updateProfileImage(principal.getUserId(), updateRequest.imageKey());

        return ResponseEntity.noContent().build();
    }

    //    TODO: add update password endpoint

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal MuddaUser principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        log.trace("Received request to delete account {}", principal.getName());

        accountService.deleteAccount(principal.getUserId());

        return ResponseEntity.noContent().build();
    }

    // endregion

}
