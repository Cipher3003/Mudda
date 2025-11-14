package com.mudda.backend.vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VoteService {

    Page<Vote> findAllVotes(Pageable pageable);

    Optional<Vote> findVoteById(long id);

    VoteResponse create(long issueId, Long userId);

    void deleteVote(long id);

    void deleteAllVotesByIssueId(long issueId);

    void deleteAllVotesByUserId(long id);

    VoteResponse deleteVoteByIssueIdAndUserId(long issueId, Long userId);

    void deleteAllVotesByIssueIds(List<Long> issueIds);

}
