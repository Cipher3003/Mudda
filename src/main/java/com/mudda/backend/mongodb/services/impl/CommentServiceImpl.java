package com.mudda.backend.mongodb.services.impl;

import com.mudda.backend.mongodb.models.Comment;
import com.mudda.backend.mongodb.repositories.CommentRepository;
import com.mudda.backend.mongodb.services.CommentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Optional<Comment> findById(ObjectId commentsId){
        return commentRepository.findByCommentId(commentsId);
    }

    @Override
    public Optional<Comment> findByIssue(ObjectId issueId){
        return commentRepository.findByIssueId(issueId);
    }

    @Override
    public List<Comment> findAllComments(){
        return commentRepository.findAll();
    }
}
