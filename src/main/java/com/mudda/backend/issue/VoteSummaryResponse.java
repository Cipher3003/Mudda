package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoteSummaryResponse(
        @JsonProperty("issue_id") Long issueId,
        @JsonProperty("vote_count") Long voteCount
) {
}
