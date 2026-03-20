/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueDetailProjection
 * Author  : Vikas Kumar
 * Created : 19-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

import org.locationtech.jts.geom.Point;

import java.time.Instant;

public interface IssueDetailProjection {

    Long getId();

    String getTitle();

    String getDescription();

    IssueStatus getStatus();

    IssueCategory getCategory();

    String getCity();

    String getState();

    Point getCoordinate();

    Long getVoteCount();

    Long getCommentCount();

    Double getSeverityScore();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    Instant getIssueDeletedAt();

    Long getUserId();

    String getUsername();

    String getProfileImageUrl();

    Instant getUserDeletedAt();

    Boolean getHasLiked();
}
