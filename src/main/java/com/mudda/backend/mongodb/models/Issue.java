package com.mudda.backend.mongodb.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {

    @Id
    @Field("_id")
    private String issueId; // MongoDB _id as String

    private String userName;
//    private Long userId;     // Reference to User in PostgreSQL
    private Long categoryId; // Reference to Category in PostgreSQL

    private String title;
    private String description;

    private IssueStatus status = IssueStatus.OPEN;

    private List<String> mediaUrls;

    private Location location;

    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}