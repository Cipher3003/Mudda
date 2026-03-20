package com.mudda.backend.community;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.mudda.backend.user.MuddaUser;

/**
 * SpEL-accessible authorization helper for community-scoped {@code @PreAuthorize} checks.
 * <p>
 * Usage in service layer:
 * <pre>
 * {@literal @}PreAuthorize("@communityAuthz.isVerifiedResident(#communityId, authentication)")
 * public Page<IssueResponse> getCommunityFeed(Long communityId, Pageable pageable) { ... }
 * </pre>
 */
@Component("communityAuthz")
public class CommunityAuthorizationService {

    private final CommunityMemberRepository memberRepository;

    public CommunityAuthorizationService(CommunityMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Returns true if the authenticated user is a VERIFIED member of the given community
     * (any role: RESIDENT, MODERATOR, or ADMIN).
     */
    public boolean isVerifiedResident(Long communityId, Authentication authentication) {
        Long userId = extractUserId(authentication);
        if (userId == null) return false;

        return memberRepository.findByUserIdAndCommunityId(userId, communityId)
                .map(CommunityMember::isVerified)
                .orElse(false);
    }

    /**
     * Returns true if the authenticated user holds the ADMIN role in the given community.
     */
    public boolean isAdmin(Long communityId, Authentication authentication) {
        Long userId = extractUserId(authentication);
        if (userId == null) return false;

        return memberRepository.findByUserIdAndCommunityId(userId, communityId)
                .filter(CommunityMember::isVerified)
                .map(CommunityMember::isAdmin)
                .orElse(false);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof MuddaUser muddaUser) {
            return muddaUser.getUserId();
        }
        return null;
    }
}
