package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Vote;

public interface VoteService {

    List<Vote> findAllVotes();

    Optional<Vote> findById(Long id);

    Vote create(Vote vote);

    void delete(Long id);

    void deleteAllVotesByUserId(Long userId);

    void deleteAllVotesByIssueId(Long issueId);

}
