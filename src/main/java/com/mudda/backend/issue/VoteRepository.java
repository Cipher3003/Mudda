package com.mudda.backend.issue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // TODO: maybe remove this
    Optional<Vote> findByIssueIdAndUserId(Long issueId, Long userId);

    List<Vote> findByIssueId(Long issueId);

    List<Vote> findByUserId(Long userId);

    boolean existsByIssueIdAndUserId(Long issueId, Long userId);

    @Query("SELECT COUNT(v) " +
            "FROM Vote v " +
            "WHERE v.issueId = :issueId")
    long countByIssueId(Long issueId);

    @Query("SELECT new com.mudda.backend.issue.VoteSummaryResponse(v.issueId, COUNT(v)) " +
            "FROM Vote v " +
            "WHERE v.issueId IN :issueIds " +
            "GROUP BY v.issueId")
    List<VoteSummaryResponse> countVotesByIssueIds(@Param("issueIds") List<Long> issueIds);

    void deleteAllByIssueIdAndUserId(Long issueId, Long userId);

    void deleteByIssueId(Long issueId);

    void deleteByUserId(Long userId);
}
