package com.mudda.backend.community;

import com.mudda.backend.community.dto.CommunityMemberResponse;
import com.mudda.backend.community.dto.CommunityResponse;
import com.mudda.backend.community.dto.CreateCommunityRequest;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository memberRepository;
    private final UserRepository userRepository;

    public CommunityServiceImpl(CommunityRepository communityRepository,
                                CommunityMemberRepository memberRepository,
                                UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    // region Queries (Read Operations)

    @Override
    public List<CommunityResponse> findNearbyCommunities(double lat, double lon, double radiusMeters) {
        log.debug("Finding nearby communities at ({}, {}) within {} m", lat, lon, radiusMeters);

        List<Community> communities = communityRepository.findNearbyCommunities(lat, lon, radiusMeters);

        // Batch-fetch resident counts in one pass
        Map<Long, Long> residentCounts = communities.stream()
                .collect(Collectors.toMap(
                        Community::getId,
                        c -> memberRepository.countByCommunityIdAndStatus(c.getId(), MemberStatus.VERIFIED)
                ));

        return communities.stream()
                .map(c -> CommunityMapper.toResponse(c, residentCounts.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    @Override
    public Optional<CommunityResponse> findById(Long communityId) {
        return communityRepository.findById(communityId)
                .map(c -> {
                    long residentCount = memberRepository.countByCommunityIdAndStatus(c.getId(), MemberStatus.VERIFIED);
                    return CommunityMapper.toResponse(c, residentCount);
                });
    }

    @Override
    public Page<CommunityMemberResponse> getMembers(Long communityId, Pageable pageable) {
        if (!communityRepository.existsById(communityId))
            throw communityNotFound(communityId);

        Page<CommunityMember> memberPage = memberRepository.findByCommunityId(communityId, pageable);

        // Batch-load users for the page
        Set<Long> userIds = memberPage.getContent().stream()
                .map(CommunityMember::getUserId)
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> usersMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, u -> u));

        return memberPage.map(member -> {
            MuddaUser user = usersMap.get(member.getUserId());
            if (user == null)
                throw new IllegalStateException("User not found for member: " + member.getId());
            return CommunityMapper.toMemberResponse(member, user);
        });
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    @Override
    public CommunityResponse createCommunity(Long ownerId, CreateCommunityRequest request) {
        if (ownerId == null)
            throw new IllegalArgumentException("Owner user ID must be provided");

        // Validate owner exists
        userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner user not found with id: " + ownerId));

        Polygon geofence = parseGeofence(request.geofenceWkt());
        Community community = new Community(request.name(), request.description(), geofence, ownerId);
        community.activate(); // new communities start ACTIVE

        Community saved = communityRepository.save(community);
        log.info("Created Community '{}' (id={}) by owner {}", saved.getName(), saved.getId(), ownerId);

        // Auto-create owner as an ADMIN member with VERIFIED status
        CommunityMember ownerMember = new CommunityMember(ownerId, saved.getId(), CommunityRole.ADMIN, MemberStatus.VERIFIED);
        memberRepository.save(ownerMember);
        log.info("Auto-created ADMIN membership for owner {} in community {}", ownerId, saved.getId());

        return CommunityMapper.toResponse(saved, 1L); // owner counts as resident
    }

    @Transactional
    @Override
    public CommunityMemberResponse joinCommunity(Long communityId, Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID must be provided");

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> communityNotFound(communityId));

        if (community.getStatus() != CommunityStatus.ACTIVE)
            throw new IllegalStateException("Cannot join an inactive community");

        // Idempotent: return existing membership if already exists
        Optional<CommunityMember> existing = memberRepository.findByUserIdAndCommunityId(userId, communityId);
        if (existing.isPresent()) {
            MuddaUser user = userRepository.findById(userId)
                    .orElseThrow(() -> userNotFound(userId));
            log.debug("User {} already has a membership in community {} — returning existing", userId, communityId);
            return CommunityMapper.toMemberResponse(existing.get(), user);
        }

        // Validate user exists
        MuddaUser user = userRepository.findById(userId)
                .orElseThrow(() -> userNotFound(userId));

        CommunityMember member = new CommunityMember(userId, communityId);
        CommunityMember saved = memberRepository.save(member);
        log.info("User {} requested to join community {} — status PENDING", userId, communityId);

        return CommunityMapper.toMemberResponse(saved, user);
    }

    // endregion

    // region Helpers

    /**
     * Parses a Well-Known Text (WKT) string into a JTS {@link Polygon} with SRID 4326.
     */
    private Polygon parseGeofence(String wkt) {
        try {
            var reader = new WKTReader();
            var geometry = reader.read(wkt);

            if (!(geometry instanceof Polygon polygon))
                throw new IllegalArgumentException("Geofence WKT must describe a Polygon, but got: " + geometry.getGeometryType());

            polygon.setSRID(4326);
            return polygon;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid WKT for geofence: " + e.getMessage(), e);
        }
    }

    private EntityNotFoundException communityNotFound(Long id) {
        return new EntityNotFoundException("Community not found with id: " + id);
    }

    private EntityNotFoundException userNotFound(Long id) {
        return new EntityNotFoundException("User not found with id: " + id);
    }

    // endregion
}
