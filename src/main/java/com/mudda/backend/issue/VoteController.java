package com.mudda.backend.issue;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/votes")
    public ResponseEntity<List<Vote>> getAllVotes() {
        return ResponseEntity.ok(voteService.findAllVotes());
    }

    @GetMapping("/issues/votes")
    public ResponseEntity<List<VoteSummaryResponse>> getAllIssuesVoteCount(@Valid @RequestParam VoteCountRequest countRequest) {
        return ResponseEntity.ok(voteService.countVotesForIssues(countRequest));
    }

    @GetMapping("/issues/{issueId}/votes/{userId}")
    public ResponseEntity<Boolean> checkVoteOnIssueIdByUserId(@PathVariable(name = "issueId") Long issueId,
                                                              @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(voteService.hasUserVotedOnIssue(issueId, userId));
    }

    @GetMapping("/issues/{issueId}/votes")
    public ResponseEntity<Long> getVoteCountByIssueId(@PathVariable(name = "issueId") Long issueId) {
        return ResponseEntity.ok(voteService.countVotesForIssue(issueId));
    }

    @GetMapping("/votes/{voteId}")
    public ResponseEntity<Vote> getVotesById(@PathVariable(name = "id") Long voteId) {
        return voteService.findVoteById(voteId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/issues/{issueId}/votes/{userId}")
    public ResponseEntity<VoteResponse> create(@PathVariable(name = "issueId") Long issueId,
                                               @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(voteService.castVote(issueId, userId));
    }

    @DeleteMapping("/votes/{voteId}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long voteId) {
        voteService.delete(voteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/issues/{issueId}/votes/{userId}")
    public ResponseEntity<Void> deleteVoteOnIssueIdByUserId(@PathVariable(name = "issueId") Long issueId,
                                                            @PathVariable(name = "userId") Long userId) {
        voteService.deleteAllVotesByIssueIdAndUserId(issueId, userId);
        return ResponseEntity.noContent().build();
    }

}
