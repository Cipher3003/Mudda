package com.mudda.backend.community.announcement;

import com.mudda.backend.community.announcement.dto.AnnouncementResponse;
import com.mudda.backend.community.announcement.dto.CreateAnnouncementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(Long communityId, Long authorId, CreateAnnouncementRequest request);

    Page<AnnouncementResponse> getAnnouncements(Long communityId, Pageable pageable);
}
