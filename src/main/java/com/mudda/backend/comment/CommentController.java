package com.mudda.backend.comment;

import com.mudda.backend.comment.dto.*;
import com.mudda.backend.user.MuddaUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    public CommentController(CommentService commentService, CommentLikeService commentLikeService) {
        this.commentService = commentService;
        this.commentLikeService = commentLikeService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping("/issues/{issueId}/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentsByIssue(
            @PathVariable long issueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = principal != null ? principal.getUserId() : null;

        log.debug("Getting comments by issue id {}", issueId);

        return ResponseEntity.ok(commentService.findCommentsWithLikes(issueId, pageable, userId));
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> getCommentsById(
            @PathVariable long commentId,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        Long userId = principal != null ? principal.getUserId() : null;

        log.debug("Getting comments by id {}", commentId);

        return commentService.findById(commentId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<Page<CommentResponse>> getAllRepliesByComment(
            @PathVariable long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = principal != null ? principal.getUserId() : null;

        log.debug("Getting replies by comment id {}", commentId);

        return ResponseEntity.ok(commentService.findAllReplies(commentId, pageable, userId));
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @Operation(description = "Creates comments/replies under an issue depending on parentId")
    @PostMapping("/comments")
    public ResponseEntity<CommentCreatedResponse> createComment(
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        log.debug("Creating comment with request {}", request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(principal.getUserId(), request));
    }

    @Operation(description = "Updates both comments and replies by their id")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateComment(
            @PathVariable long commentId,
            @Valid @NotBlank @RequestBody String text
    ) {
        log.debug("Updating comment with id {} and text {}", commentId, text);
        return ResponseEntity.ok(commentService.updateComment(commentId, text));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> likeComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        log.debug("Liking comment with id {} by user {}", commentId, principal.getUserId());

        return ResponseEntity.ok(commentLikeService.likeOnComment(commentId, principal.getUserId()));
    }

    @Operation(description = "Deletes both comments and replies by their id")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable long commentId) {
        log.debug("Deleting comment with id {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> removeLikeFromComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal MuddaUser principal
    ) {
        log.debug("Removing like on comment with id {} by user {}", commentId, principal.getUserId());

        return ResponseEntity.ok(commentLikeService.deleteLikeFromComment(commentId, principal.getUserId()));
    }

    // endregion
}
