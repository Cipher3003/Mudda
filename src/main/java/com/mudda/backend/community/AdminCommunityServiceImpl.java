package com.mudda.backend.community;

import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.DashboardStatsResponse;
import com.mudda.backend.community.dto.UpdateIssueStatusRequest;
import com.mudda.backend.community.event.CommunityNotificationService;
import com.mudda.backend.initiative.InitiativeMapper;
import com.mudda.backend.initiative.InitiativeParticipantRepository;
import com.mudda.backend.initiative.InitiativeRepository;
import com.mudda.backend.initiative.dto.InitiativeResponse;
import com.mudda.backend.issue.*;
import com.mudda.backend.location.LocationDTO;
import com.mudda.backend.location.LocationMapper;
import com.mudda.backend.location.LocationRepository;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import com.mudda.backend.vote.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminCommunityServiceImpl implements AdminCommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final VoteRepository voteRepository;
    private final InitiativeRepository initiativeRepository;
    private final InitiativeParticipantRepository participantRepository;
    private final CommunityNotificationService communityNotificationService;

    public AdminCommunityServiceImpl(CommunityRepository communityRepository,
                                     CommunityMemberRepository memberRepository,
                                     IssueRepository issueRepository,
                                     UserRepository userRepository,
                                     LocationRepository locationRepository,
                                     VoteRepository voteRepository,
                                     InitiativeRepository initiativeRepository,
                                     InitiativeParticipantRepository participantRepository,
                                     CommunityNotificationService communityNotificationService) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.voteRepository = voteRepository;
        this.initiativeRepository = initiativeRepository;
        this.participantRepository = participantRepository;
        this.communityNotificationService = communityNotificationService;
    }

    // region Queries

    @Override
    @PreAuthorize("@communityAuthz.isAdmin(#communityId, authentication)")
    public DashboardStatsResponse getDashboard(Long communityId) {
        if (!communityRepository.existsById(communityId))
            throw communityNotFound(communityId);

        long verifiedCount = memberRepository.countByCommunityIdAndStatus(communityId, MemberStatus.VERIFIED);
        long pendingCount = memberRepository.countByCommunityIdAndStatus(communityId, MemberStatus.PENDING);

        // Issue stats via specifications
        long openCount = issueRepository.count(
                IssueSpecifications.hasCommunityId(communityId)
                        .and(IssueSpecifications.hasStatus(IssueStatus.OPEN))
                        .and(IssueSpecifications.isDeleted(false))
        );

        long resolvedCount = issueRepository.count(
                IssueSpecifications.hasCommunityId(communityId)
                        .and(IssueSpecifications.hasStatus(IssueStatus.RESOLVED))
                        .and(IssueSpecifications.isDeleted(false))
        );

        long totalIssues = openCount + resolvedCount;
        double resolutionRate = totalIssues > 0 ? (resolvedCount * 100.0) / totalIssues : 0.0;

        // Active initiatives
        List<InitiativeResponse> activeInitiatives = initiativeRepository
                .findByCommunityIdAndActiveTrue(communityId).stream()
                .map(initiative -> {
                    long participantCount = participantRepository.countByInitiativeId(initiative.getId());
                    return InitiativeMapper.toResponse(initiative, participantCount);
                })
                .toList();

        log.debug("Dashboard for community {}: {} residents, {} open issues, {}% resolution",
                communityId, verifiedCount, openCount, String.format("%.1f", resolutionRate));

        return new DashboardStatsResponse(
                communityId,
                verifiedCount,
                pendingCount,
                openCount,
                resolvedCount,
                resolutionRate,
                activeInitiatives.size(),
                activeInitiatives
        );
    }

    @Override
    @PreAuthorize("@communityAuthz.isAdmin(#communityId, authentication)")
    public Page<IssueResponse> getCommunityIssues(Long communityId, Pageable pageable) {
        if (!communityRepository.existsById(communityId))
            throw communityNotFound(communityId);

        Specification<Issue> spec = IssueSpecifications.hasCommunityId(communityId)
                .and(IssueSpecifications.isDeleted(false));

        Page<Issue> issuePage = issueRepository.findAll(spec, pageable);

        // Batch-load authors
        Set<Long> authorIds = issuePage.getContent().stream()
                .map(Issue::getUserId)
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> usersMap = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, u -> u));

        // Batch-load vote counts
        List<Long> issueIds = issuePage.getContent().stream()
                .map(Issue::getId)
                .toList();

        Map<Long, Long> voteCountMap = voteRepository.countByIssueIdIn(issueIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return issuePage.map(issue -> {
            MuddaUser author = usersMap.get(issue.getUserId());
            if (author == null)
                throw new IllegalStateException("User not found for issue: " + issue.getId());

            LocationDTO locationSummary = locationRepository.findById(issue.getLocationId())
                    .map(LocationMapper::toSummary)
                    .orElse(null);

            long voteCount = voteCountMap.getOrDefault(issue.getId(), 0L);

            // Admin always has full capabilities on community issues
            return IssueMapper.toResponse(
                    issue, author, locationSummary, null, voteCount,
                    false, false, false, false, false
            );
        });
    }

    // endregion

    // region Commands

    @Transactional
    @Override
    public void updateIssueStatus(Long issueId, Long adminUserId, UpdateIssueStatusRequest request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));

        issue.setStatus(request.status());

        if (request.officialResponse() != null && !request.officialResponse().isBlank()) {
            issue.setOfficialResponse(request.officialResponse().trim());
        }

        issueRepository.save(issue);
        log.info("Admin {} updated issue {} status to {} with official response: {}",
                adminUserId, issueId, request.status(),
                request.officialResponse() != null ? "yes" : "no");

        // FCM: notify reporter when their issue is resolved
        if (request.status() == IssueStatus.RESOLVED) {
            communityNotificationService.notifyIssueResolved(
                    issue.getUserId(), issue.getId(), issue.getTitle());
        }
    }

    @Transactional
    @Override
    @PreAuthorize("@communityAuthz.isAdmin(#communityId, authentication)")
    public CommunityMemberResponse verifyMember(Long communityId, Long memberId, boolean accept) {
        CommunityMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        if (!member.getCommunityId().equals(communityId))
            throw new IllegalArgumentException("Member does not belong to community: " + communityId);

        if (member.getStatus() != MemberStatus.PENDING)
            throw new IllegalStateException("Only PENDING members can be verified or rejected");

        // Fetch community name for notifications before status change
        String communityName = communityRepository.findById(communityId)
                .map(Community::getName).orElse("Community");

        if (accept) {
            member.verify();
            log.info("Admin verified member {} in community {}", memberId, communityId);
            communityNotificationService.notifyMemberVerified(member.getUserId(), communityName);
        } else {
            member.reject();
            log.info("Admin rejected member {} in community {}", memberId, communityId);
            communityNotificationService.notifyMemberRejected(member.getUserId(), communityName);
        }

        memberRepository.save(member);

        MuddaUser user = userRepository.findById(member.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + member.getUserId()));

        return CommunityMapper.toMemberResponse(member, user);
    }

    // endregion

    // region Helpers

    private EntityNotFoundException communityNotFound(Long id) {
        return new EntityNotFoundException("Community not found with id: " + id);
    }

    // endregion
}
