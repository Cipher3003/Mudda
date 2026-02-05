/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SeedData
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.seed;

import com.mudda.backend.category.CategorySeed;
import com.mudda.backend.comment.CommentSeed;
import com.mudda.backend.issue.IssueSeed;
import com.mudda.backend.location.LocationSeed;
import com.mudda.backend.user.UserSeed;

import java.util.List;

public record SeedData(
        List<UserSeed> users,
        List<LocationSeed> locations,
        List<CategorySeed> categories,
        List<IssueSeed> issues,
        List<CommentSeed> comments
) {
}
