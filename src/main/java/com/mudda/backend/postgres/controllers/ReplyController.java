package com.mudda.backend.postgres.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mudda.backend.postgres.models.Reply;
import com.mudda.backend.postgres.services.ReplyService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/replies")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping
    public ResponseEntity<List<Reply>> getAll() {
        return ResponseEntity.ok(replyService.findAllReplies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reply> getById(@RequestParam Long id) {
        return replyService.findReplyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/text/{text}")
    public ResponseEntity<List<Reply>> getAllContainingText(@RequestParam String text) {
        return ResponseEntity.ok(replyService.findReplyContainingText(text));
    }

    @GetMapping("/comment/{comment_id}")
    public ResponseEntity<List<Reply>> getAllByCommentId(@RequestParam Long id) {
        return ResponseEntity.ok(replyService.findAllReplyByCommentId(id));
    }

    @PostMapping
    public ResponseEntity<Reply> create(@RequestBody Reply reply) {
        return ResponseEntity.ok(replyService.createReply(reply));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reply> putReply(@PathVariable Long id, @RequestBody String reply) {
        return ResponseEntity.ok(replyService.update(id, reply));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }

}
