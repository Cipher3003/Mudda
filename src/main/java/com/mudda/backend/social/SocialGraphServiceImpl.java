package com.mudda.backend.social;

import com.mudda.backend.social.dto.UserProfileResponse;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SocialGraphServiceImpl implements SocialGraphService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;
    private final InteractionNotificationService interactionNotificationService;

    public SocialGraphServiceImpl(UserFollowRepository userFollowRepository,
                                  UserRepository userRepository,
                                  InteractionNotificationService interactionNotificationService) {
        this.userFollowRepository = userFollowRepository;
        this.userRepository = userRepository;
        this.interactionNotificationService = interactionNotificationService;
    }

    @Transactional
    @Override
    public void followUser(Long followerId, Long followingId) {
        if (!userRepository.existsById(followerId)) throw new EntityNotFoundException("Follower not found");
        if (!userRepository.existsById(followingId)) throw new EntityNotFoundException("Target user not found");

        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return; // Idempotent
        }

        UserFollow follow = new UserFollow(followerId, followingId);
        userFollowRepository.save(follow);
        log.info("User {} followed user {}", followerId, followingId);

        // Notify target user
        userRepository.findById(followerId).ifPresent(follower -> {
            interactionNotificationService.notifyNewFollower(followingId, follower.getUsername());
        });
    }

    @Transactional
    @Override
    public void unfollowUser(Long followerId, Long followingId) {
        userFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(follow -> {
                    userFollowRepository.delete(follow);
                    log.info("User {} unfollowed user {}", followerId, followingId);
                });
    }

    @Override
    public Page<UserProfileResponse> getFollowers(Long userId, Pageable pageable) {
        Page<UserFollow> follows = userFollowRepository.findByFollowingId(userId, pageable);
        return mapFollowsToProfiles(follows, true);
    }

    @Override
    public Page<UserProfileResponse> getFollowing(Long userId, Pageable pageable) {
        Page<UserFollow> follows = userFollowRepository.findByFollowerId(userId, pageable);
        return mapFollowsToProfiles(follows, false);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) return false;
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public UserProfileResponse getUserProfileStats(Long targetUserId) {
        MuddaUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
                
        long followers = userFollowRepository.countByFollowingId(targetUserId);
        long following = userFollowRepository.countByFollowerId(targetUserId);
        
        return new UserProfileResponse(
                targetUser.getUserId(),
                targetUser.getUsername(),
                targetUser.getName(),
                null, // Optional avatar url if muddy user gets one later
                followers,
                following
        );
    }

    private Page<UserProfileResponse> mapFollowsToProfiles(Page<UserFollow> follows, boolean isFollowersList) {
        Set<Long> userIds = follows.getContent().stream()
                .map(f -> isFollowersList ? f.getFollowerId() : f.getFollowingId())
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, u -> u));

        return follows.map(f -> {
            Long targetId = isFollowersList ? f.getFollowerId() : f.getFollowingId();
            MuddaUser u = userMap.get(targetId);
            // Assuming no avatar for now, returning count as 0 for lists to save queries, can be optimized later
            return new UserProfileResponse(
                    u.getUserId(), u.getUsername(), u.getName(), null, 0, 0
            );
        });
    }
}
