/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CommentSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment;

public record CommentSeed(
        String text,
        int issueId,
        int userId,
        Integer parentId) {

    public static Comment toComment(CommentSeed seed) {
        if (seed.parentId() == null) {
            return new Comment(
                    seed.text(),
                    (long) seed.issueId(),
                    (long) seed.userId());
        }
        return new Comment(
                seed.text(),
                seed.parentId().longValue(),
                (long) seed.issueId(),
                (long) seed.userId());
    }
}
