/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SeedData
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.seed;

import com.mudda.backend.comment.dto.CommentSeed;
import com.mudda.backend.issue.dto.IssueSeed;
import com.mudda.backend.user.dto.UserSeed;

import java.util.List;

public record SeedData(
        List<UserSeed> users,
        List<IssueSeed> issues,
        List<CommentSeed> comments
) {
}
