package com.mudda.backend.mongodb.controllers;

import com.mudda.backend.mongodb.models.Comment;
import com.mudda.backend.mongodb.services.CommentService;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    public ResponseEntity<List<Comment>> getAllIssues() {
        return ResponseEntity.ok(commentService.findAllComments());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Comment> getCommentsById(@PathVariable String id){
        return commentService.findById(new ObjectId(id)).
                map(ResponseEntity::ok).
                orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<Optional<Comment>> getCommentsByIssue(@PathVariable String issueId){
        return ResponseEntity.ok(commentService.findByIssue(new ObjectId(issueId)));
    }
}
