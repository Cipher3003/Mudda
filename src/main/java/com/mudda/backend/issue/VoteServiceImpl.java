package com.mudda.backend.issue;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;

    public VoteServiceImpl(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    //    TODO: maybe remove this
    @Override
    public List<Vote> findAllVotes() {
        return voteRepository.findAll();
    }

    @Override
    public List<VoteSummaryResponse> countVotesForIssues(VoteCountRequest countRequest) {
        if (countRequest.issueIds() == null || countRequest.issueIds().isEmpty())
            throw new IllegalArgumentException("Issue Ids list cannot be null or empty");
        return voteRepository.countVotesByIssueIds(countRequest.issueIds());
    }

    @Override
    public long countVotesForIssue(Long issueId) {
        return voteRepository.countByIssueId(issueId);
    }

    @Override
    public Optional<Vote> findVoteById(Long id) {
        return voteRepository.findById(id);
    }

    @Override
    public boolean hasUserVotedOnIssue(Long issueId, Long userId) {
        return voteRepository.existsByIssueIdAndUserId(issueId, userId);
    }

    //    TODO: add proper exception handling
    @Override
    public VoteResponse castVote(Long issueId, Long userId) {
        if (voteRepository.existsByIssueIdAndUserId(issueId, userId))
            throw new RuntimeException("User " + userId + " has already voted on issue " + issueId);

        Vote vote = Vote.castVote(issueId, userId);
        Vote saved = voteRepository.save(vote);
        return VoteResponse.from(saved);
    }

    @Override
    public void delete(Long id) {
        voteRepository.deleteById(id);
    }

    @Override
    public void deleteAllVotesByUserId(Long userId) {
        List<Vote> votes = voteRepository.findByUserId(userId);
        voteRepository.deleteAll(votes);
    }

    @Override
    public void deleteAllVotesByIssueId(Long issueId) {
        List<Vote> votes = voteRepository.findByIssueId(issueId);
        voteRepository.deleteAll(votes);
    }

    @Override
    public void deleteAllVotesByIssueIdAndUserId(Long issueId, Long userId) {
        voteRepository.deleteAllByIssueIdAndUserId(issueId, userId);
    }

}
