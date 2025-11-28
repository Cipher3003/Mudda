/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record IssueSeed(
        String title,
        String description,
        @SerializedName("location_id")
        int locationId,
        @SerializedName("category_id")
        int categoryId,
        @SerializedName("user_id")
        int userId,
        @SerializedName("media_urls")
        List<String> mediaUrls
) {

    public static Issue toIssue(IssueSeed seed) {
        return new Issue(
                seed.title(),
                seed.description(),
                (long) seed.userId(),
                (long) seed.locationId(),
                (long) seed.categoryId(),
                seed.mediaUrls()
        );
    }
}
