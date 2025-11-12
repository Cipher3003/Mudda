package com.mudda.backend.comment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

// TODO: Use session or JWT for userId context when implementing security

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // #region Queries (Read Operations)

    @GetMapping("/issues/{issueId}/comments/{userId}")
    public ResponseEntity<Page<CommentDetailResponse>> getCommentsByIssue(
            @PathVariable long issueId,
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.findCommentsWithLikes(issueId, pageable, userId));
    }

    @GetMapping("/comments/{commentId}/{userId}")
    public ResponseEntity<CommentDetailResponse> getCommentsById(@PathVariable long commentId,
                                                                 @PathVariable long userId) {
        return commentService.findById(commentId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comments/{commentId}/replies/{userId}")
    public ResponseEntity<Page<ReplyResponse>> getAllRepliesByComment(
            @PathVariable long commentId,
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.findAllReplies(commentId, pageable, userId));
    }

    // #endregion

    // #region Commands (Write Operations)

    @Operation(description = "Creates comments under an issue")
    @PostMapping("/issues/{issueId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable long issueId,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(issueId, request));
    }

    @Operation(description = "Creates replies under a comment")
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentResponse> createReply(@PathVariable long commentId,
                                                       @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createReply(commentId, request));
    }

    @Operation(description = "Updates both comments and replies by their commentId")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable long commentId,
                                                         @Valid @NotBlank @RequestBody String text) {
        return ResponseEntity.ok(commentService.updateComment(commentId, text));
    }

    @PostMapping("/comments/{commentId}/like/{userId}")
    public ResponseEntity<CommentLikeResponse> likeComment(@PathVariable long commentId, @PathVariable long userId) {
        return ResponseEntity.ok(commentService.likeComment(commentId, userId));
    }

    @Operation(description = "Deletes both comments and replies by their commentId")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}/like/{userId}")
    public ResponseEntity<CommentLikeResponse> removeLikeFromComment(@PathVariable long commentId,
                                                                     @PathVariable long userId) {
        return ResponseEntity.ok(commentService.deleteLikeComment(commentId, userId));
    }

    // #endregion
}
