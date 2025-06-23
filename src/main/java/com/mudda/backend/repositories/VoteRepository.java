package com.mudda.backend.repositories;

import com.mudda.backend.models.Issue;
import com.mudda.backend.models.User;
import com.mudda.backend.models.Vote;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndIssue(User user, Issue issue);
}

