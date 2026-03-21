package com.mudda.backend.community.event;

import com.mudda.backend.community.Community;
import com.mudda.backend.community.CommunityRepository;
import com.mudda.backend.issue.Issue;
import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.location.Location;
import com.mudda.backend.location.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

/**
 * Listens for newly created issues and auto-tags them with the matching
 * community based on the issue's GPS coordinate falling inside a
 * community's geofence polygon.
 *
 * <p>Runs in a new transaction <strong>after</strong> the issue-creating
 * transaction commits, so a geofence lookup failure does not roll back
 * the original issue insert.</p>
 */
@Slf4j
@Component
public class CommunityAutoFilingListener {

    private final IssueRepository issueRepository;
    private final LocationRepository locationRepository;
    private final CommunityRepository communityRepository;

    public CommunityAutoFilingListener(IssueRepository issueRepository,
                                       LocationRepository locationRepository,
                                       CommunityRepository communityRepository) {
        this.issueRepository = issueRepository;
        this.locationRepository = locationRepository;
        this.communityRepository = communityRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onIssueCreated(IssueAutoFileEvent event) {
        log.debug("Auto-filing: checking issue {} for community geofence match", event.issueId());

        Optional<Location> locationOpt = locationRepository.findById(event.locationId());
        if (locationOpt.isEmpty()) {
            log.warn("Auto-filing: location {} not found for issue {}", event.locationId(), event.issueId());
            return;
        }

        Point coordinate = locationOpt.get().getCoordinate();
        double lat = coordinate.getY();
        double lon = coordinate.getX();

        List<Community> matchingCommunities =
                communityRepository.findCommunitiesContainingPoint(lat, lon);

        if (matchingCommunities.isEmpty()) {
            log.debug("Auto-filing: no community geofence contains issue {} at ({}, {})", event.issueId(), lat, lon);
            return;
        }

        // Take the first matching community (communities shouldn't overlap, but just in case)
        Community community = matchingCommunities.get(0);

        issueRepository.findById(event.issueId()).ifPresent(issue -> {
            if (issue.getCommunityId() == null) {
                issue.setCommunityId(community.getId());
                issueRepository.save(issue);
                log.info("Auto-filed issue {} into community '{}' (id={})",
                        event.issueId(), community.getName(), community.getId());
            }
        });
    }
}
