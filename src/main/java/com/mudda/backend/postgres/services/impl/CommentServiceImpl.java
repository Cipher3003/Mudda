package com.mudda.backend.postgres.services.impl;

import com.mudda.backend.postgres.models.Comment;
import com.mudda.backend.postgres.models.Reply;
import com.mudda.backend.postgres.repositories.CommentRepository;
import com.mudda.backend.postgres.services.CommentService;
import com.mudda.backend.postgres.services.ReplyService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private ReplyService replyService;

    @Override
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findByIssueId(Long issueId) {
        return commentRepository.findByIssueId(issueId);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Reply> findAllReplies(Long id) {
        return replyService.findAllReplyByCommentId(id);
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, String text) {
        Comment comment = commentRepository.findById(id).get();
        comment.setText(text);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        replyService.deleteAllRepliesByCommentId(id);
        commentRepository.deleteById(id);
    }

    @Override
    public void deleteAllCommentsByIssueId(Long issueId) {
        List<Comment> comments = commentRepository.findByIssueId(issueId);
        for (Comment comment : comments) {
            deleteComment(comment.getId());
        }
    }

}
