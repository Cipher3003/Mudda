package com.mudda.backend.postgres.services.impl;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Vote;
import com.mudda.backend.postgres.repositories.VoteRepository;
import com.mudda.backend.postgres.services.VoteService;

public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    public VoteServiceImpl(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    public List<Vote> findAllVotes() {
        return voteRepository.findAll();
    }

    @Override
    public Optional<Vote> findById(Long id) {
        return voteRepository.findById(id);
    }

    @Override
    public Vote create(Vote vote) {
        return voteRepository.save(vote);
    }

    @Override
    public void delete(Long id) {
        voteRepository.deleteById(id);
    }

    @Override
    public void deleteAllVotesByUserId(Long userId) {
        List<Vote> votes = voteRepository.findByUserId(userId);
        for (Vote vote : votes) {
            voteRepository.deleteById(vote.getId());
        }
    }

    @Override
    public void deleteAllVotesByIssueId(Long issueId) {
        List<Vote> votes = voteRepository.findByIssueId(issueId);
        for (Vote vote : votes) {
            voteRepository.deleteById(vote.getId());
        }
    }

}
