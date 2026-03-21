package com.mudda.backend.initiative;

import com.mudda.backend.community.Community;
import com.mudda.backend.community.CommunityRepository;
import com.mudda.backend.initiative.dto.CreateInitiativeRequest;
import com.mudda.backend.initiative.dto.HubResponse;
import com.mudda.backend.initiative.dto.InitiativeResponse;
import com.mudda.backend.initiative.dto.ParticipateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class InitiativeServiceImpl implements InitiativeService {

    private final InitiativeRepository initiativeRepository;
    private final InitiativeParticipantRepository participantRepository;
    private final CommunityRepository communityRepository;

    public InitiativeServiceImpl(InitiativeRepository initiativeRepository,
                                 InitiativeParticipantRepository participantRepository,
                                 CommunityRepository communityRepository) {
        this.initiativeRepository = initiativeRepository;
        this.participantRepository = participantRepository;
        this.communityRepository = communityRepository;
    }

    // region Queries

    @Override
    public HubResponse getCommunityHub(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> communityNotFound(communityId));

        List<CommunityInitiative> activeInitiatives =
                initiativeRepository.findByCommunityIdAndActiveTrue(communityId);

        List<InitiativeResponse> responses = activeInitiatives.stream()
                .map(initiative -> {
                    long participantCount = participantRepository.countByInitiativeId(initiative.getId());
                    return InitiativeMapper.toResponse(initiative, participantCount);
                })
                .toList();

        long totalParticipants = responses.stream()
                .mapToLong(InitiativeResponse::participantCount)
                .sum();

        return new HubResponse(
                community.getId(),
                community.getName(),
                activeInitiatives.size(),
                totalParticipants,
                responses
        );
    }

    // endregion

    // region Commands

    @Transactional
    @Override
    public InitiativeResponse createInitiative(Long communityId, CreateInitiativeRequest request) {
        if (!communityRepository.existsById(communityId))
            throw communityNotFound(communityId);

        CommunityInitiative initiative = new CommunityInitiative(
                communityId,
                request.title(),
                request.description(),
                request.type(),
                request.targetMetric(),
                request.eventDate()
        );

        CommunityInitiative saved = initiativeRepository.save(initiative);
        log.info("Created initiative '{}' (id={}) for community {}", saved.getTitle(), saved.getId(), communityId);

        return InitiativeMapper.toResponse(saved, 0L);
    }

    @Transactional
    @Override
    public InitiativeResponse participate(Long initiativeId, Long userId, ParticipateRequest request) {
        if (userId == null)
            throw new IllegalArgumentException("User ID must be provided");

        CommunityInitiative initiative = initiativeRepository.findById(initiativeId)
                .orElseThrow(() -> new EntityNotFoundException("Initiative not found with id: " + initiativeId));

        if (!initiative.isActive())
            throw new IllegalStateException("Cannot participate in an inactive initiative");

        // Idempotent: if already participating, return current state
        if (participantRepository.existsByUserIdAndInitiativeId(userId, initiativeId)) {
            log.debug("User {} already participating in initiative {} — returning current state", userId, initiativeId);
            long participantCount = participantRepository.countByInitiativeId(initiativeId);
            return InitiativeMapper.toResponse(initiative, participantCount);
        }

        InitiativeParticipant participant = new InitiativeParticipant(userId, initiativeId, request.pledgedMetric());
        participantRepository.save(participant);

        // Update the initiative's current metric
        initiative.addProgress(request.pledgedMetric());
        initiativeRepository.save(initiative);

        log.info("User {} pledged {} to initiative {} (now at {}/{})",
                userId, request.pledgedMetric(), initiativeId,
                initiative.getCurrentMetric(), initiative.getTargetMetric());

        long participantCount = participantRepository.countByInitiativeId(initiativeId);
        return InitiativeMapper.toResponse(initiative, participantCount);
    }

    // endregion

    // region Helpers

    private EntityNotFoundException communityNotFound(Long id) {
        return new EntityNotFoundException("Community not found with id: " + id);
    }

    // endregion
}
