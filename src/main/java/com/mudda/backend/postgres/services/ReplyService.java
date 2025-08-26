package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Reply;

public interface ReplyService {

    List<Reply> findAllReplies();

    Optional<Reply> findReplyById(Long id);

    List<Reply> findReplyContainingText(String text);

    List<Reply> findAllReplyByCommentId(Long commentId);

    Reply createReply(Reply reply);

    Reply update(Long id, String text);

    void deleteReply(Long id);

    void deleteAllRepliesByCommentId(Long commentId);

}
