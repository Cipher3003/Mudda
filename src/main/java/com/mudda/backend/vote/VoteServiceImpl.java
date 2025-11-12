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
    public Optional<Vote> findVoteById(Long id) {
        return voteRepository.findById(id);
    }

    // #endregion

    // #region Commands (Write Operations)

    @Override
    public VoteResponse create(Long issueId, Long userId) {
        validateReferences(issueId, userId);

        Vote vote = Vote.castVote(issueId, userId);
        Vote saved = voteRepository.save(vote);
        return VoteResponse.from(saved);
    }

    @Override
    public void delete(Long id) {
        voteRepository.deleteById(id);
    }


    @Override
    public void deleteAllVotesByIssueId(Long issueId) {
        List<Vote> votes = voteRepository.findByIssueId(issueId);
        voteRepository.deleteAll(votes);
    }

    @Override
    public void deleteVoteByIssueIdAndUserId(Long issueId, Long userId) {
        voteRepository.deleteAllByIssueIdAndUserId(issueId, userId);
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
