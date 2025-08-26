package com.mudda.backend.postgres.controllers;

import com.mudda.backend.postgres.models.Comment;
import com.mudda.backend.postgres.models.Reply;
import com.mudda.backend.postgres.services.CommentService;

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
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllIssues() {
        return ResponseEntity.ok(commentService.findAllComments());
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<List<Comment>> getCommentsByIssue(@PathVariable Long issueId) {
        return ResponseEntity.ok(commentService.findByIssueId(issueId));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Reply>> getAllReplies(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findAllReplies(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentsById(@PathVariable Long id) {
        return commentService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: Validate input
    @PostMapping
    public ResponseEntity<Comment> create(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.createComment(comment));
    }

    // TODO: Validate input
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody String text) {
        return ResponseEntity.ok(commentService.updateComment(id, text));
    }

    // TODO: not found check
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

}
