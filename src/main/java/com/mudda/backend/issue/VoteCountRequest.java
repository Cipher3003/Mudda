package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VoteCountRequest(
        @JsonProperty("issue_ids") @Size(min = 1, max = 20) List<Long> issueIds
) {
}
