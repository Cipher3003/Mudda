package com.mudda.backend.issue;

import java.util.List;
import java.util.Optional;

public interface VoteService {

    //    TODO: remove this method does not provide any value
    List<Vote> findAllVotes();

    long countVotesForIssue(Long issueId);

    List<VoteSummaryResponse> countVotesForIssues(VoteCountRequest countRequest);

    //    TODO: remove this method does not provide any value
    Optional<Vote> findVoteById(Long id);

    boolean hasUserVotedOnIssue(Long issueId, Long userId);

    VoteResponse castVote(Long issueId, Long userId);

    //    TODO: remove this method does not provide any value
    void delete(Long id);

    //    TODO: remove if not used
    void deleteAllVotesByUserId(Long userId);

    void deleteAllVotesByIssueId(Long issueId);

    void deleteAllVotesByIssueIdAndUserId(Long issueId, Long userId);
}
