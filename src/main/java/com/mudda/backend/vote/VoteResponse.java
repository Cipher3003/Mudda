package com.mudda.backend.vote;

public record VoteResponse(
        Long voteCount,
        Boolean hasVoted
) {

    public static VoteResponse from(long voteCount, boolean hasVoted) {
        return new VoteResponse(
                voteCount,
                hasVoted
        );
    }
}