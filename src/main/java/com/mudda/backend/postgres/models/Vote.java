package com.mudda.backend.postgres.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "votes")
// @CompoundIndexes({
// @CompoundIndex(name = "unique_vote_per_user_per_issue", def = "{'issue.$id':
// 1, 'userId': 1}", unique = true)
// })
// README: Could be removed for direct join table if hard linked
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @Column(nullable = false)
    private Long issueId; // soft link to issue that has this vote

    @Column(nullable = false)
    private Long userId; // soft link to user that has made this vote

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
