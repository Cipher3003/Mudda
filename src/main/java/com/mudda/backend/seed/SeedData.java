/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SeedData
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.seed;

import com.mudda.backend.comment.Comment;
import com.mudda.backend.comment.CommentLike;
import com.mudda.backend.issue.Issue;
import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.PointFactory;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.MuddaUserRole;
import com.mudda.backend.vote.Vote;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.util.List;

public record SeedData(
        List<UserSeed> users,
        List<IssueSeed> issues,
        List<CommentSeed> comments
) {
}

record VoteSeed(int issueId, int userId) {

    public static Vote toVote(VoteSeed seed) {
        return new Vote(
                (long) seed.issueId(),
                (long) seed.userId()
        );
    }

}

record CommentLikeSeed(int commentId, int userId) {

    public static CommentLike toCommentLike(CommentLikeSeed seed) {
        return new CommentLike(
                (long) seed.commentId(),
                (long) seed.userId()
        );
    }

}

record CommentSeed(String text, int issueId, int userId, Integer parentId) {

    public static Comment toComment(CommentSeed seed) {

        if (seed.parentId() == null) {
            return new Comment(
                    seed.text(),
                    (long) seed.issueId(),
                    (long) seed.userId()
            );
        }

        return new Comment(
                seed.text(),
                seed.parentId().longValue(),
                (long) seed.issueId(),
                (long) seed.userId()
        );
    }
}

record CoordinateSeed(double y, double x) {

    public static Point toPoint(CoordinateSeed seed) {
        return PointFactory.createPoint(new Coordinate(seed.x(), seed.y()));
    }
}

record UserSeed(
        String username,
        String name,
        String email,
        String dateOfBirth,
        String phoneNumber,
        String password,
        MuddaUserRole role,
        String profileImageUrl
) {

    public static MuddaUser toUser(UserSeed seed) {
        return new MuddaUser(
                seed.username(),
                seed.name(),
                seed.phoneNumber(),
                LocalDate.parse(seed.dateOfBirth()),
                seed.email(),
                seed.password(),
                seed.profileImageUrl(),
                seed.role()
        );
    }
}

record IssueSeed(
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
