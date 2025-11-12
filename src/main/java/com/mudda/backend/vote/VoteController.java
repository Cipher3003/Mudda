package com.mudda.backend.vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // #region Queries (Read Operations)

    //    NOTE: DEVELOPER ONLY
    @GetMapping("/votes")
    public ResponseEntity<Page<Vote>> getAllVotes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voteService.findAllVotes(pageable));
    }

    @GetMapping("/votes/{voteId}")
    public ResponseEntity<Vote> getVotesById(@PathVariable(name = "id") long voteId) {
        return voteService.findVoteById(voteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // #endregion

    // #region Commands (Write Operations)

    @PostMapping("/issues/{issueId}/votes/{userId}")
    public ResponseEntity<VoteResponse> create(@PathVariable(name = "issueId") long issueId,
                                               @PathVariable(name = "userId") long userId) {
        return ResponseEntity.ok(voteService.create(issueId, userId));
    }

    @DeleteMapping("/votes/{voteId}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") long voteId) {
        voteService.delete(voteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/issues/{issueId}/votes/{userId}")
    public ResponseEntity<Void> deleteVoteOnIssueIdByUserId(@PathVariable(name = "issueId") long issueId,
                                                            @PathVariable(name = "userId") long userId) {
        voteService.deleteVoteByIssueIdAndUserId(issueId, userId);
        return ResponseEntity.noContent().build();
    }

    // #endregion
}
