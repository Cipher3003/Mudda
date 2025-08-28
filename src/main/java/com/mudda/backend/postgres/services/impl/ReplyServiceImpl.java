package com.mudda.backend.postgres.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mudda.backend.postgres.models.Reply;
import com.mudda.backend.postgres.repositories.ReplyRepository;
import com.mudda.backend.postgres.services.ReplyService;

@Service
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;

    public ReplyServiceImpl(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @Override
    public List<Reply> findAllReplies() {
        return replyRepository.findAll();
    }

    @Override
    public Optional<Reply> findReplyById(Long id) {
        return replyRepository.findById(id);
    }

    @Override
    public List<Reply> findReplyContainingText(String text) {
        return replyRepository.findByTextContaining(text);
    }

    @Override
    public List<Reply> findAllReplyByCommentId(Long commentId) {
        return replyRepository.findByCommentId(commentId);
    }

    @Override
    public Reply createReply(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public Reply update(Long id, String text) {
        Reply reply = replyRepository.findById(id).get();
        reply.setText(text);
        return replyRepository.save(reply);
    }

    @Override
    public void deleteReply(Long id) {
        replyRepository.deleteById(id);
    }

    @Override
    public void deleteAllRepliesByCommentId(Long commentId) {
        List<Reply> replies = replyRepository.findByCommentId(commentId);
        replyRepository.deleteAll(replies);
    }

}
