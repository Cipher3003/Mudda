package com.mudda.backend.comment;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> findAllComments() {
        return commentRepository.findByParentIdIsNull();
    }

    @Override
    public List<Comment> findCommentsByIssueId(Long issueId) {
        return commentRepository.findByIssueIdAndParentIdIsNull(issueId);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAllReplies(Long parentId) {
        return commentRepository.findByParentId(parentId);
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        comment.setText(text);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if (comment.getParentId() == null) {
            commentRepository.deleteByParentId(comment.getCommentId());
        }
        commentRepository.deleteById(id);
    }

    @Override
    public void deleteAllCommentsByIssueId(Long issueId) {
        List<Comment> comments = commentRepository.findByIssueIdAndParentIdIsNull(issueId);
        for (Comment comment : comments) {
            deleteComment(comment.getIssueId());
        }
    }

}
