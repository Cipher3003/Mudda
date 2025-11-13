package com.mudda.backend.vote;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.user.UserRepository;
import com.mudda.backend.utils.EntityValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public VoteServiceImpl(VoteRepository voteRepository,
                           IssueRepository issueRepository,
                           UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
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
    public VoteResponse create(long issueId, long userId) {
        validateReferences(issueId, userId);

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
    public void deleteVoteByIssueIdAndUserId(long issueId, long userId) {
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

    private void validateReferences(long issueId, long userId) {

        EntityValidator.validateExists(issueRepository, issueId, "Issue");
        EntityValidator.validateExists(userRepository, userId, "User");
    }

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Issue not found with id: %d".formatted(id));
    }

}
