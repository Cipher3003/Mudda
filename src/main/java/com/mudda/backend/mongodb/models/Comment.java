package com.mudda.backend.mongodb.models;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Document(collection = "comments")
public class Comment {
    @Id
    private ObjectId commentId;

    private ObjectId issueId;

    private Long userId;

    private String text;

    private Instant createdAt = Instant.now();
}
