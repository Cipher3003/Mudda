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
                           IssueRepository issueRepository
    ) {
        this.voteRepository = voteRepository;
        this.issueRepository = issueRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public Page<Vote> findAllVotes(Pageable pageable) {
        return voteRepository.findAll(pageable);
    }

    @Override
    public Optional<Vote> findVoteById(long id) {
        return voteRepository.findById(id);
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    @Override
    public VoteResponse create(long issueId, Long userId) {

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        validateReferences(issueId);

        Vote vote = Vote.castVote(issueId, userId);
        Vote saved = voteRepository.save(vote);
        return VoteResponse.from(saved);
    }

    @Transactional
    @Override
    public void delete(long id) {
        voteRepository.deleteById(id);
    }


    @Transactional
    @Override
    public void deleteAllVotesByIssueId(long issueId) {
        voteRepository.deleteByIssueId(issueId);
    }

    @Override
    public void deleteAllVotesByUserId(long userId) {
        voteRepository.deleteByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteVoteByIssueIdAndUserId(long issueId, Long userId) {

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        validateReferences(issueId);

        voteRepository.deleteByIssueIdAndUserId(issueId, userId);
    }

    @Transactional
    @Override
    public void deleteAllVotesByIssueIds(List<Long> issueIds) {
        voteRepository.deleteAllByIssueIdIn(issueIds);
    }

    // #endregion

//    ------------------------------
//    Helpers
//    ------------------------------

    private void validateReferences(long issueId) {

        EntityValidator.validateExists(issueRepository, issueId, "Issue");
    }

}
