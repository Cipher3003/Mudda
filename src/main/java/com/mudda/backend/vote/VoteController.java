package com.mudda.backend.vote;

import com.mudda.backend.security.SecurityUtil;
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

    // region Queries (Read Operations)

    //    NOTE: DEVELOPER ONLY
    @GetMapping("/votes")
    public ResponseEntity<Page<Vote>> getAllVotes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voteService.findAllVotes(pageable));
    }

    //    NOTE: DEVELOPER ONLY
    @GetMapping("/votes/{voteId}")
    public ResponseEntity<Vote> getVotesById(@PathVariable long voteId) {
        return voteService.findVoteById(voteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // endregion

    // region Commands (Write Operations)

    @PostMapping("/issues/{issueId}/votes")
    public ResponseEntity<VoteResponse> create(@PathVariable long issueId) {
        Long userId = SecurityUtil.getUserIdOrNull();

        return ResponseEntity.ok(voteService.create(issueId, userId));
    }

    //    NOTE: DEVELOPER ONLY
    @DeleteMapping("/votes/{voteId}")
    public ResponseEntity<Void> delete(@PathVariable long voteId) {
        voteService.deleteVote(voteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/issues/{issueId}/votes")
    public ResponseEntity<VoteResponse> deleteVoteOnIssueIdByUserId(@PathVariable long issueId) {
        Long userId = SecurityUtil.getUserIdOrNull();

        return ResponseEntity.ok(voteService.deleteVoteByIssueIdAndUserId(issueId, userId));
    }

    // endregion
}
