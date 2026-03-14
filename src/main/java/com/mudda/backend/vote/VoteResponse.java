package com.mudda.backend.vote;

public record VoteResponse(
        Long voteCount,
        Boolean hasVoted
) {
    /**
     * Factory method to create VoteResponse from Vote entity
     */
    public static VoteResponse from(long voteCount, boolean hasVoted) {
        return new VoteResponse(
                voteCount,
                hasVoted
        );
    }
}