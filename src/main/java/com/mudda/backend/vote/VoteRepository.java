package com.mudda.backend.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByIssueId(Long issueId);

    List<Vote> findByUserId(Long userId);

    boolean existsByIssueIdAndUserId(Long issueId, Long userId);

    @Query("SELECT COUNT(v) " +
            "FROM Vote v " +
            "WHERE v.issueId = :issueId")
    long countByIssueId(Long issueId);

    void deleteAllByIssueIdAndUserId(Long issueId, Long userId);
}
