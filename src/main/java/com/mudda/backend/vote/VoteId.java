/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VoteId
 * Author  : Vikas Kumar
 * Created : 18-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.vote;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class VoteId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public VoteId(Long issueId, Long userId) {
        if (issueId == null || userId == null || issueId < 0 || userId < 0)
            throw new IllegalArgumentException("Issue Id and User Id cannot be null");

        this.issueId = issueId;
        this.userId = userId;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof VoteId that)) return false;
        return Objects.equals(issueId, that.issueId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId, userId);
    }
}
