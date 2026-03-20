package com.mudda.backend.community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    Optional<CommunityMember> findByUserIdAndCommunityId(Long userId, Long communityId);

    boolean existsByUserIdAndCommunityId(Long userId, Long communityId);

    /**
     * All verified residents of a community (for notification dispatch, headcounts, etc.).
     */
    List<CommunityMember> findByCommunityIdAndStatus(Long communityId, MemberStatus status);

    /**
     * Paginated member list for the admin dashboard.
     */
    Page<CommunityMember> findByCommunityId(Long communityId, Pageable pageable);

    long countByCommunityIdAndStatus(Long communityId, MemberStatus status);
}
