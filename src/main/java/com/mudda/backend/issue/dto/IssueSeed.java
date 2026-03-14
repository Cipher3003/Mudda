/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.Issue;

import java.util.List;

public record IssueSeed(
        String title,
        String description,
        int locationId,
        int categoryId,
        int userId,
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
