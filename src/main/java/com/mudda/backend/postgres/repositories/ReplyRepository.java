package com.mudda.backend.postgres.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mudda.backend.postgres.models.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    public List<Reply> findByTextContaining(String text);

    public List<Reply> findByCommentId(Long commentId);

}
