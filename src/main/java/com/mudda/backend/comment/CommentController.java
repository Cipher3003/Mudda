package com.mudda.backend.comment;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeRepository commentLikeRepository;

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getAllIssues() {
        return ResponseEntity.ok(commentService.findAllComments().stream()
                .map(comment -> {
                    Long likeCount = commentLikeRepository.countByCommentId(comment.getCommentId());
                    return CommentMapper.toCommentResponse(comment, likeCount);
                }).toList());
    }

    @GetMapping("/issues/{issueId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByIssue(@PathVariable(name = "issueId") Long issueId) {
        return ResponseEntity.ok(commentService.findCommentsByIssueId(issueId).stream()
                .map(comment -> {
                    Long likeCount = commentLikeRepository.countByCommentId(comment.getCommentId());
                    return CommentMapper.toCommentResponse(comment, likeCount);
                }).toList());
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> getCommentsById(@PathVariable(name = "commentId") Long commentId) {
        return commentService.findById(commentId).map(comment -> {
                    Long likeCount = commentLikeRepository.countByCommentId(comment.getCommentId());
                    return ResponseEntity.ok(CommentMapper.toCommentResponse(comment, likeCount));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<ReplyResponse>> getAllRepliesByComment(@PathVariable(name = "commentId") Long commentId) {
        return ResponseEntity.ok(commentService.findAllReplies(commentId).stream()
                .map(reply -> {
                    Long likeCount = commentLikeRepository.countByCommentId(reply.getCommentId());
                    return CommentMapper.toReplyResponse(reply, likeCount);
                }).toList());
    }

    // TODO: Validate input
    @Operation(description = "Creates both comments and replies based on request")
    @PostMapping("/comments")
    public ResponseEntity<Comment> create(@Valid @RequestBody CreateCommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.createComment(CommentMapper.toComment(commentRequest)));
    }

    // TODO: Validate input
    @Operation(description = "Updates both comments and replies by their commentId")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable(name = "commentId") Long commentId,
                                                 @RequestBody String text) {
        return ResponseEntity.ok(commentService.updateComment(commentId, text));
    }

    // TODO: not found check
    @Operation(description = "Deletes both comments and replies by their commentId")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}
