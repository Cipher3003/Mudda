package com.mudda.backend.community;

import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.CommunityResponse;
import com.mudda.backend.community.dto.CreateCommunityRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommunityService {

    // ---- Resident-facing ----

    List<CommunityResponse> findNearbyCommunities(double lat, double lon, double radiusMeters);

    Optional<CommunityResponse> findById(Long communityId);

    CommunityMemberResponse joinCommunity(Long communityId, Long userId);

    Page<CommunityMemberResponse> getMembers(Long communityId, Pageable pageable);

    // ---- Admin / Owner ----

    CommunityResponse createCommunity(Long ownerId, CreateCommunityRequest request);
}
