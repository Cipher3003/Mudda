package com.mudda.backend.vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VoteService {

    //    TODO: remove this method does not provide any value
    Page<Vote> findAllVotes(Pageable pageable);

    //    TODO: remove this method does not provide any value
    Optional<Vote> findVoteById(long id);

    VoteResponse create(long issueId, long userId);

    //    TODO: remove this method does not provide any value
    void delete(long id);

    void deleteAllVotesByIssueId(long issueId);

    void deleteAllVotesByUserId(long id);

    void deleteVoteByIssueIdAndUserId(long issueId, long userId);

    void deleteAllVotesByIssueIds(List<Long> issueIds);

}
