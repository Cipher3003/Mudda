package com.mudda.backend.comment;

import com.mudda.backend.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping("/issues/{issueId}/comments")
    public ResponseEntity<Page<CommentDetailResponse>> getCommentsByIssue(
            @PathVariable long issueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Getting comments by issue id {}", issueId);

        return ResponseEntity.ok(commentService.findCommentsWithLikes(issueId, pageable, userId));
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDetailResponse> getCommentsById(@PathVariable long commentId) {
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Getting comments by id {}", commentId);

        return commentService.findById(commentId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<Page<ReplyResponse>> getAllRepliesByComment(
            @PathVariable long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Getting replies by comment id {}", commentId);

        return ResponseEntity.ok(commentService.findAllReplies(commentId, pageable, userId));
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @Operation(description = "Creates comments under an issue")
    @PostMapping("/issues/{issueId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable long issueId,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Creating comment for issue with id {} and request {}", issueId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(issueId, userId, request));
    }

    @Operation(description = "Creates replies under a comment")
    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentResponse> createReply(@PathVariable long commentId,
                                                       @Valid @RequestBody CreateCommentRequest request) {
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Creating reply for comment with id {} and request {}", commentId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createReply(commentId, userId, request));
    }

    @Operation(description = "Updates both comments and replies by their commentId")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable long commentId,
                                                         @Valid @NotBlank @RequestBody String text) {
        log.debug("Updating comment with id {} and text {}", commentId, text);
        return ResponseEntity.ok(commentService.updateComment(commentId, text));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> likeComment(@PathVariable long commentId) {
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Liking comment with id {} by user {}", commentId, userId);

        return ResponseEntity.ok(commentService.likeComment(commentId, userId));
    }

    @Operation(description = "Deletes both comments and replies by their commentId")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable long commentId) {
        log.debug("Deleting comment with id {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> removeLikeFromComment(@PathVariable long commentId) {
        Long userId = SecurityUtil.getUserIdOrNull();

        log.debug("Removing like on comment with id {} by user {}", commentId, userId);

        return ResponseEntity.ok(commentService.deleteLikeComment(commentId, userId));
    }

    // endregion
}
