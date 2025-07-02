package com.mudda.backend.mongodb.repositories;

import java.util.Optional;

import com.mudda.backend.mongodb.models.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    Optional<Comment> findByIssueId(ObjectId issueId);
    Optional<Comment> findByCommentId(ObjectId commentsId);
}
