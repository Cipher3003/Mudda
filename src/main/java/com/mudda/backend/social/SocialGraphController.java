package com.mudda.backend.social;

import com.mudda.backend.security.SecurityUtil;
import com.mudda.backend.social.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class SocialGraphController {

    private final SocialGraphService socialGraphService;

    public SocialGraphController(SocialGraphService socialGraphService) {
        this.socialGraphService = socialGraphService;
    }

    @Operation(summary = "Follow a user")
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(@PathVariable Long userId) {
        Long myId = SecurityUtil.getUserIdOrNull();
        if (myId == null) throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        log.info("User {} following {}", myId, userId);
        socialGraphService.followUser(myId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unfollow a user")
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
        Long myId = SecurityUtil.getUserIdOrNull();
        if (myId == null) throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        log.info("User {} unfollowing {}", myId, userId);
        socialGraphService.unfollowUser(myId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user's followers")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<UserProfileResponse>> getFollowers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(socialGraphService.getFollowers(userId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get users that this user is following")
    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<UserProfileResponse>> getFollowing(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(socialGraphService.getFollowing(userId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get user profile social stats")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(socialGraphService.getUserProfileStats(userId));
    }
}
