package com.mudda.backend.community.announcement;

import com.mudda.backend.community.announcement.dto.AnnouncementResponse;
import com.mudda.backend.user.MuddaUser;

/**
 * Mapper between CommunityAnnouncement entity and its DTO.
 */
public class AnnouncementMapper {

    private AnnouncementMapper() {}

    public static AnnouncementResponse toResponse(CommunityAnnouncement announcement, MuddaUser author) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getCommunityId(),
                announcement.getAuthorId(),
                author != null ? author.getUsername() : "Unknown",
                announcement.getTitle(),
                announcement.getBody(),
                announcement.getCreatedAt()
        );
    }
}
