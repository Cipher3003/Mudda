package com.mudda.backend.vote;

import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.utils.EntityValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final IssueRepository issueRepository;

    public VoteServiceImpl(VoteRepository voteRepository,
            IssueRepository issueRepository) {
        this.voteRepository = voteRepository;
        this.issueRepository = issueRepository;
    }

    // region Queries (Read Operations)

    @Override
    public Page<Vote> findAllVotes(Pageable pageable) {
        return voteRepository.findAll(pageable);
    }

    @Override
    public Optional<Vote> findVoteById(long id) {
        return voteRepository.findById(id);
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    @Override
    public VoteResponse create(long issueId, Long userId) {

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        validateReferences(issueId);

        Vote vote = Vote.castVote(issueId, userId);
        voteRepository.save(vote);

        long voteCount = voteRepository.countByIssueId(issueId);

        return VoteResponse.from(voteCount, true);
    }

    @Override
    public void saveVotes(List<Vote> votes) {
        voteRepository.saveAll(votes);
    }

    @Transactional
    @Override
    public void deleteVote(long id) {
        voteRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteAllVotesByIssueId(long issueId) {
        voteRepository.deleteByIssueId(issueId);
    }

    @Transactional
    @Override
    public void deleteAllVotesByUserId(long userId) {
        voteRepository.deleteByUserId(userId);
    }

    @Transactional
    @Override
    public VoteResponse deleteVoteByIssueIdAndUserId(long issueId, Long userId) {

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        validateReferences(issueId);

        voteRepository.deleteByIssueIdAndUserId(issueId, userId);

        long voteCount = voteRepository.countByIssueId(issueId);
        return VoteResponse.from(voteCount, false);
    }

    @Transactional
    @Override
    public void deleteAllVotesByIssueIds(List<Long> issueIds) {
        voteRepository.deleteAllByIssueIdIn(issueIds);
    }

    // endregion

    private void validateReferences(long issueId) {
        EntityValidator.validateExists(issueRepository, issueId, "Issue");
    }

}
