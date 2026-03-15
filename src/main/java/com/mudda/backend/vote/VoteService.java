package com.mudda.backend.vote;

import com.mudda.backend.issue.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final IssueRepository issueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public VoteService(
            VoteRepository voteRepository,
            IssueRepository issueRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.voteRepository = voteRepository;
        this.issueRepository = issueRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // region Queries (Read Operations)

    public Page<Vote> findAllVotes(Pageable pageable) {
        return voteRepository.findAll(pageable);
    }

    public Optional<Vote> findVoteById(long id) {
        return voteRepository.findById(id);
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    public VoteResponse create(long issueId, Long userId) {
        Long voteCount = issueRepository.getCountById(issueId).orElseThrow(
                () -> new EntityNotFoundException("Issue not found with id: %s".formatted(issueId))
        );

        voteRepository.save(Vote.castVote(issueId, userId));
        applicationEventPublisher.publishEvent(new VoteCratedEvent(issueId));

        return VoteResponse.from(voteCount + 1, true);
    }

    public void saveVotes(List<Vote> votes) {
        voteRepository.saveAll(votes);
    }

    @Transactional
    public void deleteVote(long id) {
        voteRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllVotesByIssueId(long issueId) {
        voteRepository.deleteByIssueId(issueId);
    }

    @Transactional
    public void deleteAllVotesByUserId(long userId) {
        voteRepository.deleteByUserId(userId);
    }

    @Transactional
    public VoteResponse deleteVoteByIssueIdAndUserId(long issueId, Long userId) {
        Long voteCount = issueRepository.getCountById(issueId).orElseThrow(
                () -> new EntityNotFoundException("Issue not found with id: %s".formatted(issueId))
        );

        voteRepository.deleteByIssueIdAndUserId(issueId, userId);
        applicationEventPublisher.publishEvent(new VoteRemovedEvent(issueId));

        return VoteResponse.from(voteCount - 1, false);
    }

    @Transactional
    public void deleteAllVotesByIssueIds(List<Long> issueIds) {
        voteRepository.deleteAllByIssueIdIn(issueIds);
    }

    // endregion

}
