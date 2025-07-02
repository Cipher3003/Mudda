package com.mudda.backend.mongodb.services;

import com.mudda.backend.mongodb.models.Comment;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;


public interface CommentService {
    Optional<Comment> findByIssue(ObjectId issueId);
    Optional<Comment> findById(ObjectId commentId);
    List<Comment> findAllComments();
}
