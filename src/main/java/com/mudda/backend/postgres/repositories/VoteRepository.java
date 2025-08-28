package com.mudda.backend.postgres.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mudda.backend.postgres.models.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // TODO: maybe remove this
    Optional<Vote> findByIssueIdAndUserId(Long userId, Long issueId);

    List<Vote> findByIssueId(Long issueId);

    List<Vote> findByUserId(Long userId);
}
