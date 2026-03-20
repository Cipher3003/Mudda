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
import com.mudda.backend.issue.IssueCategory;

import java.util.List;

public record IssueSeed(
        String title,
        String description,
        String category,
        int userId,
        List<String> mediaUrls,
        String pinCode,
        String city,
        String state,
        CoordinateSeed coordinate
) {

    public static Issue toIssue(IssueSeed seed) {
        return new Issue(
                seed.title(),
                seed.description(),
                (long) seed.userId(),
                IssueCategory.valueOf(seed.category()),
                seed.pinCode(),
                seed.city(),
                seed.state(),
                CoordinateSeed.toPoint(seed.coordinate())
        );
    }
}
