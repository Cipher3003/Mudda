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
import com.mudda.backend.issue.IssueSummaryResponse;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/account")
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

    @GetMapping("/me")
    public ResponseEntity<AccountInfo> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        MuddaUser currentUser = (MuddaUser) authentication.getPrincipal();

        return ResponseEntity.ok(new AccountInfo(
                currentUser.getUserId(), currentUser.getUsername(), currentUser.getName(),
                currentUser.getEmail(), currentUser.getPhoneNumber(), currentUser.getProfileImageUrl(),
                currentUser.getRole(), currentUser.getCreatedAt()
        ));
//        TODO: modify it store user quick info in authentication maybe to avoid database usage in JWT
//        TODO: sync logged in user info with DB changes in session

    }

    @GetMapping("/me/issues")
    public ResponseEntity<Page<IssueSummaryResponse>> getUserIssues(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "CREATED_AT") IssueSortBy sort,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String direction
    ) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        MuddaUser currentUser = (MuddaUser) authentication.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, direction.equalsIgnoreCase("desc")
                ? Sort.by(sort.getFieldName()).descending()
                : Sort.by(sort.getFieldName()).ascending());

        return ResponseEntity.ok(issueService.findAllIssuesByAuthor(pageable, currentUser.getUserId()));
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @PatchMapping("/me/profileImage")
//    TODO: use @AuthenticationPrincipl instead of Authentication to get user directly
    public ResponseEntity<Void> updateProfileImage(Authentication authentication,
                                                   @RequestBody UpdateAccountRequest updateRequest) {
        if (authentication == null || !authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        MuddaUser currentUser = (MuddaUser) authentication.getPrincipal();

        userService.updateProfileImage(currentUser.getUserId(), updateRequest.imageKey());

        return ResponseEntity.noContent().build();
    }

    //    TODO: add update password endpoint

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        log.trace("Received request to delete account {}", authentication.getName());
        Long userId = ((MuddaUser) authentication.getPrincipal()).getUserId();

        accountService.deleteAccount(userId);

        return ResponseEntity.noContent().build();
    }

    // endregion

}
