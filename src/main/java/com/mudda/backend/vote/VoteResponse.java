package com.mudda.backend.vote;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoteResponse(
        @JsonProperty("vote_id") Long voteId,
        @JsonProperty("issue_id") Long issueId,
        @JsonProperty("user_id") Long userId
) {
    /**
     * Factory method to create VoteResponse from Vote entity
     */
    public static VoteResponse from(Vote vote) {
        return new VoteResponse(
                vote.getVoteId(),
                vote.getIssueId(),
                vote.getUserId()
        );
    }
}