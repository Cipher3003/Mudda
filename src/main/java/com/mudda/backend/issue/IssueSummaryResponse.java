package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record IssueSummaryResponse(
                Long id,
                String title,
                IssueStatus status,
                @JsonProperty("vote_count") Long voteCount,
                @JsonProperty("media_urls") List<String> mediaUrls,
                @JsonProperty("created_at") Instant createdAt,
                // Author
                @JsonProperty("author_id") Long userId,
                @JsonProperty("author_name") String userName,
                @JsonProperty("author_image_url") String profileImageUrl,
                // FLAGS
                @JsonProperty("has_user_voted") Boolean hasUserVoted,
                @JsonProperty("can_user_vote") Boolean canUserVote
) {
}
