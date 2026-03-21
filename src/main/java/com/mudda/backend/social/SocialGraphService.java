package com.mudda.backend.social;

import com.mudda.backend.social.dto.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SocialGraphService {

    void followUser(Long followerId, Long followingId);

    void unfollowUser(Long followerId, Long followingId);

    Page<UserProfileResponse> getFollowers(Long userId, Pageable pageable);

    Page<UserProfileResponse> getFollowing(Long userId, Pageable pageable);

    boolean isFollowing(Long followerId, Long followingId);
    
    UserProfileResponse getUserProfileStats(Long targetUserId);
}
