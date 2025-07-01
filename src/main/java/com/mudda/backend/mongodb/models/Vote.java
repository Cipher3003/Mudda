package com.mudda.backend.mongodb.models;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "votes")
@CompoundIndexes({
        @CompoundIndex(name = "unique_vote_per_user_per_issue", def = "{'issue.$id': 1, 'userId': 1}", unique = true)
})
public class Vote {

    @Id
    private ObjectId voteId;
    // MongoDB reference to Issue document

    private ObjectId issueId;

    // Store the userId (PostgreSQL user)
    private Long userId;

    private VoteType voteType;

    private Instant createdAt = Instant.now();
}
