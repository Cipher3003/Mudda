package com.mudda.backend.vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface VoteService {

    //    TODO: remove this method does not provide any value
    Page<Vote> findAllVotes(Pageable pageable);

    //    TODO: remove this method does not provide any value
    Optional<Vote> findVoteById(Long id);

    VoteResponse create(Long issueId, Long userId);

    //    TODO: remove this method does not provide any value
    void delete(Long id);

    void deleteAllVotesByIssueId(Long issueId);

    void deleteVoteByIssueIdAndUserId(Long issueId, Long userId);
}
