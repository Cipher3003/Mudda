package com.mudda.backend.vote;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoteResponse(
        @JsonProperty("vote_count") Long voteCount,
        @JsonProperty("has_user_voted") Boolean hasUserVoted
) {
    /**
     * Factory method to create VoteResponse from Vote entity
     */
    public static VoteResponse from(long voteCount, boolean hasUserVoted) {
        return new VoteResponse(
                voteCount,
                hasUserVoted
        );
    }
}