package com.mudda.backend.community.announcement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<CommunityAnnouncement, Long> {

    Page<CommunityAnnouncement> findByCommunityIdOrderByCreatedAtDesc(Long communityId, Pageable pageable);
}
