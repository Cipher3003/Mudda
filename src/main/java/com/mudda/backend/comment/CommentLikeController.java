package com.mudda.backend.comment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments/{commentId}/like/{userId}")
public class CommentLikeController {

    private final CommentLikeService likeService;

    public CommentLikeController(CommentLikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping
    public ResponseEntity<Boolean> hasUserLikedOnComment(@PathVariable(name = "commentId") Long commentId,
                                                         @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(likeService.hasUserLikedOnComment(commentId, userId));
    }

    @PostMapping
    public ResponseEntity<CommentLike> userLikesOnComment(@PathVariable(name = "commentId") Long commentId,
                                                          @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(likeService.userLikesOnComment(commentId, userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeUserLikesFromComment(@PathVariable(name = "commentId") Long commentId,
                                                           @PathVariable(name = "userId") Long userId) {
        likeService.userRemovesLikeFromComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
