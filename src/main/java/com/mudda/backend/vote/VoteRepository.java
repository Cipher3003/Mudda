package com.mudda.backend.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByUserIdAndIssueIdIn(long userId, List<Long> issueId);

    boolean existsByIssueIdAndUserId(long issueId, long userId);

    @Query("SELECT COUNT(v) " +
            "FROM Vote v " +
            "WHERE v.issueId = :issueId")
    long countByIssueId(long issueId);

    void deleteByUserId(long userId);

    void deleteByIssueId(long issueId);

    void deleteAllByIssueIdIn(List<Long> issueIds);

    void deleteByIssueIdAndUserId(long issueId, long userId);
}
