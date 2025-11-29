package com.mudda.backend.vote;

public record VoteSeed(
        int issueId,
        int userId
) {

    public static Vote toVote(VoteSeed seed) {
        return new Vote(
                (long) seed.issueId(),
                (long) seed.userId()
        );
    }

}
