package com.mudda.backend.community.announcement;

import com.mudda.backend.community.*;
import com.mudda.backend.community.announcement.dto.AnnouncementResponse;
import com.mudda.backend.community.announcement.dto.CreateAnnouncementRequest;
import com.mudda.backend.notification.NotificationService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository,
                                   CommunityRepository communityRepository,
                                   CommunityMemberRepository memberRepository,
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        this.announcementRepository = announcementRepository;
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // region Queries

    @Override
    public Page<AnnouncementResponse> getAnnouncements(Long communityId, Pageable pageable) {
        if (!communityRepository.existsById(communityId))
            throw communityNotFound(communityId);

        Page<CommunityAnnouncement> page =
                announcementRepository.findByCommunityIdOrderByCreatedAtDesc(communityId, pageable);

        // Batch-load authors
        Set<Long> authorIds = page.getContent().stream()
                .map(CommunityAnnouncement::getAuthorId)
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> usersMap = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, u -> u));

        return page.map(a -> AnnouncementMapper.toResponse(a, usersMap.get(a.getAuthorId())));
    }

    // endregion

    // region Commands

    @Transactional
    @Override
    public AnnouncementResponse createAnnouncement(Long communityId, Long authorId, CreateAnnouncementRequest request) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> communityNotFound(communityId));

        MuddaUser author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + authorId));

        CommunityAnnouncement announcement = new CommunityAnnouncement(
                communityId, authorId, request.title(), request.body());

        CommunityAnnouncement saved = announcementRepository.save(announcement);
        log.info("Created announcement '{}' (id={}) in community '{}'",
                saved.getTitle(), saved.getId(), community.getName());

        // Fire FCM broadcast to all verified residents (async best-effort)
        broadcastToResidents(communityId, community.getName(), saved.getTitle(), saved.getBody());

        return AnnouncementMapper.toResponse(saved, author);
    }

    // endregion

    // region Helpers

    /**
     * Sends a push notification to every verified resident's FCM token.
     * Failures are logged but do not roll back the transaction.
     */
    private void broadcastToResidents(Long communityId, String communityName, String title, String body) {
        List<CommunityMember> verifiedMembers =
                memberRepository.findByCommunityIdAndStatus(communityId, MemberStatus.VERIFIED);

        Set<Long> userIds = verifiedMembers.stream()
                .map(CommunityMember::getUserId)
                .collect(Collectors.toSet());

        List<MuddaUser> users = userRepository.findAllById(userIds);

        int sent = 0;
        for (MuddaUser user : users) {
            if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
                try {
                    notificationService.sendNotification(
                            user.getFcmToken(),
                            communityName,
                            "📢 " + title,
                            body
                    );
                    sent++;
                } catch (Exception e) {
                    log.warn("Failed to send announcement notification to user {}: {}",
                            user.getUserId(), e.getMessage());
                }
            }
        }

        log.info("Announcement broadcast: sent {} notifications to {} verified residents in community {}",
                sent, verifiedMembers.size(), communityId);
    }

    private EntityNotFoundException communityNotFound(Long id) {
        return new EntityNotFoundException("Community not found with id: " + id);
    }

    // endregion
}
