package com.mudda.backend.community;

import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.CommunityResponse;
import com.mudda.backend.user.MuddaUser;

/**
 * Mapper between Community/CommunityMember domain entities and their DTOs.
 */
public class CommunityMapper {

    private CommunityMapper() {}

    public static CommunityResponse toResponse(Community community, long residentCount) {
        return new CommunityResponse(
                community.getId(),
                community.getName(),
                community.getDescription(),
                community.getStatus(),
                community.getOwnerId(),
                residentCount,
                community.getCreatedAt()
        );
    }

    public static CommunityMemberResponse toMemberResponse(CommunityMember member, MuddaUser user) {
        return new CommunityMemberResponse(
                member.getId(),
                member.getUserId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                member.getRole(),
                member.getStatus(),
                member.getJoinedAt()
        );
    }
}
