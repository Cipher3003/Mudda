package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.location.LocationDTO;

import java.time.Instant;
import java.util.List;

public record IssueResponse(
                Long id,
                String title,
                String description,
                IssueStatus status,
                LocationDTO locationSummary,
                String category,
                @JsonProperty("vote_count") Long voteCount,
                @JsonProperty("media_urls") List<String> mediaUrls,
                @JsonProperty("severity_score") double severityScore,
                @JsonProperty("created_at") Instant createdAt,
                @JsonProperty("updated_at") Instant updatedAt,
                // Author
                @JsonProperty("author_id") Long userId,
                @JsonProperty("author_name") String userName,
                @JsonProperty("author_image_url") String profileImageUrl,
                // FLAGS
                @JsonProperty("has_user_voted") Boolean hasUserVoted,
                @JsonProperty("can_user_vote") Boolean canUserVote,
                @JsonProperty("can_user_comment") Boolean canUserComment,
                @JsonProperty("can_user_edit") Boolean canUserEdit,
                @JsonProperty("can_user_delete") Boolean canUserDelete
) {
}
