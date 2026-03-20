package com.mudda.backend.vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v.id.issueId FROM Vote v WHERE v.id.issueId IN :issueIds AND v.id.userId = :userId")
    List<Long> findByUserIdAndIssueIdIn(Long userId, List<Long> issueIds);

    boolean existsById(VoteId id);

    @Query("""
            SELECT COUNT(v)
            FROM Vote v
            WHERE v.id.issueId = :issueId
            """)
    long countByIssueId(long issueId);

    @Query("""
            SELECT v.id.issueId, COUNT(v)
            FROM Vote v
            WHERE v.id.issueId IN :issueIds
            GROUP BY v.id.issueId
            """)
    List<Object[]> countByIssueIdIn(@Param("issueIds") List<Long> issueIds);

    //    TODO: fix this error
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.userId = :userId")
    void deleteByUserId(long userId);

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.issueId = :issueId")
    void deleteByIssueId(long issueId);

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.issueId IN :issueIds")
    void deleteAllByIssueIdIn(List<Long> issueIds);

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.id.issueId = :issueId AND v.id.userId = :userId")
    void deleteByIssueIdAndUserId(long issueId, long userId);
}
