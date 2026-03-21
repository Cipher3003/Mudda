package com.mudda.backend.community.event;

/**
 * Spring Application Event published after a new issue is persisted.
 * The {@link CommunityAutoFilingListener} picks this up and tags the
 * issue with its geographically matching community (if any).
 *
 * <p>This is separate from the SQS-based {@code IssueCreatedEvent} used
 * for hate-speech analysis — keeping the two concerns decoupled.</p>
 */
public record IssueAutoFileEvent(
        Long issueId,
        Long locationId
) {}
